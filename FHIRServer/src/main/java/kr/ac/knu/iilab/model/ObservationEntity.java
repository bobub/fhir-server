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
public class ObservationEntity {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

	@Column(columnDefinition="TEXT")
	private String observationId;
	
	@Column(columnDefinition="TEXT")
    private String observationResourceStr;

	@Column(columnDefinition="TEXT")
	private String deviceReference;

	@Column(columnDefinition="TEXT")
	private String performerReference;
	
	@Column(columnDefinition="TEXT")
	private String subjectReference;
}
