package net.sf.wts.client.test;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import net.sf.wts.client.WtsClient;

public class ServerClientTest {
	private Map<String,String> inputs = new HashMap<String,String>();
    private Map<String,String> outputs = new HashMap<String,String>();

    public Map<String,String> getInputs(){
    	return inputs;
    }
    public Map<String,String> getOutputs(){
    	return outputs;
    }
	public String getAccount(){
		return "public";
	}
	public String getPassword(){
		return "public";
	}
	public String getService(){
		return "get-file-provider-file";
	}
	
	public void launch() {
		inputs.put("file_provider_name","test");
		inputs.put("file_name","file");
		outputs.put("file_provider_file",null);

		WtsClient c = new WtsClient("http://localhost:8080/wts/services/");

		String challenge;
		String ticket;

		try {
			challenge = c.getChallenge(getAccount());
			ticket = c.login(getAccount(), challenge, getPassword(), getService());

			for (Map.Entry<String, String> entry : getInputs().entrySet()) {
				c.upload(ticket, getService(), entry.getKey(), entry.getValue().getBytes());
			}

			c.start(ticket, getService());

			boolean finished = false;
			while (!finished) {
				try {
					Thread.sleep(getWaitDelay());
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				if (c.monitorStatus(ticket, getService()).startsWith("ENDED")) {
					finished = true;
				}
			}

//			for (Map.Entry<String, String> entry : getOutputs().entrySet()) {
//				byte[] resultArray = c.download(ticket, getService(),
//						entry.getKey());
//				entry.setValue(new String(resultArray));
//				System.out.println(entry.getKey() +" "+ entry.getValue());
//			}

			c.closeSession(ticket, getService());
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	private long getWaitDelay() {
		return 10;
	}
	
	public static void main(String args[]){
		ServerClientTest sct = new ServerClientTest();
		
		long i=0;
		while(true){
			sct.launch();
			if((++i % 1000) == 0)
				System.out.println(""+ i);
		}
	}
}
