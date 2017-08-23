package kr.ac.knu.iilabcom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {

	/**
	 * GET [base]/Patient?_id=23
	 * @param _id
	 * @return	
	 */
	@GetMapping(value="/Patient")
	public String search(@RequestParam(value="_id") int _id) {
		return "search : " + _id;
	}
	
	@GetMapping(value="/test")
	public String test(@RequestParam(value="name", defaultValue="world") String name) {
		return "test " + name;
	} 
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	@Autowired
	private SampleModelEntityRepository sampleModelEntityRepository;

	@GetMapping(path = "/Patient/add") // Map ONLY GET Requests
	public @ResponseBody String addNewUser(@RequestParam String name, @RequestParam String email) {

		SampleModelEntity n = new SampleModelEntity();
		n.setName(name);
		n.setEmail(email);
		sampleModelEntityRepository.save(n);
		return "Saved";
	}

	@GetMapping(path = "/Patient/all")
	public @ResponseBody Iterable<SampleModelEntity> getAllUsers() {
		// This returns a JSON or XML with the users
		return sampleModelEntityRepository.findAll();
	}
}
