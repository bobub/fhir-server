package kr.ac.knu.iilab.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import kr.ac.knu.iilab.model.PatientEntity;

public interface PatientEntityRepository extends CrudRepository<PatientEntity, Long> {
	List<PatientEntity> findByPatientId(String patientId);
}
