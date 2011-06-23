package net.sf.regadb.sequencedb;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaInsertionId;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaMutationId;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.ViralIsolate;

public class SequenceUtils {
	public static class SequenceDistance {
		public int numberOfDifferences;
		public int numberOfPositions;
		
		public double distance() {
			if (numberOfPositions != 0)
				return (double)numberOfDifferences / numberOfPositions;
			else 
				return 0.0;
		}
	}
	
	public static SequenceDistance distance(String alignment1, String alignment2, int from, int to) {
		if (alignment1.length() != alignment2.length())
			throw new RuntimeException("Alignments differ in size");
		
		SequenceDistance f = new SequenceDistance();
		
		for (int i = from; i < to; i++) {
			if (alignment1.charAt(i) != '-' && alignment2.charAt(i) != '-') {
				f.numberOfPositions++;
				if (alignment1.charAt(i) != alignment2.charAt(i))
					f.numberOfDifferences++;
			}
		}
		
		return f;
	}
	
	public static ViralIsolate combineViralIsolates(Collection<ViralIsolate> viralIsolates){
		if(viralIsolates.size() == 0)
			return null;
		
		ViralIsolate v = viralIsolates.iterator().next();
//		if(viralIsolates.size() == 1)
//			return v;
		
		ViralIsolate result = new ViralIsolate();
		result.setGenome(v.getGenome());
		
		Map<String, List<AaSequence>> aas = new TreeMap<String, List<AaSequence>>();
		
		for(ViralIsolate vi : viralIsolates){
			for(NtSequence nt : vi.getNtSequences()){
				for(AaSequence aa : nt.getAaSequences()){
					String key = aa.getProtein().getAbbreviation() +":"
						+ aa.getProtein().getOpenReadingFrame().getGenome().getOrganismName();
					
					List<AaSequence> list = aas.get(key);
					if(list == null){
						list = new LinkedList<AaSequence>();
						aas.put(key, list);
					}
					
					list.add(aa);
				}
			}
		}
		
		NtSequence nt = new NtSequence(result);
		result.getNtSequences().add(nt);
		
		for(List<AaSequence> aa : aas.values()){
			AaSequence a = combineAaSequences(aa);
			a.setNtSequence(nt);
			nt.getAaSequences().add(a);
		}
		
		return result;
	}
	
	static class Mut{
		public int pos;
		public String aa;
	}
	
	public static AaSequence combineAaSequences(Collection<AaSequence> aas){
		if(aas.size() == 0)
			return null;

		AaSequence a = aas.iterator().next();

//		if(aas.size() == 1)
//			return a;
		
		AaSequence result = new AaSequence();
		result.setProtein(a.getProtein());
		result.setFirstAaPos(a.getFirstAaPos());
		result.setLastAaPos(a.getLastAaPos());
		
		Map<Short, AaMutation> muts = new TreeMap<Short, AaMutation>();
		Map<Short, Map<Short, AaInsertion>> inss = new TreeMap<Short, Map<Short, AaInsertion>>();
		
		for(AaSequence aa : aas){
			if(aa.getLastAaPos() > result.getLastAaPos())
				result.setLastAaPos(aa.getLastAaPos());
			
			if(aa.getFirstAaPos() < result.getFirstAaPos())
				result.setFirstAaPos(aa.getFirstAaPos());
			
			for(AaMutation mut : aa.getAaMutations()){
				AaMutation cmut = muts.get(mut.getId().getMutationPosition());
				
				if(cmut == null){
					AaMutationId id = new AaMutationId(mut.getId().getMutationPosition(), result);
					cmut = new AaMutation();
					cmut.setId(id);
					cmut.setAaReference(mut.getAaReference());
					cmut.setAaMutation(mut.getAaMutation() == null ? "-" : mut.getAaMutation());
					result.getAaMutations().add(cmut);
					
					muts.put(id.getMutationPosition(), cmut);
				} else {
					String m = mut.getAaMutation() == null ? "-" : mut.getAaMutation();
					for(int i = 0; i < m.length(); ++i){
						char AA = m.charAt(i);
						if(!cmut.getAaMutation().contains(AA +""))
							cmut.setAaMutation(cmut.getAaMutation() + AA);
					}
				}
			}
			
			for(AaInsertion ins : aa.getAaInsertions()){
				AaInsertion cins = null;
				Map<Short, AaInsertion> cinso = inss.get(ins.getId().getInsertionPosition());
				if(cinso == null){
					cinso = new TreeMap<Short, AaInsertion>();
					inss.put(ins.getId().getInsertionPosition(), cinso);
				} else
					cins = cinso.get(ins.getId().getInsertionOrder());
				
				if(cins == null){
					AaInsertionId id = new AaInsertionId(
							ins.getId().getInsertionPosition(),
							result,
							ins.getId().getInsertionOrder());
					cins = new AaInsertion();
					cins.setId(id);
					cins.setAaInsertion(ins.getAaInsertion());
					result.getAaInsertions().add(cins);

					cinso.put(id.getInsertionOrder(), cins);
				} else {
					for(int i = 0; i < ins.getAaInsertion().length(); ++i){
						char AA = ins.getAaInsertion().charAt(i);
						if(!cins.getAaInsertion().contains(AA +""))
							cins.setAaInsertion(cins.getAaInsertion() + AA);
					}
				}
			}
		}
		
//		System.err.println(toString(result));
		
		return result;
	}
	
	static String toString(AaSequence a){
		StringBuilder sb = new StringBuilder();
		
		for(AaMutation m : a.getAaMutations())
			sb.append(toString(m)).append(" ");
		
		for(AaInsertion i : a.getAaInsertions())
			sb.append(toString(i)).append(" ");
		
		return sb.toString();
	}
	
	static String toString(AaMutation m){
		return m.getAaReference() + m.getId().getMutationPosition() + m.getAaMutation();
	}
	
	static String toString(AaInsertion i){
		return "*"+ i.getId().getInsertionPosition() +"("+ i.getId().getInsertionOrder() +")"+ i.getAaInsertion();
	}
}
