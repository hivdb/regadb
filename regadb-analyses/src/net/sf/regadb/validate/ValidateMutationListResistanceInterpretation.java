package net.sf.regadb.validate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.sequencedb.SequenceUtils;
import net.sf.regadb.service.wts.ViralIsolateAnalysisHelper;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.frequency.ProgressCounter;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ValidateMutationListResistanceInterpretation {
	
	private Login login;
	
	public ValidateMutationListResistanceInterpretation(){
	}
	
	public void run(String user, String pass, File outputDir)
			throws WrongUidException, WrongPasswordException, DisabledUserException{
		login = Login.authenticate(user, pass);
		
		try{
			
			File oldDir = new File(outputDir.getAbsolutePath() + File.separatorChar + "old");
			File mlcDir = new File(outputDir.getAbsolutePath() + File.separatorChar + "mlc");
			File mljDir = new File(outputDir.getAbsolutePath() + File.separatorChar + "mlj");
			
			oldDir.mkdir();
			mlcDir.mkdir();
			mljDir.mkdir();
			
			Transaction t = login.createTransaction();

			List<Test> riTests = new ArrayList<Test>();
			Collection<Test> tests = t.getTests();
			for(Test test : tests){
				if(StandardObjects.getGssDescription().equals(test.getTestType().getDescription())
						|| StandardObjects.getTDRDescription().equals(test.getTestType().getDescription()))
					riTests.add(test);
			}
			
			Collection<ViralIsolate> vis = t.getViralIsolates();
			ProgressCounter progress = new ProgressCounter(vis.size());
			progress.start();
			
			for(ViralIsolate vi : vis){
				t.attach(vi);
				
				File oldViDir = new File(oldDir.getAbsolutePath()
						+ File.separatorChar + vi.getSampleId());
				File mlcViDir = new File(mlcDir.getAbsolutePath()
						+ File.separatorChar + vi.getSampleId());
				File mljViDir = new File(mljDir.getAbsolutePath()
						+ File.separatorChar + vi.getSampleId());

				oldViDir.mkdir();
				mlcViDir.mkdir();
				mljViDir.mkdir();
				
				List<ViralIsolate> l = new ArrayList<ViralIsolate>(1);
				l.add(vi);
				ViralIsolate combinedVi = SequenceUtils.combineViralIsolates(l);
				
				for(Test test : riTests){
					t.attach(test);
					interpret(test, vi, combinedVi, oldViDir, mlcViDir, mljViDir);
				}
				
				progress.tick();
				
				if(progress.getTotal() % 100 == 1)
					System.err.println(progress.getProgressString());
				
				t.clearCache();
				t.flush();
				t = login.createTransaction();
			}

			
			t.commit();
			
		} finally {
			login.closeSession();
		}
	}
	
	public void interpret(Test test, ViralIsolate vi, ViralIsolate combinedVi,
			File oldViDir, File mlcViDir, File mljViDir){
		int waitDelay = 10;
		
		File f = new File(oldViDir.getAbsolutePath()
				+ File.separatorChar + test.getDescription()
				+ "_" + test.getTestIi() + ".xml");
		if(!f.exists())
			write(f, ViralIsolateAnalysisHelper.run(vi, test, waitDelay));

		f = new File(mljViDir.getAbsolutePath()
				+ File.separatorChar + test.getDescription()
				+ "_" + test.getTestIi() + ".xml");
		if(!f.exists())
			write(f, ViralIsolateAnalysisHelper.runMutlist(combinedVi, test, waitDelay));
		
		f = new File(mlcViDir.getAbsolutePath()
				+ File.separatorChar + test.getDescription()
				+ "_" + test.getTestIi() + ".xml");
		if(!f.exists())
			write(f, ViralIsolateAnalysisHelper.runMutlist(vi, test, waitDelay));
	}
	
	public void write(File out, byte[] content){
		if(content == null || content.length == 0)
			return;
		
		FileOutputStream fos = null;
		
		try{
			fos = new FileOutputStream(out);
			fos.write(content);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(fos != null)
				try{
					fos.close();
				} catch(Exception e){}
		}
	}
	
	public static void main(String[] args)
			throws WrongUidException, WrongPasswordException, DisabledUserException{
		Arguments as = new Arguments();
		
		PositionalArgument user = as.addPositionalArgument("user", true);
		PositionalArgument pass = as.addPositionalArgument("pass", true);
		PositionalArgument output = as.addPositionalArgument("output-dir", true);
		
		ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		ValidateMutationListResistanceInterpretation vmlri =
			new ValidateMutationListResistanceInterpretation();
		
		vmlri.run(user.getValue(), pass.getValue(), new File(output.getValue()));
	}
}
