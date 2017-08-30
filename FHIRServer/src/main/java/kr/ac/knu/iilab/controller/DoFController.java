package kr.ac.knu.iilab.controller;

import static org.mockito.Matchers.endsWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle.HTTPVerb;
import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.DeviceComponent;
import org.hl7.fhir.dstu3.model.DeviceMetric;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.codesystems.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import kr.ac.knu.iilab.StringList;
import kr.ac.knu.iilab.Utils;
import kr.ac.knu.iilab.model.DeviceComponentEntity;
import kr.ac.knu.iilab.model.DeviceEntity;
import kr.ac.knu.iilab.model.ObservationEntity;
import kr.ac.knu.iilab.model.PatientEntity;
import kr.ac.knu.iilab.repository.DeviceComponentEntityRepository;
import kr.ac.knu.iilab.repository.DeviceEntityRepository;
import kr.ac.knu.iilab.repository.ObservationEntityRepository;
import kr.ac.knu.iilab.repository.PatientEntityRepository;

@RestController
public class DoFController {
	
	@Autowired
	private PatientEntityRepository patientEntityRepository;
	
	@Autowired
	private DeviceComponentEntityRepository deviceComponentEntityRepository;
	
	@Autowired
	private DeviceEntityRepository deviceEntityRepository;
	
	@Autowired
	private ObservationEntityRepository observationEntityRepository;
	
	private Gson gson = new Gson();
	
	/**
	 * Device on FHIR
	 * @param bundleMessage
	 * @return
	 */
	@PostMapping(value="/fhir")
	private String post(
			@RequestHeader(value="Content-Type", required=true) String contentType,
			@RequestBody(required=true) String bundleMessage) {

		IParser parser = null;
		
		// header checker
		if( contentType.equals("application/fhir+xml") ) {
			parser = Utils.xmlParser;
		}
		else {
			return "Content-Type must be 'application/fhir+xml'";
		}
		
		Bundle bundle = parser.parseResource(Bundle.class, bundleMessage);

		//
		// TODO: Bundle Type Check is required. must be 'transaction'.
		//
		
		// if bundle.type is 'transaction',  
		bundle.setType(BundleType.TRANSACTIONRESPONSE);
		
		//
		// Temporary Code ... for Plug-thon
		for(int i=0; i<bundle.getEntry().size(); i++) {
			BundleEntryComponent entry = bundle.getEntry().get(i);
			Resource resource = bundle.getEntry().get(i).getResource();
			
			switch( resource.getResourceType() ) {
			case Patient:
			case Device:
			case DeviceComponent:
				/*
				if( entry.getRequest().getMethod().compareTo(HTTPVerb.PUT) != 0 ) {
					return "Patient, Device, DeviceComponent Resource must have Request with 'PUT' method";    
				}
				break;
				*/
			case Observation: 
				if( entry.getRequest().getMethod().compareTo(HTTPVerb.POST) != 0 ) {
					return "Patient, Device, DeviceComponent, Observation Resource must have Request with 'POST' method";    
				}
				break;
			default:
				return "DoF Resource must be contain 'Patient', 'Device', 'DeviceComponent', 'Observation'";
			}
		}
		
		/*
		Patient
		Device 
		-------- Patient

		DeviceComponent
		-------- Device

		Observation
		-------- Patient
		-------- Device
		*/
		
		String patientReference = "none";
		String deviceReference = "none";
		
		// Patient
		for(int i=0; i<bundle.getEntry().size(); i++) {
			BundleEntryComponent entry = bundle.getEntry().get(i);
			Resource resource = bundle.getEntry().get(i).getResource();

			int j;
			if( resource.getResourceType() == ResourceType.Patient) { 
				Patient patient = (Patient) resource;
				
				PatientEntity patientEntity = new PatientEntity();
				patientEntity.setGiven(patient.getName().get(0).getGiven().get(0).getValue());
				patientEntity.setPatientResourceStr(parser.encodeResourceToString(patient));

				patientEntityRepository.save(patientEntity);
				
				patientReference = "Patient/" + patientEntity.getId(); 
				entry.setFullUrl(patientReference);
				patient.setId(patientEntity.getId() + "");
			}
		}
		

		// Device
		for(int i=0; i<bundle.getEntry().size(); i++) {
			BundleEntryComponent entry = bundle.getEntry().get(i);
			Resource resource = bundle.getEntry().get(i).getResource();

			int j;
			if( resource.getResourceType() == ResourceType.Device) {
				Device device = (Device) resource;
				
				/*
				List<DeviceEntity> dList = deviceEntityRepository.findByDeviceId(deviceId);
				for(j=0; j<dList.size(); j++) {
					if( dList.get(j).getDeviceId().equals(deviceId) ) {
						break;
					}
				}
				if(j == dList.size()) {
					return deviceId + " is not exist.";
				}
				System.out.println(">> " + deviceId + " is exist.");
				*/
				DeviceEntity deviceEntity = new DeviceEntity();
				deviceEntity.setDeviceResourceStr(parser.encodeResourceToString(device));
				deviceEntity.setParentReference(device.getPatient().getReference());
				
				deviceEntityRepository.save(deviceEntity);
				
				deviceReference = "Device/" + deviceEntity.getId(); 
				entry.setFullUrl(deviceReference);
				
				device.setId(deviceEntity.getId() + "");
				device.setPatient(new Reference(patientReference));
			}
		}

		// DeviceComponent
		for(int i=0; i<bundle.getEntry().size(); i++) {
			BundleEntryComponent entry = bundle.getEntry().get(i);
			Resource resource = bundle.getEntry().get(i).getResource();

			int j;
			if( resource.getResourceType() == ResourceType.DeviceComponent) { 
				/*
				DeviceComponent deviceComponent = (DeviceComponent) resource;
				String deviceComponentId = deviceComponent.getIdElement().getIdPart();
				
				// TODO: history ..
				List<DeviceComponentEntity> dcList = deviceComponentEntityRepository.findByDeviceComponentId(deviceComponentId);
				for(j=0; j<dcList.size(); j++) {
					if( dcList.get(j).getDeviceComponentId().equals(deviceComponentId) ) {
						break;
					}
				}
				if(j == dcList.size()) {
					return deviceComponentId + " is not exist.";
				}
				System.out.println(">> " + deviceComponentId + " is exist.");
				 */
				
				DeviceComponent deviceComponent = (DeviceComponent) resource;
			//	String deviceComponentId = deviceComponent.getIdElement().getIdPart();
				
				DeviceComponentEntity deviceComponentEntity = new DeviceComponentEntity();
			//	deviceComponentEntity.setDeviceComponentId(deviceComponentId);
				deviceComponentEntity.setDeviceComponentResourceStr(parser.encodeResourceToString(deviceComponent));
				deviceComponentEntity.setParentReference(deviceComponent.getParent().getReference());
				
				deviceComponentEntityRepository.save(deviceComponentEntity);
				
				entry.setFullUrl("DeviceComponent/" + deviceComponentEntity.getId());
				
				deviceComponent.setId(deviceComponentEntity.getId() + "");
				deviceComponent.setSource(new Reference(deviceReference));
			}
		}

		// Observation
		for(int i=0; i<bundle.getEntry().size(); i++) {
			BundleEntryComponent entry = bundle.getEntry().get(i);
			Resource resource = bundle.getEntry().get(i).getResource();

			int j;
			if( resource.getResourceType() == ResourceType.Observation) {
				Observation observation = (Observation) resource;

				ObservationEntity observationEntity = new ObservationEntity();
				observationEntity.setObservationId(observation.getIdElement().getIdPart());
				observationEntity.setObservationResourceStr(parser.encodeResourceToString(observation));
				observationEntity.setDeviceReference(deviceReference);
				observationEntity.setSubjectReference(patientReference);
				
				List<String> list = new ArrayList<String>();
				for(j=0; j<observation.getPerformer().size(); j++) {
					list.add(observation.getPerformer().get(j).getReference());
				}
				
				observationEntity.setPerformerReference(gson.toJson(list));
				
				observationEntityRepository.save(observationEntity);
				entry.setFullUrl("Observation/" + observationEntity.getId());
				observation.setId(observationEntity.getId() + "");
				
				observation.setPerformer(Arrays.asList(new Reference(patientReference)));
				observation.setSubject(new Reference(patientReference));
				observation.setDevice(new Reference(deviceReference));
			}
		}
		
		// no operation
		for(int i=0; i<bundle.getEntry().size(); i++) {
			BundleEntryComponent entry = bundle.getEntry().get(i);
			Resource resource = bundle.getEntry().get(i).getResource();

			int j;
			
			switch ( resource.getResourceType() ) {
			case Patient:	// only if "PUT"
				 
				/*
				Patient patient = (Patient) resource;
				String patientId = patient.getIdElement().getIdPart();
				
				//
				// TODO: history 구현 안됨 
				//
				if( patientEntityRepository.findByPatientId(patientId) == null ) {
					return patientId + " is not exist.";
				}
				System.out.println(">> " + patientId + " is exist.");
				*/
				
				break;
				
			case DeviceComponent:
				
				break;
				
			case Device:
				
				break;
			case Observation:
				
				break;
				
			default:
				// NOT Patient, DeviceComponent, DeviceMetric, Observation
				break;
			}
		}
		
		return Utils.resourceToXmlString(bundle);
	}
	
	@PostMapping(value="/fhir/xml")
	public String fhirtest(@RequestBody String bundleMessage) {
		FhirContext.forDstu3().newXmlParser().parseResource(Bundle.class, bundleMessage);
		Observation ob = new Observation();
		
		return "fhir-xml";
	}
}
