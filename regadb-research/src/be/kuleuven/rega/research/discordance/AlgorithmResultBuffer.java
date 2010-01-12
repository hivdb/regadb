package be.kuleuven.rega.research.discordance;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;

public class AlgorithmResultBuffer {

	private Map<Algorithm, ResistanceTestResult> scorePerAlgorithm = new HashMap<Algorithm, ResistanceTestResult>();
	
	public void updateScores(Algorithm current, char sir, double gss, int rule, boolean treated) {
		if(scorePerAlgorithm.containsKey(current)){
			throw new IllegalStateException(current+" already exists");
		}
		scorePerAlgorithm.put(current, new ResistanceTestResult(rule, sir, gss, treated));
	}

	public void end() {
		scorePerAlgorithm.clear();
	}

	public void writeDiscordances(Writer writer, AaSequence aa, String subtype) throws IOException {
		for (Algorithm a : Algorithm.knownAlgorithms()) {
			if(!scorePerAlgorithm.containsKey(a)) continue;
			
			for (Algorithm b : Algorithm.knownAlgorithms()) {
				if(a.ordinal() <= b.ordinal()) continue;
				if(!scorePerAlgorithm.containsKey(b)) continue;
				if(scorePerAlgorithm.get(a).getSir() == scorePerAlgorithm.get(b).getSir()) continue;
				doWriteDiscordances(writer, aa, subtype);
				return;
			}
		}
	}

	private void doWriteDiscordances(Writer writer, AaSequence aa, String subtype) throws IOException {
		String toWrite = "{ ";
		for (Iterator<AaMutation> iterator = aa.getAaMutations().iterator(); iterator.hasNext();) {
			AaMutation mut = iterator.next();
			String aaTo = mut.getAaMutation();
			String mutString = mut.getId().getMutationPosition()+(aaTo==null ? "d" : aaTo.toUpperCase());
			toWrite += mutString;
			
			if(iterator.hasNext())
				toWrite += ", ";
		}
		if(! aa.getAaInsertions().isEmpty())
			toWrite += ", ";
		for (Iterator<AaInsertion> iterator = aa.getAaInsertions().iterator(); iterator.hasNext();) {
			AaInsertion mut = iterator.next();
			toWrite += mut.getId().getInsertionPosition()+mut.getAaInsertion();
			if(iterator.hasNext())
				toWrite += ", ";
		}
		toWrite += " } ";
		
		double ssd = 0;
		boolean treated = false;
		for (Algorithm a : Algorithm.knownAlgorithms()) {
			if(!scorePerAlgorithm.containsKey(a)) continue;
			
			toWrite += a.toString()+": "+scorePerAlgorithm.get(a).getRule()+"; ";
			treated = scorePerAlgorithm.get(a).isTreated();
			
			for (Algorithm b : Algorithm.knownAlgorithms()) {
				if(a.ordinal() <= b.ordinal()) continue;
				if(!scorePerAlgorithm.containsKey(b)) continue;
				if(scorePerAlgorithm.get(a).equals(scorePerAlgorithm.get(b))) continue;
				ssd += Math.pow(scorePerAlgorithm.get(a).getGss() - scorePerAlgorithm.get(b).getGss(),2);
			}
		}
		if(ssd==0.0){
			throw new IllegalStateException();
		}
		toWrite += "diff="+Double.toString(ssd)+"; ";
		toWrite += "treated="+treated+"; ";
		toWrite += "subtype="+subtype+"\n";
		
		writer.write(toWrite);
		writer.flush();	
	}

}
