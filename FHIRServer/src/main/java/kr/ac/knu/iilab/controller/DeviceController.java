package kr.ac.knu.iilab.controller;

import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.DeviceComponent;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.knu.iilab.Utils;
import kr.ac.knu.iilab.model.DeviceComponentEntity;
import kr.ac.knu.iilab.model.DeviceEntity;
import kr.ac.knu.iilab.repository.DeviceEntityRepository;

@RestController
public class DeviceController {

	@Autowired
	private DeviceEntityRepository deviceEntityRepository;
	
	@PostMapping(value="Device")
	public String registerDevice(
			@RequestBody String deviceResource) {

		DeviceEntity deviceEntity = new DeviceEntity();
		
		Device device = (Device) Utils.xmlParser.parseResource(deviceResource);
		deviceEntity.setDeviceResourceStr(deviceResource);
		
		deviceEntityRepository.save(deviceEntity);
		
		device.setId(deviceEntity.getId() + "");
		
		return Utils.resourceToXmlString((Resource) device);
	}
}
