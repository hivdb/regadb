package net.sf.hivgensim.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.sf.hivgensim.queries.GetDrugClassNaiveAASequences;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;

public class ResistanceMutationsInNaive {

	private class PositionWithAA {
		private int pos;
		private char aa;

		public PositionWithAA(int pos, char aa){
			this.pos = pos;
			this.aa = aa;
		}

		public int getPos() {
			return pos;
		}

		public char getAa() {
			return aa;
		}

		public String toString(){
			return ""+getPos()+getAa();
		}

		@Override
		public boolean equals(Object obj) {
			if(!obj.getClass().equals(this.getClass())){
				return false;
			}
			PositionWithAA other = (PositionWithAA) obj;
			return (other.getAa()==this.getAa() && other.getPos()==this.getPos());
		}
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		new ResistanceMutationsInNaive().checkNaive(args);
	}

	private void checkNaive(String[] files) throws NumberFormatException, IOException {
		final HashMap<String, Set<PositionWithAA>> mutsForProtein = new HashMap<String, Set<PositionWithAA>>();
		for(String file: files){
			BufferedReader read = new BufferedReader(new FileReader(file));
			String line;
			while((line = read.readLine()) != null){
				String[] data = line.split(",");
				String protein = data[0];
				int pos = Integer.parseInt(data[1]);
				HashSet<PositionWithAA> muts = new HashSet<PositionWithAA>();
				for(char mut: data[2].toCharArray()){
					muts.add(new PositionWithAA(pos, mut));
				}
				if(mutsForProtein.containsKey(protein)){
					mutsForProtein.get(protein).addAll(muts);
				} else {
					mutsForProtein.put(protein, muts);
				}
			}
		}

		for(String protein : mutsForProtein.keySet()){
			System.out.print(protein);
			String[] drugClasses = DrugGenericUtils.getDrugClassForProtein(protein);

			new FromSnapshot(new File("admin"), new GetDrugClassNaiveAASequences(drugClasses, new IQuery<AaSequence>() {

				@Override
				public void process(AaSequence input) {

					int seqMuts = 0;
					Set<PositionWithAA> aaForProtein = mutsForProtein.get(input.getProtein().getAbbreviation());
					for (AaMutation seqMut : input.getAaMutations()) {
						int pos = seqMut.getId().getMutationPosition();
						char aa = seqMut.getAaMutation().charAt(0);
						if(seqMut.getAaMutation().length() > 1) throw new IllegalStateException();
						if(aaForProtein.contains(new PositionWithAA(pos, aa))){
							seqMuts++;
						}
					}
					System.out.print(seqMuts);
				}

				@Override
				public void close() {
					System.out.println();
				}
			}));
		}
	}
}
