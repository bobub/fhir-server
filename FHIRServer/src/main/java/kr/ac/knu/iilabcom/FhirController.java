package kr.ac.knu.iilabcom;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.uhn.fhir.context.FhirContext;

@RestController
public class FhirController {
	
	private List<Bundle> bundleList = new ArrayList<>();
	private List<Patient> patientList = new ArrayList<>();
	
	@PostMapping(value="/fhir")
	private String post(@RequestBody String bundleMessage) {
		//Bundle bundle = FhirContext.forDstu3().newXmlParser().parseResource(Bundle.class, bundleMessage);
		Bundle bundle = FhirContext.forDstu3().newJsonParser().parseResource(Bundle.class, bundleMessage);
		Patient patient = (Patient) bundle.getEntry().get(0).getResource();
		
		// temporal code
		bundleList.add(bundle);
		patientList.add(patient);
		
		return "[" + bundleList.size() + "] " + patient.getName().get(0).getFamily() + patient.getName().get(0).getGivenAsSingleString();
	}
	
	@GetMapping(value="/view")
	private String view() {
		StringBuffer strBuf = new StringBuffer();
		for(int i=bundleList.size()-1; i>=0; i--) {
			strBuf.append((i+1) + "\t");
			strBuf.append(patientList.get(i).getName().get(0).getFamily());
			strBuf.append(patientList.get(i).getName().get(0).getGivenAsSingleString());
			strBuf.append("<br>");
		}
		return strBuf.toString();
	}
}
