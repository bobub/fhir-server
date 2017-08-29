package kr.ac.knu.iilab.controller;

import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.knu.iilab.Utils;
import kr.ac.knu.iilab.model.ObservationEntity;
import kr.ac.knu.iilab.repository.ObservationEntityRepository;

@RestController
public class ObservationController {

	@Autowired
	private ObservationEntityRepository observationEntityRepository;
	
	/**
	 * /Observation?subject=Patient/23
	 * @return
	 */
	@GetMapping(value="/Observation")
	public String searchObservation(
			@RequestParam(value="subject") String subjectReference) {
		
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.SEARCHSET);
		
		List<ObservationEntity> obs = observationEntityRepository.findBySubjectReference(subjectReference);
		for(int i=0; i<obs.size(); i++) {
			BundleEntryComponent component = new BundleEntryComponent();
			component.setResource(Utils.xmlParser.parseResource(Observation.class, obs.get(i).getObservationResourceStr()));
			bundle.addEntry(component);
		}
		
		return Utils.resourceToXmlString(bundle);
	}
}
