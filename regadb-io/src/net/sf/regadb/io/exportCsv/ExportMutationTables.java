package net.sf.regadb.io.exportCsv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

public class ExportMutationTables {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws WrongUidException, WrongPasswordException, DisabledUserException{
		Arguments as = new Arguments();
		PositionalArgument user = as.addPositionalArgument("user", true);
		PositionalArgument pass = as.addPositionalArgument("pass", true);
		PositionalArgument dir = as.addPositionalArgument("output-dir", true);
		ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		Login login = Login.authenticate(user.getValue(), pass.getValue());
		
		Transaction t = login.createTransaction();
		
		Map<String, MutationTable> mutationTables = new TreeMap<String, MutationTable>();
		
		Query q = t.createQuery("select v.sampleId, n.label, p.abbreviation, g.organismName, a " +
			"from ViralIsolate v join v.ntSequences n join n.aaSequences a join v.genome g join a.protein p");
		
		for(Object[] os : (List<Object[]>)q.list()){
			String sampleId = (String)os[0];
			String label = (String)os[1];
			String protein = (String)os[2];
			String organism = (String)os[3];
			AaSequence aaSequence = (AaSequence)os[4];
			
			String mtKey = organism +"_"+ protein;
			MutationTable mt = mutationTables.get(mtKey);
			if(mt == null){
				mt = new MutationTable();
				mutationTables.put(mtKey, mt);
			}
			
			List<MutationTable.Mutation> muts = new ArrayList<MutationTable.Mutation>();
			for(AaMutation m : aaSequence.getAaMutations())
				muts.add(new MutationTable.DbAaMutation(m));
			for(AaInsertion i : aaSequence.getAaInsertions())
				muts.add(new MutationTable.DbAaInsertion(i));
			
			String sampleKey = sampleId +"_"+ label;
			mt.add(sampleKey, muts);
		}
		
		t.commit();
		
		File outputDir = new File(dir.getValue());
		for(Map.Entry<String, MutationTable> mte : mutationTables.entrySet()){
			File f = new File(outputDir.getAbsolutePath() + File.separator +"mutation_table_"+ mte.getKey() +".csv");
			PrintStream ps = null;
			try{
				ps = new PrintStream(new FileOutputStream(f));
				mte.getValue().writeCsvTable(ps);
			} catch(IOException e){
				e.printStackTrace();
			} finally {
				if(ps != null){
					ps.close();
				}
			}
		}
	}
}
