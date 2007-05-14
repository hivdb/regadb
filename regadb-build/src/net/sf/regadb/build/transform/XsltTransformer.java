package net.sf.regadb.build.transform;

import java.io.FileOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XsltTransformer 
{
	public static void transform(String xmlFileName, String htmlFileName, String xsltFileName ) 
	{
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tFactory.newTransformer(new StreamSource(xsltFileName));
			
			transformer.transform(new StreamSource(xmlFileName), new StreamResult(new FileOutputStream(htmlFileName)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
