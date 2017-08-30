package kr.ac.knu.iilab.controller;

import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.uhn.fhir.context.FhirContext;
import kr.ac.knu.iilab.SampleModelEntity;
import kr.ac.knu.iilab.SampleModelEntityRepository;
import kr.ac.knu.iilab.Utils;
import kr.ac.knu.iilab.model.PatientEntity;
import kr.ac.knu.iilab.repository.PatientEntityRepository;

@RestController
public class PatientController {

	@Autowired
	private PatientEntityRepository patientEntityRepository;
	
	/**
	 * 
	 * @param patientResource
	 * @return
	 */
	@PostMapping(value="Patient")
	public String registerPatient(
			@RequestBody(required=true) String patientResource) {
		
		PatientEntity patientEntity = new PatientEntity();
		
		Patient patient = (Patient) Utils.xmlParser.parseResource(patientResource);
		patientEntity.setGiven(patient.getName().get(0).getGiven().get(0).getValue());
		patientEntity.setPatientResourceStr(patientResource);
		
		patientEntityRepository.save(patientEntity);
		
		patient.setId(patientEntity.getId() + "");
		
		return Utils.resourceToXmlString((Resource) patient);
	}
	
	/**
	 * GET [base]/Patient?_id=23
	 * @param _id
	 * @return	
	 */
	@GetMapping(value="/Patient")
	public String search(@RequestParam(value="_id") String _id) {
		
		PatientEntity pe = patientEntityRepository.findByPatientId(_id);
		if( pe == null ) {
			return "Patient is not exist: " + _id;
		}
		
		return pe.getPatientResourceStr();
		/*
		List<PatientEntity> patientEntityList = patientEntityRepository.findByPatientId(_id);
		
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.SEARCHSET);
		for(int i=0; i<patientEntityList.size(); i++) {
			BundleEntryComponent component = new BundleEntryComponent();
			component.setResource(Utils.jsonParser.parseResource(Patient.class, patientEntityList.get(i).getPatientResourceStr()));
			bundle.addEntry(component);
		}
		return Utils.jsonParser.encodeResourceToString(bundle);
		*/
	}
	
	@GetMapping(value="Patient/{_id}")
	public String searchPatientById(
			@PathVariable("_id") String _id) {
		PatientEntity patientEntity = patientEntityRepository.findByPatientId(_id);
		
		return patientEntity.getPatientResourceStr();
	}
	
	@GetMapping(value="/test")
	public String test(@RequestParam(value="name", defaultValue="world") String name) {
		return "test " + name;
	} 
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	@Autowired
	private SampleModelEntityRepository sampleModelEntityRepository;

	@GetMapping(path = "/Patient/add") // Map ONLY GET Requests
	public @ResponseBody String addNewUser(@RequestParam String name, @RequestParam String email) {

		SampleModelEntity n = new SampleModelEntity();
		n.setName(name);
		n.setEmail(email);
		sampleModelEntityRepository.save(n);
		return "Saved";
	}

	@GetMapping(path = "/Patient/all")
	public @ResponseBody Iterable<SampleModelEntity> getAllUsers() {
		// This returns a JSON or XML with the users
		return sampleModelEntityRepository.findAll();
	}
}
