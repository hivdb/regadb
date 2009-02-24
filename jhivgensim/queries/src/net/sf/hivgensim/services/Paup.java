package net.sf.hivgensim.services;

import java.io.File;

import net.sf.wts.client.WtsClient;

public class Paup extends AbstractService {
	
	public void run(String inputFilename, String outputFilename){
		WtsClient wc = new WtsClient(getUrl(),getEncodedPrivateKey());
		String serviceName = "paup";
		
		String input = "phylo.nex";
		String output = "tree.phy";
		
		File localLocation = new File(inputFilename);
		File toWrite = new File(outputFilename);
		
		try{
			String challenge = wc.getChallenge(getUid());
			String sessionTicket = wc.login(getUid(), challenge, serviceName);

			wc.upload(sessionTicket, serviceName, input, localLocation);
			
			wc.start(sessionTicket, serviceName);
			String status = wc.monitorStatus(sessionTicket, serviceName);			
			while (!status.equals("ENDED_SUCCES")) {
				status = wc.monitorStatus(sessionTicket, serviceName);			
			}
			wc.download(sessionTicket, serviceName, output, toWrite);			
			wc.closeSession(sessionTicket, serviceName);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
