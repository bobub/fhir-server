package kr.ac.knu.iilab.controller;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.DeviceComponent;
import org.hl7.fhir.dstu3.model.DeviceMetric;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
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
		
		for(int i=0; i<bundle.getEntry().size(); i++) {
			BundleEntryComponent entry = bundle.getEntry().get(i);
			Resource resource = bundle.getEntry().get(i).getResource();
			
			switch ( resource.getResourceType() ) {
			case Patient:
				Patient patient = (Patient) resource;
				String patientId = patient.getIdElement().getIdPart();
				
				PatientEntity patientEntity = new PatientEntity();
				patientEntity.setPatientId(patientId);
				patientEntity.setPatientResourceStr(parser.encodeResourceToString(patient));

				patientEntityRepository.save(patientEntity);
				entry.setFullUrl("Patient/" + patientEntity.getId());
				
				break;
			case DeviceComponent:
				DeviceComponent deviceComponent = (DeviceComponent) resource;
				String deviceComponentId = deviceComponent.getIdElement().getIdPart();

				DeviceComponentEntity deviceComponentEntity = new DeviceComponentEntity();
				deviceComponentEntity.setDeviceComponentId(deviceComponentId);
				deviceComponentEntity.setDeviceComponentResourceStr(parser.encodeResourceToString(deviceComponent));
				deviceComponentEntity.setParentReference(deviceComponent.getParent().getReference());
				
				deviceComponentEntityRepository.save(deviceComponentEntity);
				entry.setFullUrl("DeviceComponent/" + deviceComponentEntity.getId());
				
				break;
			case Device:	// delete
				Device device = (Device) resource;

				DeviceEntity deviceEntity = new DeviceEntity();
				deviceEntity.setDeviceId(device.getIdElement().getIdPart());
				deviceEntity.setDeviceResourceStr(parser.encodeResourceToString(device));
				deviceEntity.setParentReference(device.getPatient().getReference());
				
				deviceEntityRepository.save(deviceEntity);
				entry.setFullUrl("Device/" + deviceEntity.getId());
				
				break;
			case Observation:
				Observation observation = (Observation) resource;

				ObservationEntity observationEntity = new ObservationEntity();
				observationEntity.setObservationId(observation.getIdElement().getIdPart());
				observationEntity.setObservationResourceStr(parser.encodeResourceToString(observation));
				observationEntity.setDeviceReference(observation.getDevice().getReference());
				
				List<String> list = new ArrayList<String>();
				for(int j=0; j<observation.getPerformer().size(); j++) {
					list.add(observation.getPerformer().get(j).getReference());
				}
				
				observationEntity.setPerformerReference(gson.toJson(list));
				observationEntity.setSubjectReference(observation.getSubject().getReference());
				
				observationEntityRepository.save(observationEntity);
				entry.setFullUrl("Observation/" + observationEntity.getId());
				
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
