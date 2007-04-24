package net.sf.wts.client;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

public class Test 
{
    public static void main(String [] args)
    {
        String SOAPUrlChallenge = "http://localhost:8080/wts/services/GetChallenge";
        String SOAPUrlLogin = "http://localhost:8080/wts/services/Login";
        String SOAPUrlListServices = "http://localhost:8080/wts/services/ListServices";
        
        Service service = new Service();
        Call call;
        try {
            call = (Call) service.createCall();

        
        try {
            call.setTargetEndpointAddress(new java.net.URL(SOAPUrlListServices));
            call.setOperationName(new javax.xml.namespace.QName("urn:ListServices", "exec"));
            //call.addParameter("in0", XMLType.XSD_STRING, ParameterMode.IN);
            
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            
            String result = (String)(call.invoke(new Object[]{}));
            System.err.println("result:"+result);
            
            
            service = new Service();
            call = (Call) service.createCall();
            
            call.setTargetEndpointAddress(new java.net.URL(SOAPUrlChallenge));
            call.setOperationName(new javax.xml.namespace.QName("urn:GetChallenge", "exec"));
            call.removeAllParameters();
            call.addParameter("in0", XMLType.XSD_STRING, ParameterMode.IN);
            
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            
            result = (String)(call.invoke(new Object[]{"kdforc0"}));
            System.err.println("result:"+result);
            
            
            service = new Service();
            call = (Call) service.createCall();
            
            call.setTargetEndpointAddress(new java.net.URL(SOAPUrlLogin));
            call.setOperationName(new javax.xml.namespace.QName("urn:Login", "exec"));
            call.removeAllParameters();
            call.addParameter("in0", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("in1", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("in2", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("in3", XMLType.XSD_STRING, ParameterMode.IN);
            
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            
            result = (String)(call.invoke(new Object[]{"kdforc0", result, Encrypt.encryptMD5(Encrypt.encryptMD5("Vitabis1")+result), "regadb-align"}));
            System.err.println("result:"+result);
        }
        catch (AxisFault fault) {
            System.err.println("faulty");
            System.err.println(fault.toString());
            fault.detail.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        } catch (ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
