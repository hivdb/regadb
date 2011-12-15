package net.sf.regadb.io.exportCsv;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;

public class MutationTable {
	
	public static final String GAP = "-";
	
	public static class MutationPosition implements Comparable<MutationPosition>{
		private int pos;
		private int insPos;
		
		public MutationPosition(int pos){
			this.pos = pos;
			this.insPos = -1;
		}
		
		public MutationPosition(int pos, int insPos){
			this.pos = pos;
			this.insPos = insPos;
		}
		
		public boolean isInsertion(){
			return insPos > -1;
		}
		
		@Override
		public boolean equals(Object o){
			if(this == o)
				return true;
			
			if(!(o instanceof MutationPosition))
				return false;
			
			MutationPosition p = (MutationPosition)o;
			return p.pos == this.pos && p.insPos == this.insPos;
		}
		
		@Override
		public int hashCode(){
			return ((insPos + 1) * 1000) + pos;
		}

		@Override
		public int compareTo(MutationPosition mp) {
			int c = pos - mp.pos;
			return c == 0 ? insPos - mp.insPos : c;
		}
		
		@Override
		public String toString(){
			if(isInsertion())
				if(insPos > 0)
					return "i"+ pos +"("+ insPos +")";
				else
					return "i"+ pos;
			else
				return pos +"";
		}
	}

	public static interface Mutation{
		public MutationPosition getPos();
		public String getAas();
	}

	public static class DefaultMutation implements Mutation{
		private MutationPosition pos;
		private String aas;
		
		public DefaultMutation(){
			this(-1, "", false);
		}
		
		public DefaultMutation(int pos, String aas){
			this(pos, aas, false);
		}
		
		public DefaultMutation(int pos, String aas, boolean insertion){
			this(pos, aas, insertion, insertion ? 0 : -1);
		}
		
		public DefaultMutation(int pos, String aas, boolean insertion, int insertionOrder){
			this.pos = new MutationPosition(pos, insertionOrder);
			this.aas = aas == null || aas.length() == 0 ? GAP : aas;
		}
		
		public DefaultMutation(String mutation){
			this();
			parseMutationString(mutation);
		}
		
		public DefaultMutation(MutationPosition pos, String aas){
			this.pos = pos;
			this.aas = aas;
		}
		
		private void parseMutationString(String m){
			if(m == null)
				return;
			
			m = m.trim();
			if(m.length() == 0)
				return;
			
			boolean insertion = (m.charAt(0) == 'i' || m.charAt(0) == '-');
			
			int a = 0;
			while(a < m.length() && !Character.isDigit(m.charAt(a)))
				++a;
			
			if(a < m.length()){
				int b = a+1;
				while(b < m.length() && Character.isDigit(m.charAt(b)))
					++b;
				
				pos = new MutationPosition(Integer.parseInt(m.substring(a, b)), insertion ? 0 : -1);
				aas = m.substring(b);
			}
		}
		
		@Override
		public MutationPosition getPos(){
			return pos;
		}
		
		@Override
		public String getAas(){
			return aas;
		}
		
		@Override
		public String toString(){
			return pos + aas;
		}
	}
	
	public static class DbAaMutation implements Mutation{
		private AaMutation mut;
		private MutationPosition pos;
		
		public DbAaMutation(AaMutation mut){
			this.mut = mut;
			pos = new MutationPosition(mut.getId().getMutationPosition());
		}

		@Override
		public MutationPosition getPos() {
			return pos;
		}

		@Override
		public String getAas() {
			return mut.getAaMutation() == null || mut.getAaMutation().length() == 0 ?
					GAP : mut.getAaMutation();
		}
	}
	
	public static class DbAaInsertion implements Mutation{
		private AaInsertion ins;
		private MutationPosition pos;
		
		public DbAaInsertion(AaInsertion ins){
			this.ins = ins;
			pos = new MutationPosition(ins.getId().getInsertionPosition(),
					ins.getId().getInsertionPosition());
		}

		@Override
		public MutationPosition getPos() {
			return pos;
		}

		@Override
		public String getAas() {
			return ins.getAaInsertion() == null || ins.getAaInsertion().length() == 0 ?
					GAP : ins.getAaInsertion();
		}
	}
	
	public static final Comparator<Mutation> MUTATION_COMPARATOR = new Comparator<Mutation>(){

		@Override
		public int compare(Mutation o1, Mutation o2) {
			int c = o1.getPos().compareTo(o2.getPos());
			return c == 0 ? o1.getAas().compareTo(o2.getAas()) : c;
		}
		
	};
	
	public static void main(String[] args){
		MutationTable t = new MutationTable();
		
		t.add("a", createMutations("1A","2B","3C"));
		t.add("b", createMutations("1A","2EB","3D"));
		t.add("c", createMutations("1A","i1B","2EB","3D"));
		
		t.writeCsvTable(System.out);
	}
	
	public static Collection<Mutation> createMutations(String... ms){
		List<Mutation> l = new ArrayList<Mutation>();
		
		for(String m : ms)
			l.add(new DefaultMutation(m));
		
		return l;
	}
	
	public static Mutation merge(Mutation a, Mutation b){
		if(!a.getPos().equals(b.getPos()))
			return null;
		
		StringBuilder aas = new StringBuilder(a.getAas());
		for(int i=0; i<b.getAas().length(); ++i){
			char c = b.getAas().charAt(i);
			if(aas.indexOf(c+"") == -1)
				aas.append(c);
		}
		
		return new DefaultMutation(a.getPos(), aas.toString());
	}

	private Map<String, TreeSet<Mutation>> sampleMutations = new TreeMap<String, TreeSet<Mutation>>();

	public MutationTable(){
	}
	
	public void add(String sampleId, Collection<Mutation> mutations){
		TreeSet<Mutation> set = new TreeSet<Mutation>(MUTATION_COMPARATOR);
		set.addAll(mutations);
		sampleMutations.put(sampleId, set);
	}
	
	private TreeMap<MutationPosition, Mutation> getAllPossibleMutations(){
		TreeMap<MutationPosition, Mutation> allmuts = new TreeMap<MutationPosition, Mutation>();
		
		for(Map.Entry<String, TreeSet<Mutation>> me : sampleMutations.entrySet()){
			for(Mutation mut : me.getValue()){
				Mutation m = allmuts.get(mut.getPos());
				if(m == null){
					allmuts.put(mut.getPos(), mut);
				}else{
					allmuts.remove(mut.getPos());
					Mutation mm = merge(m, mut);
					allmuts.put(mut.getPos(), mm);
				}
			}
		}
		
		return allmuts;
	}

	public void writeCsvTable(PrintStream out){
		TreeMap<MutationPosition, Mutation> allmuts = getAllPossibleMutations();
		
		out.print("sample_id");
		
		for(Mutation m : allmuts.values()){
			for(int i=0; i< m.getAas().length(); ++i){
				out.print(",");
				out.print(m.getPos());
				out.print(m.getAas().charAt(i));
			}
		}
		out.println();

		
		for(Map.Entry<String, TreeSet<Mutation>> smuts : sampleMutations.entrySet()){
			out.print(smuts.getKey());
			
			Iterator<Mutation> it = smuts.getValue().iterator();
			Mutation sm = it.hasNext() ? it.next() : null;
			
			for(Mutation am : allmuts.values()){
				while(sm != null
						&& sm.getPos().compareTo(am.getPos()) < 0
						&& it.hasNext())
					sm = it.next();
				
				for(int i=0; i<am.getAas().length(); ++i){
					out.print(",");
					
					char aa = am.getAas().charAt(i);
					
					out.print(sm != null
							&& sm.getPos().equals(am.getPos())
							&& sm.getAas().indexOf(aa) != -1);
				}
			}
			
			out.println();
		}
	}
}
