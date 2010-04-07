package net.sf.hivgensim.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import net.sf.hivgensim.services.SubtypeService;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.util.settings.RegaDBSettings;



public class SubtypeCompletion {
	
	private static Test t;
	private static Genome g;
	
	public static void main(String[] args) throws FileNotFoundException {
		RegaDBSettings.createInstance();
		t = RegaDBWtsServer.getSubtypeTest();
		g = StandardObjects.getHiv1Genome();
		Scanner s = new Scanner(new File("/home/gbehey0/zehava/subtypes.csv"));
		PrintStream ps = new PrintStream(new File("/home/gbehey0/zehava/complete-subtypes.csv"));
		ps.println("id,sequences,complete-subtype");
		while(s.hasNextLine()){
			String[] cols = s.nextLine().split(",");
			String subtype = "";
			if(cols.length == 2){
				try {
					boolean first = true;
					for(String nucs : cols[1].replaceAll("-","").split("\\+")){
						NtSequence ntseq = new NtSequence();
						ntseq.setNucleotides(nucs);
						SubtypeService ss = new SubtypeService(ntseq,t,g);
						ss.launch();
						if(first){
							subtype = ss.getResult();
							first = false;
						} else {
							subtype += "+" + ss.getResult();
						}						
					}
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			} else {
				subtype = cols[2];
			}
			ps.println(cols[0]+","+cols[1]+","+subtype);
		}
		ps.flush();
		ps.close();
	}

}
