package kr.ac.knu.iilab.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import kr.ac.knu.iilab.model.ObservationEntity;
import kr.ac.knu.iilab.model.PatientEntity;

public interface ObservationEntityRepository extends CrudRepository<ObservationEntity, Long> {
	List<ObservationEntity> findByObservationId(String observationId);
}
