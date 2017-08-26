package kr.ac.knu.iilab.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import kr.ac.knu.iilab.model.DeviceMetricEntity;
import kr.ac.knu.iilab.model.PatientEntity;

public interface DeviceMetricEntityRepository extends CrudRepository<DeviceMetricEntity, Long> {
	List<DeviceMetricEntity> findByDeviceMetricId(String deviceMetricId);
}
