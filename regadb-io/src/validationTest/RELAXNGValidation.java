package validationTest;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

public class RELAXNGValidation {
    public static void main(String[] args) throws Exception {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
        Schema schema = sf.newSchema(new File("/home/plibin0/myWorkspace/regadb-io/src/net/sf/regadb/io/relaxng/regadb-relaxng.xml"));

        //for( int i=1; i<args.length; i++ ) {
        //    System.out.println("Validating "+args[i]);
            schema.newValidator().validate(new StreamSource("/home/plibin0/patient.xml"));
        //}
    }
}
