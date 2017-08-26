package kr.ac.knu.iilab.controller;

import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
	 * GET [base]/Patient?_id=23
	 * @param _id
	 * @return	
	 */
	@GetMapping(value="/Patient")
	public String search(@RequestParam(value="_id") String _id) {
		List<PatientEntity> patientEntityList = patientEntityRepository.findByPatientId(_id);
		
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.SEARCHSET);
		for(int i=0; i<patientEntityList.size(); i++) {
			BundleEntryComponent component = new BundleEntryComponent();
			component.setResource(Utils.jsonParser.parseResource(Patient.class, patientEntityList.get(i).getPatientResourceStr()));
			bundle.addEntry(component);
		}
		
		return Utils.jsonParser.encodeResourceToString(bundle);
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