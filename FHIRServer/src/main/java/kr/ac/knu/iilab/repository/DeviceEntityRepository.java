package kr.ac.knu.iilab.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import kr.ac.knu.iilab.model.DeviceEntity;
import kr.ac.knu.iilab.model.PatientEntity;

public interface DeviceEntityRepository extends CrudRepository<DeviceEntity, Long> {
	List<DeviceEntity> findByDeviceId(String deviceId);
}
