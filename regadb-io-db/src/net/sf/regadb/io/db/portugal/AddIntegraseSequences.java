package net.sf.regadb.io.db.portugal;

import java.io.File;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.io.db.util.AddViralIsolates;
import net.sf.regadb.io.db.util.AddViralIsolates.SampleIdNotFoundException;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

public class AddIntegraseSequences {

	private AddViralIsolates adder;
	
	public AddIntegraseSequences(String user, String pass, String dataset) throws WrongUidException, WrongPasswordException, DisabledUserException{
		adder = new AddViralIsolates(user, pass, dataset);
	}
	
	public void run(File sequenceDir){
		for(File f : sequenceDir.listFiles()){
			if(!f.getName().endsWith("_IN.fsta")){
				System.err.println("skipping: "+ f.getName());
				continue;
			}
			
			String sampleId = f.getName().substring(0, f.getName().length() - "_IN.fsta".length());
			String nucleotides = FastaHelper.readFastaFile(f, true).xna_;
			
			try {
				adder.handleSequence(sampleId, "IN", nucleotides);
			} catch (SampleIdNotFoundException e) {
				System.err.println(e.getMessage());
			}
		}
		
		adder.exportViralIsolates(new File(sequenceDir.getAbsolutePath() + File.separatorChar +"viralisolates.xml"));
	}
	
	public static void main(String args[]) {
		Arguments as = new Arguments();
    	ValueArgument conf			= as.addValueArgument("conf-dir", "configuration directory", false);
    	PositionalArgument user		= as.addPositionalArgument("regadb user", true);
    	PositionalArgument pass		= as.addPositionalArgument("regadb password", true);
    	PositionalArgument dataset	= as.addPositionalArgument("regadb dataset", true);
		PositionalArgument seqDir = as.addPositionalArgument("sequence-dir", true);
		
		if(!as.handle(args))
			return;
		
		if(conf.isSet())
			RegaDBSettings.createInstance(conf.getValue());
		else
			RegaDBSettings.createInstance();
		
		try {
			AddIntegraseSequences anv = new AddIntegraseSequences(user.getValue(), pass.getValue(), dataset.getValue());
			anv.run(new File(seqDir.getValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
