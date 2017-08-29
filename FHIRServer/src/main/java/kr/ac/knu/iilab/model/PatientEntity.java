package kr.ac.knu.iilab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class PatientEntity {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

	@Column(columnDefinition="TEXT")
	private String patientId;
	
	@Column(columnDefinition="TEXT")
    private String patientResourceStr;
}
