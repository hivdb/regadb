package net.sf.regadb.io.db.ghb;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.io.db.util.AddViralIsolates;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.db.util.AddViralIsolates.AddViralIsolateException;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.pair.Pair;
import net.sf.regadb.util.settings.RegaDBSettings;

public class AddMissingViralIsolates {

	private AddViralIsolates adder;
	
	public AddMissingViralIsolates(String user, String pass, String dataset) throws WrongUidException, WrongPasswordException, DisabledUserException{
		adder = new AddViralIsolates(user, pass, dataset);
	}
	
	public void run(File sampleIds, File sequenceDir){
		
		TreeMap<String,List<Pair<String,String>>> sequences = new TreeMap<String,List<Pair<String,String>>>();
		Pattern pattern = Pattern.compile("([A-Za-z0-9]+-[0-9]+)[-_.]?([^\\.]*)\\.(fsta|fasta)");

		for(File f : sequenceDir.listFiles()){
			if(!f.getName().endsWith(".fasta") && !f.getName().endsWith(".fsta"))
				continue;
			
			String sampleId = f.getName();
			Matcher matcher = pattern.matcher(sampleId);
			if(matcher.matches()){
				sampleId = matcher.group(1).toLowerCase();
				String label = matcher.group(2);
				String nucleotides = FastaHelper.readFastaFile(f, true).xna_;
				
				List<Pair<String,String>> s = sequences.get(sampleId);
				if(s == null){
					s = new LinkedList<Pair<String,String>>();
					sequences.put(sampleId, s);
				}
				s.add(new Pair<String,String>(label.length()==0 ? "Sequence "+ (s.size()+1) : label, nucleotides));
			}
			else{
				System.err.println("couldn't parse sample id: "+ sampleId);
			}
		}
		

		Table t = Utils.readTable(sampleIds.getAbsolutePath(), ',');
		
		int iSampleId = t.findColumn("sample_id");
		int iSampleDate = t.findColumn("sample_date");
		int iPatientId = t.findColumn("patient_id");
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		for(int i=1; i<t.numRows(); ++i){
			try {
				String patientId = t.valueAt(iPatientId, i);
				String sampleId = t.valueAt(iSampleId, i);
				Date sampleDate = df.parse(t.valueAt(iSampleDate, i));
				
				List<Pair<String,String>> s = sequences.get(sampleId.toLowerCase());
				if(s == null){
					System.err.println("no fasta(s) found: "+ sampleId);
					continue;
				}
			
				adder.handleViralIsolate(patientId, sampleId, sampleDate, s);
			} catch (ParseException e) {
				System.err.println(e.getMessage());
			} catch (AddViralIsolateException e) {
				System.err.println(e.getMessage());
			}
		}
		
		adder.exportViralIsolates(
				new File(sequenceDir.getAbsolutePath() + File.separatorChar + "viralisolates.xml"));
	}
	
	public static void main(String args[]) {
		Arguments as = new Arguments();
    	ValueArgument conf			= as.addValueArgument("conf-dir", "configuration directory", false);
    	PositionalArgument user		= as.addPositionalArgument("regadb user", true);
    	PositionalArgument pass		= as.addPositionalArgument("regadb password", true);
    	PositionalArgument dataset	= as.addPositionalArgument("regadb dataset", true);
		PositionalArgument sampleIds = as.addPositionalArgument("sample-ids.csv", true);
		PositionalArgument seqDir = as.addPositionalArgument("sequence-dir", true);
		
		if(!as.handle(args))
			return;
		
		if(conf.isSet())
			RegaDBSettings.createInstance(conf.getValue());
		else
			RegaDBSettings.createInstance();
		
		try {
			AddMissingViralIsolates anv = new AddMissingViralIsolates(user.getValue(), pass.getValue(), dataset.getValue());
			anv.run(new File(sampleIds.getValue()), new File(seqDir.getValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
