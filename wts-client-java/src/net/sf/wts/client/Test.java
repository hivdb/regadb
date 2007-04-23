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
        String SOAPUrl = "http://zolder:8080/wts/services/GetChallenge";
        
        Service service = new Service();
        Call call;
        try {
            call = (Call) service.createCall();

        
        try {
            call.setTargetEndpointAddress(new java.net.URL(SOAPUrl));
            call.setOperationName(new javax.xml.namespace.QName("urn:GetChallenge", "exec"));
            call.addParameter("in0", XMLType.XSD_STRING, ParameterMode.IN);
            
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            
            

            
           String result = (String)(call.invoke(new Object[]{}));
            call.invoke();
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
