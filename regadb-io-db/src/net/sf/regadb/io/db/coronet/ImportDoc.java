package net.sf.regadb.io.db.coronet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.tools.FastaFile;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ImportDoc {
	
	private FastaFile beforeTherapy;
	private FastaFile afterTherapy;
	
	private static DateFormat longDate = new SimpleDateFormat("dd/MM/yyyy");
	private static DateFormat shortDate = new SimpleDateFormat("dd/MM/yy");
	
	private Map<String, Patient> patients = new TreeMap<String, Patient>();
	
	private PrintStream log;
	
	public ImportDoc(){
		beforeTherapy = new FastaFile();
		afterTherapy = new FastaFile();
		
		log = System.out;
		log.println("file,patient_id,centre,sample_id1,sample_date1,nt_sequence_count1,sample_id2,sample_date2,nt_sequence_count2");
	}
	
	public void parseDoc(File docFile){
		POIFSFileSystem fs = null;

		log.print(docFile.getName() +",");
		
		try{
			fs = new POIFSFileSystem(new FileInputStream(docFile)); 
			HWPFDocument doc = new HWPFDocument(fs);
			WordExtractor we = new WordExtractor(doc);
			String[] ps = we.getParagraphText();
			
			Calendar cal = Calendar.getInstance();
			cal.set(1900, 1, 1);
			Date firstUnknownDate = cal.getTime();
			cal.set(2000,1,1);
			Date lastUnknownDate = cal.getTime();
			
			String patientId = null;
			String centre = null;
			
			ViralIsolate firstIsolate = null;
			ViralIsolate lastIsolate = null;
			
			for(int i=0; i<ps.length; i++) {
				ps[i] = ps[i].replaceAll("\\cM?\r?\n","");
//				System.out.println("["+ i +"] "+ ps[i]);
				
				if(ps[i].contains("Centre of care:")){
					centre = ps[i].replace("Centre of care:", "").trim();
				}
				else if(ps[i].contains("Patient ID:")){
					patientId = ps[i].replace("Patient ID:", "").trim();
				}
				else if(ps[i].contains("Nucleotide sequences before raltegravir")){
					ViralIsolate v = readIsolate(ps, i);
					if(v.getNtSequences().size() > 0){
						if(v.getSampleDate() == null)
							v.setSampleDate(firstUnknownDate);
						v.setSampleId(trimSampleId(v.getSampleId()));
						firstIsolate = v;
					}
				}
				else if(ps[i].contains("Nucleotide sequences after raltegravir")){
					ViralIsolate v = readIsolate(ps, i);
					if(v.getNtSequences().size() > 0){
						if(v.getSampleDate() == null)
							v.setSampleDate(lastUnknownDate);
						v.setSampleId(trimSampleId(v.getSampleId()));
						lastIsolate = v;
					}
				}
			}
			
			if(patientId != null && patientId.length() > 0){
				Patient p = new Patient();
				p.setPatientId(centre +"_"+ patientId);
				
				log.print(patientId +","+ centre +",");
				
				if(firstIsolate != null){
					p.addViralIsolate(firstIsolate);
					log.print(firstIsolate.getSampleId() +",");
					log.print(longDate.format(firstIsolate.getSampleDate()) +",");
					log.print(firstIsolate.getNtSequences().size() +",");
					
					addToFastaFile(beforeTherapy, p.getPatientId() +"_", firstIsolate);
				} else {
					log.print(",,,");
				}
				if(lastIsolate != null){
					p.addViralIsolate(lastIsolate);
					log.print(lastIsolate.getSampleId() +",");
					log.print(longDate.format(lastIsolate.getSampleDate()) +",");
					log.print(lastIsolate.getNtSequences().size());

					addToFastaFile(afterTherapy, p.getPatientId() +"_", lastIsolate);
				} else {
					log.print(",,");
				}
				
				if(patients.put(p.getPatientId(), p) != null){
					System.err.println("duplicate patient id: "+ p.getPatientId());
				}
			}
		}
		catch(Exception e) { 
			e.printStackTrace();
		}
		
		log.println();
	}
	
	private void addToFastaFile(FastaFile fasta, String prefix, ViralIsolate v){
		for(NtSequence n : v.getNtSequences()){
			if(n.getLabel().toLowerCase().contains("integrase"))
				fasta.add(
						prefix + n.getLabel(),
						n.getNucleotides(),
						true);
		}
	}
	
	private String trimSampleId(String sampleId){
		int pos = sampleId.indexOf("specimen");
		if(pos != -1){
			sampleId = sampleId.substring(pos +"specimen".length());
		}
		return sampleId.trim();
	}
	
	private void printViralIsolate(ViralIsolate v){
		System.err.println("Sample id: "+ v.getSampleId());
		System.err.println("Sample date: "+ (v.getSampleDate() == null ? "-" : longDate.format(v.getSampleDate())));
		for(NtSequence nt : v.getNtSequences()){
			System.err.println(nt.getLabel() +": "+ nt.getNucleotides());
		}
	}
	
	private ViralIsolate readIsolate(String[] ps, int i){
		ViralIsolate v = new ViralIsolate();
		String label = null;
		
		int numberOfSequences = 0;
		
		for(;i<ps.length; ++i){
			if(ps[i].contains(" sequence")){
				label = ps[i].substring(0, ps[i].indexOf(" sequence")).trim();
			}
			else if(v.getSampleDate() == null
					&& ps[i].contains("Sample date")){
				Date sampleDate = parseDate(ps[i].replaceAll("[^0-9\\/]",""));
				if(sampleDate != null)
					v.setSampleDate(sampleDate);
				
			} else if(ps[i].contains(">")){
				String[] lines = ps[i].split("\r");
				
				if(v.getSampleId() == null || v.getSampleId().length() == 0)
					v.setSampleId(lines[0].replace(">", "").trim());
				
				StringBuilder nucs = new StringBuilder();
				for(int j=1; j<lines.length; ++j)
					nucs.append(lines[j]);
				
				while(++i < ps.length){
					int pos = ps[i].indexOf("");
					if(pos != -1){
						nucs.append(ps[i].substring(0,pos));
						break;
					}
					else{
						nucs.append(ps[i]);
					}
				}
				
				String nucleotides = nucs.toString().replaceAll("[^A-Za-z]", "");
				if(nucleotides.length() > 0 && !nucleotides.toLowerCase().equals("seeabove")){
					NtSequence nt = new NtSequence(v);
					v.getNtSequences().add(nt);
					nt.setLabel(label);
					nt.setNucleotides(nucleotides);
				}
					
				if(++numberOfSequences == 3)
					break;
			}
		}
		
		return v;
	}
	
	private Date parseDate(String s){
		Date d = null;
		try {
			if(s.length() == 10){
				d = longDate.parse(s);
			} else if(s.length() == 8){
				d = shortDate.parse(s);
			}
		} catch (ParseException e) {
		}
		
		return d;
	}
	
	public Collection<Patient> getPatients(){
		return patients.values();
	}
	
	public FastaFile getBeforeTherapyFastaFile(){
		return beforeTherapy;
	}
	
	public FastaFile getAfterTherFastaFile(){
		return afterTherapy;
	}

	public static void main(String[] args){
		Arguments as = new Arguments();
		PositionalArgument iDir = as.addPositionalArgument("input-dir", true);
		PositionalArgument oDir = as.addPositionalArgument("output-dir", true);
		
		if(!as.handle(args))
			return;
		
		File oDirFile = new File(oDir.getValue());
		ImportDoc id = new ImportDoc();
		
		File wd = new File(iDir.getValue());
		for(File f : wd.listFiles()){
			if(f.getName().toLowerCase().endsWith(".doc")){
				id.parseDoc(f);
			}
		}
		
		try {
			id.getAfterTherFastaFile().write(
					new File(oDirFile.getAbsolutePath() + File.separator +"after.fasta"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			id.getBeforeTherapyFastaFile().write(
					new File(oDirFile.getAbsolutePath() + File.separator +"before.fasta"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		IOUtils.exportPatientsXML(
				id.getPatients(),
				oDirFile.getAbsolutePath() + File.separatorChar + "patients.xml",
				ConsoleLogger.getInstance());
	}
}
