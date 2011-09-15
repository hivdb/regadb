package be.kuleuven.rega.research.lowLevelViralLoad;

import java.io.File;

import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.TableQueryOutput;
import net.sf.hivgensim.queries.framework.utils.ViralIsolateUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;

public class LowLevelViralLoadOutput extends TableQueryOutput<LowLevelViralLoadData>{
	
	private boolean first = true;

	public LowLevelViralLoadOutput(
			Table out,
			File file,
			net.sf.hivgensim.queries.framework.TableQueryOutput.TableOutputType type) {
		super(out, file, type);
	}

	@Override
	public void process(LowLevelViralLoadData input) {
		if(first){
			first = false;
			addHeader();
		}

		addColumn(input.patient.getPatientId());
		addColumn(DateUtils.format(input.undetectableViralLoad.getTestDate()));
		addColumn(input.undetectableViralLoad.getValue());
		addColumn(DateUtils.format(input.lowViralLoad.getTestDate()));
		addColumn(input.lowViralLoad.getValue());
		addColumn(DateUtils.format(input.therapy.getStartDate()));
		addColumn(DateUtils.format(input.therapy.getStopDate()));
		addColumn(input.lowViralLoadIsolate.getSampleId());
		addColumn(DateUtils.format(input.lowViralLoadIsolate.getSampleDate()));
		addColumn(getSequence(input.lowViralLoadIsolate));
		addColumn(input.preLowViralLoadIsolate.getSampleId());
		addColumn(DateUtils.format(input.preLowViralLoadIsolate.getSampleDate()));
		addColumn(getSequence(input.preLowViralLoadIsolate), true);
	}
	
	private String getSequence(ViralIsolate vi){
		return ViralIsolateUtils.getConcatenatedNucleotideSequence(vi);
//		StringBuilder sb = new StringBuilder();
//		
//		for(NtSequence seq : vi.getNtSequences()){
//			sb.append(seq.getLabel());
//			sb.append(':');
//			sb.append(seq.getNucleotides());
//			sb.append(';');
//		}
//		
//		return sb.toString();
	}
	
	private void addHeader(){
		addColumn("patient_id");
		addColumn("undetectable_vl_date");
		addColumn("undetectable_vl_value");
		addColumn("low_vl_date");
		addColumn("low_vl_value");
		addColumn("therapy_start");
		addColumn("therapy_stop");
		addColumn("sample_id");
		addColumn("sample_date");
		addColumn("sequence");
		addColumn("prev_sample_id");
		addColumn("prev_sample_date");
		addColumn("prev_sequence", true);
	}

	public static void main(String[] args) throws WrongUidException, WrongPasswordException, DisabledUserException{
		Arguments as = new Arguments();
		PositionalArgument user = as.addPositionalArgument("user", true);
		PositionalArgument pass = as.addPositionalArgument("pass", true);
		PositionalArgument output = as.addPositionalArgument("output-file", true);
		ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
		
		ValueArgument dayDiffTherapy = as.addValueArgument("t", "max-day-diff-vl-therapy", false).setValue(
				LowLevelViralLoadQuery.DAY_DIFF_VIRAL_LOAD_THERAPY +"");
		ValueArgument dayDiffSample = as.addValueArgument("s", "max-day-diff-vl-sample", false).setValue(
				LowLevelViralLoadQuery.DAY_DIFF_VIRAL_LOAD_SAMPLE +"");
		
		ValueArgument minLowViralLoad = as.addValueArgument("min", "min-low-viral-load", false).setValue(
				LowLevelViralLoadQuery.MIN_VIRAL_LOAD +"");
		ValueArgument maxLowViralLoad = as.addValueArgument("max", "max-low-viral-load", false).setValue(
				LowLevelViralLoadQuery.MAX_VIRAL_LOAD +"");
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		if(dayDiffSample.isSet())
			LowLevelViralLoadQuery.DAY_DIFF_VIRAL_LOAD_SAMPLE = Integer.parseInt(dayDiffSample.getValue());
		if(dayDiffTherapy.isSet())
			LowLevelViralLoadQuery.DAY_DIFF_VIRAL_LOAD_THERAPY = Integer.parseInt(dayDiffTherapy.getValue());
		
		if(minLowViralLoad.isSet())
			LowLevelViralLoadQuery.MIN_VIRAL_LOAD = Double.parseDouble(minLowViralLoad.getValue());
		if(maxLowViralLoad.isSet())
			LowLevelViralLoadQuery.MAX_VIRAL_LOAD = Double.parseDouble(maxLowViralLoad.getValue());
		
		Login login = Login.authenticate(user.getValue(), pass.getValue());
		Transaction t = login.createTransaction();
		
		LowLevelViralLoadQuery query = new LowLevelViralLoadQuery(
				new LowLevelViralLoadOutput(new Table(), new File(output.getValue()), TableOutputType.CSV), t);
		
		t.commit();
		login.closeSession();
		
		QueryInput input = new FromDatabase(user.getValue(), pass.getValue(), query);
		input.run();
	}
}
