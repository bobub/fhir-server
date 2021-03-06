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
public class DeviceComponentEntity {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

	@Column(columnDefinition="TEXT")
	private String deviceComponentId;
	
	@Column(columnDefinition="TEXT")
    private String deviceComponentResourceStr;
	
	@Column(columnDefinition="TEXT")
	private String parentReference;
}
