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
public class DeviceMetricEntity {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

	@Column(columnDefinition="TEXT")
	private String deviceMetricId;
	
	@Column(columnDefinition="TEXT")
    private String deviceMetricResourceStr;
	
	@Column(columnDefinition="TEXT")
	private String parentReference;
}