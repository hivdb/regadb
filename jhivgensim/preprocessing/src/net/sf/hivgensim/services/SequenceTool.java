package net.sf.hivgensim.services;

import java.io.File;

import net.sf.wts.client.WtsClient;

public class SequenceTool extends AbstractService{

	public void align(String referenceFileName, String sequencesFileName, String outputFileName){
		WtsClient wc = new WtsClient(getUrl(),getEncodedPrivateKey());
		String serviceName = "sequencetool-align";

		String input = "reference.fasta";
		String input2 = "sequences.fasta";
		String output = "aligned.sequences.fasta";

		File localLocation = new File(referenceFileName);
		File localLocation2 = new File(sequencesFileName);
		File toWrite = new File(outputFileName);
		
		try{
			String challenge = wc.getChallenge(getUid());
			String sessionTicket = wc.login(getUid(), challenge, serviceName);

			wc.upload(sessionTicket, serviceName, input, localLocation);
			wc.upload(sessionTicket, serviceName, input2, localLocation2);

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
