package net.sf.regadb.io.db.portugal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

public class CheckSequences {
	private static class SampleException extends Exception{
		private String sampleId;
		
		public SampleException(String sampleId){
			this.sampleId = sampleId;
		}
		
		public String getSampleId(){ return sampleId; }
	}
	
	@SuppressWarnings("serial")
	private static class SampleIdNotFoundException extends SampleException{
		public SampleIdNotFoundException(String sampleId){
			super(sampleId);
		}
	}
	@SuppressWarnings("serial")
	private static class SampleDatesDifferException extends SampleException{
		private Date a,b; 
		
		public SampleDatesDifferException(String sampleId, Date a, Date b){
			super(sampleId);
			this.a = a;
			this.b = b;
		}
		
		public Date getDateA(){ return a; }
		public Date getDateB(){ return b; }
	}
	@SuppressWarnings("serial")
	private static class NucleotidesDifferException extends SampleException{
		private String nucleotidesA, nucleotidesB;
		
		public NucleotidesDifferException(String sampleId, String nucleotidesA, String nucleotidesB){
			super(sampleId);
			this.nucleotidesA = nucleotidesA;
			this.nucleotidesB = nucleotidesB;
		}
		
		public String getNucleotidesA(){ return nucleotidesA; }
		public String getNucleotidesB(){ return nucleotidesB; }
	}
	
	private static class Fasta{
		public File file;
		public String label;
		public String nucleotides;
		
		public Fasta(File file, String label, String nucleotides){
			this.file = file;
			this.label = label;
			this.nucleotides = nucleotides;
		}
	}
	
	public static void main(String args[]){
		Arguments as = new Arguments();
		PositionalArgument user = as.addPositionalArgument("user", true);
		PositionalArgument pass = as.addPositionalArgument("pass", true);
		PositionalArgument fastaDir = as.addPositionalArgument("fasta-dir", true);
		PositionalArgument outputDir = as.addPositionalArgument("output-dir", true);
		
		if(!as.handle(args))
			return;
		
		RegaDBSettings.createInstance();
		
		try {
			CheckSequences cs = new CheckSequences(user.getValue(), pass.getValue());
			cs.run(new File(fastaDir.getValue()), new File(outputDir.getValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private PrintStream notFoundOut, nucsDifferOut, datesDifferOut, skippedOut;
	private int seqOk, seqNotFound, seqNucsDiffer, seqDatesDiffer, skipped;
	private int pathOffset;
	Map<String, ViralIsolate> isolates = new HashMap<String, ViralIsolate>();
	
	public CheckSequences(String user, String pass) throws WrongUidException, WrongPasswordException, DisabledUserException{
		Transaction t = Login.authenticate(user, pass).createTransaction();
		List<Object[]> result = t.createQuery("select vi.sampleId, vi from ViralIsolate as vi").list();
		for (Object[] r : result) {
			isolates.put((String)r[0], (ViralIsolate)r[1]);
		}
	}
	
	public void run(File fastaDir, File outputDir){
		try{
			notFoundOut = new PrintStream(new FileOutputStream(outputDir.getAbsolutePath() + File.separatorChar + "not_found.csv"));
			nucsDifferOut = new PrintStream(new FileOutputStream(outputDir.getAbsolutePath() + File.separatorChar + "nucs_differ.csv"));
			datesDifferOut = new PrintStream(new FileOutputStream(outputDir.getAbsolutePath() + File.separatorChar + "dates_differ.csv"));
			
			seqOk = seqNotFound = seqNucsDiffer = seqDatesDiffer = skipped = 0;
			
			notFoundOut.println("file;sampleId");
			nucsDifferOut.println("file;sampleId;nucleotides_regadb;nucleotides_fasta");
			datesDifferOut.println("file;sampleId;date_regadb;date_fasta");
			
			pathOffset = fastaDir.getAbsolutePath().length()+1;
			findFastas(fastaDir);
			
			notFoundOut.close();
			nucsDifferOut.close();
			datesDifferOut.close();
			
			System.out.println("Ok: "+ seqOk 
					+" Not found: "+ seqNotFound 
					+" Dates differ: "+ seqDatesDiffer 
					+" Nucs differ: "+ seqNucsDiffer
					+" Skipped: "+ skipped);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void findFastas(File dir){
		for(File f : dir.listFiles()){
			if(skip(f)){
				System.err.println("skipped: "+ f.getAbsolutePath().substring(pathOffset));
				++skipped;
				continue;
			}
			
			if(f.isDirectory())
				findFastas(f);
			else
				process(f);
		}
	}
	
	private boolean skip(File f){
		if(f.isDirectory())
			return false;
		
		String name = f.getName().toLowerCase();
		if(name.endsWith(".fasta")
				|| name.endsWith(".fsta")
				|| name.endsWith(".fas")
				|| name.endsWith(".seq"))
			return false;
		
		return true;
	}
	
	protected String getSampleId(Fasta f){
		int pos = f.file.getName().lastIndexOf('.');
		String sampleId;
		if(pos == -1)
			sampleId = f.file.getName();
		else
			sampleId = f.file.getName().substring(0,pos);
		
		if(Pattern.matches("^[0-9]+s$",sampleId))
			sampleId = sampleId.substring(0,sampleId.length()-1);
		
		return sampleId;
	}
	
	protected String getNucleotides(Fasta f){
		return f.nucleotides.replace("-", "");
	}
	
	protected Date getSampleDate(Fasta f){
		return null;
	}
	
	private void process(File f){
		try {
			StringBuffer nucleotides;

			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			
			String label = br.readLine();
			nucleotides = new StringBuffer();
			while((line = br.readLine()) != null){
				nucleotides.append(line.trim().toLowerCase());
			}
			
			br.close();
			
			Fasta fasta = new Fasta(f,label,nucleotides.toString());
			checkSequence(
					getSampleId(fasta),
					getSampleDate(fasta),
					getNucleotides(fasta));
			++seqOk;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SampleIdNotFoundException e) {
			notFoundOut.println(f.getAbsolutePath().substring(pathOffset) +";"+ e.getSampleId());
			++seqNotFound;
		} catch (SampleDatesDifferException e) {
			datesDifferOut.println(f.getAbsolutePath().substring(pathOffset) +";"+ e.getSampleId() +";"+ e.getDateA() +";"+ e.getDateB());
			++seqDatesDiffer;
		} catch (NucleotidesDifferException e) {
			nucsDifferOut.println(f.getAbsolutePath().substring(pathOffset) +";"+ e.getSampleId() +";"+ e.getNucleotidesA() +";"+ e.getNucleotidesB());
			++seqNucsDiffer;
		}
	}

	
	protected void checkSequence(String sampleId, Date sampleDate, String nucleotides)
			throws SampleIdNotFoundException, SampleDatesDifferException, NucleotidesDifferException{
		ViralIsolate vi = isolates.get(sampleId);
		if(vi == null)
			throw new SampleIdNotFoundException(sampleId);
		
		boolean found = false;
		String dbNucleotides = null;
			for(NtSequence nt : vi.getNtSequences()){
				dbNucleotides = nt.getNucleotides().trim().toLowerCase();
				if(dbNucleotides.equals(nucleotides)){
					found = true;
					break;
				}
			}
			if(found){
				if(sampleDate != null && !sampleDate.equals(vi.getSampleDate()))
					throw new SampleDatesDifferException(sampleId, vi.getSampleDate(), sampleDate);
			}
		
		if(!found)
			throw new NucleotidesDifferException(sampleId, dbNucleotides, nucleotides);		
	}
}
