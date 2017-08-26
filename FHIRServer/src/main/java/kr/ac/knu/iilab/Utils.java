package kr.ac.knu.iilab;

import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

public class Utils {

	public static IParser jsonParser = FhirContext.forDstu3().newJsonParser();
	
	public static String resourceToString(IBaseResource resource) {		
		return FhirContext.forDstu3().newJsonParser().encodeResourceToString(resource);
	}
	
	public static Resource stringToResource(String resourceStr) {
		return FhirContext.forDstu3().newJsonParser().parseResource(Resource.class, resourceStr);
	}

}
