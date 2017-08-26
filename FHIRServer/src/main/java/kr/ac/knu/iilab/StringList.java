package kr.ac.knu.iilab;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class StringList {
	List<String> strList;
	
	public StringList() {
		this.strList = new ArrayList<String>(); 
	}
}
