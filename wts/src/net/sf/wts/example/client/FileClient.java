package net.sf.wts.example.client;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

public class FileClient {
	private static final String SOAPUrl = "http://localhost:8080/wts/services/PieterHandler";
	
	private static final String inputFile = "build.xml";
	private static final String outputFile = "client.xml";
	
	public static void main(String[] args) throws Exception {
		FileInputStream fin = new FileInputStream(inputFile);
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		copy(fin, bout);
		
		fin.close();
		
		byte[] b = bout.toByteArray();
		
		Service service = new Service();
		Call call = (Call) service.createCall();
		
		try {
			call.setTargetEndpointAddress(new java.net.URL(SOAPUrl));
			call.setOperationName(new javax.xml.namespace.QName("urn:FileHandler", "handleFile"));
			call.addParameter("file", XMLType.XSD_HEXBIN, ParameterMode.IN);
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_HEXBIN);
			
			byte[] result = (byte[])(call.invoke(new Object[] { b }));
			
			try {
				FileOutputStream fout = new FileOutputStream(outputFile);
				fout.write(result);
				fout.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		catch (AxisFault fault) {
			System.err.println(fault.toString());
		}
	}
	
	private static void copy(InputStream in, OutputStream out) throws IOException {
		synchronized (in) {
			synchronized (out) {
				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					
					if (bytesRead == -1) {
						break;
					}
					
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	}
}
