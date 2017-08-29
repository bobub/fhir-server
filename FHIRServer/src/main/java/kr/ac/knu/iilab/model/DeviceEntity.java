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
public class DeviceEntity {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

	@Column(columnDefinition="TEXT")
	private String deviceId;
	
	@Column(columnDefinition="TEXT")
    private String deviceResourceStr;
	
	@Column(columnDefinition="TEXT")
	private String parentReference;
}
