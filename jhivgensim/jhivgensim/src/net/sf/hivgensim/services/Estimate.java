package net.sf.hivgensim.services;

import java.io.File;

import net.sf.wts.client.WtsClient;

public class Estimate extends AbstractService{
	
	public void run(
			String mutTreatedFilename, 
			String naiveFastaFilename,
			String mutTreatedIdtFilename,
			String mutTreatedStrFilename,
			String mutTreatedVdFilename,
			String wildtypesFilename,
			String doublepositionsFilename,
			String mutagenesisFilename,
			String weightsFilename,
			String bestCftOutFilename,
			String estimateDiagOutFilename
			){
		WtsClient wc = new WtsClient(getUrl(),getEncodedPrivateKey());
		String serviceName = "estimate";
		
		try{
			String challenge = wc.getChallenge(getUid());
			String sessionTicket = wc.login(getUid(), challenge, serviceName);

			wc.upload(sessionTicket, serviceName, "mut_treated.csv", new File(mutTreatedFilename));
			wc.upload(sessionTicket, serviceName, "naive.fasta", new File(naiveFastaFilename));
			wc.upload(sessionTicket, serviceName, "mut_treated.idt", new File(mutTreatedIdtFilename));
			wc.upload(sessionTicket, serviceName, "mut_treated.str", new File(mutTreatedStrFilename));
			wc.upload(sessionTicket, serviceName, "mut_treated.vd", new File(mutTreatedVdFilename));
			wc.upload(sessionTicket, serviceName, "wildtypes", new File(wildtypesFilename));
			wc.upload(sessionTicket, serviceName, "doublepositions", new File(doublepositionsFilename));
			wc.upload(sessionTicket, serviceName, "mutagenesis", new File(mutagenesisFilename));
			wc.upload(sessionTicket, serviceName, "weights.csv", new File(weightsFilename));
			
			wc.start(sessionTicket, serviceName);
			String status = wc.monitorStatus(sessionTicket, serviceName);			
			while (!status.equals("ENDED_SUCCES")) {
				status = wc.monitorStatus(sessionTicket, serviceName);			
			}
			wc.download(sessionTicket, serviceName, "best.cft", new File(bestCftOutFilename));
			wc.download(sessionTicket, serviceName, "estimate.diag", new File(estimateDiagOutFilename));
			wc.closeSession(sessionTicket, serviceName);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
