package distribucion;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class XMLValidator {
	
	
	public boolean validate(String xmlPath,String schemaPath) {
		SchemaFactory scf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema sc = scf.newSchema(new File(schemaPath));
			Validator validator = sc.newValidator();
			validator.validate(new StreamSource(new File(xmlPath)));
			System.out.println("El documento ha sido validado correctamente");
			return true;
		} catch (Exception e) {
			System.out.println("El documento no ha sido validado correctamente");
			return false;
		}
	}
}
