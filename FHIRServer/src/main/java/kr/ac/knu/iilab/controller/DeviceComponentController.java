package kr.ac.knu.iilab.controller;

import org.hl7.fhir.dstu3.model.DeviceComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.knu.iilab.Utils;
import kr.ac.knu.iilab.model.DeviceComponentEntity;
import kr.ac.knu.iilab.model.PatientEntity;
import kr.ac.knu.iilab.repository.DeviceComponentEntityRepository;

@RestController
public class DeviceComponentController {

	@Autowired
	private DeviceComponentEntityRepository deviceComponentEntityRepository;
	
	@PostMapping(value="DeviceComponent")
	public String registerDeviceComponent(
			@RequestBody String deviceComponentResource) {

		DeviceComponentEntity deviceComponentEntity = new DeviceComponentEntity();
		
		DeviceComponent deviceComponent = (DeviceComponent) Utils.xmlParser.parseResource(deviceComponentResource);
		deviceComponentEntity.setDeviceComponentResourceStr(deviceComponentResource);
		
		deviceComponentEntityRepository.save(deviceComponentEntity);
		
		deviceComponent.setId(deviceComponentEntity.getId() + "");
		
		return Utils.resourceToXmlString((Resource) deviceComponent);
	}
}
