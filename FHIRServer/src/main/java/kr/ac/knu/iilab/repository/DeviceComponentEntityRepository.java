package kr.ac.knu.iilab.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import kr.ac.knu.iilab.model.DeviceComponentEntity;
import kr.ac.knu.iilab.model.PatientEntity;

public interface DeviceComponentEntityRepository extends CrudRepository<DeviceComponentEntity, Long> {
	List<DeviceComponentEntity> findByDeviceComponentId(String deviceComponentId);
}
