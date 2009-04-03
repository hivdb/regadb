package net.sf.hivgensim.preprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.AaMutInsertion;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;

import org.biojava.bio.BioException;

/**
 * needs to be changed to use the regadb alignment algorithm fully
 * 
 * 
 * @author gbehey0
 * 
 */
public class MutationTable extends Table {

	/**
	 * Creates a new MutationTable from a csvfile with 2 columns.
	 * The first column contains the ids.
	 * The second column contains the mutations separated by commas.
	 * 
	 * A mutation contains of 
	 * one char wild type AA followed by
	 * the position in the protein followed by
	 * one char AA mutation (optionally) followed by
	 * /one or more (other) AA mutation(s)
	 * 
	 * @param csvfile
	 */

	public static MutationTable parseMutationTable(File csvfile){
		MutationTable mt = new MutationTable();
		mt.createIdColumn();

		Scanner s = null;
		try {
			s = new Scanner(csvfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		s.nextLine(); // ignore header

		while(s.hasNextLine()){
			String[] columns = s.nextLine().split("\t");
			if(columns.length == 2){
				String patientId = columns[0];
				String[] mutations = columns[1].split(",");

				mt.createNewRow(patientId);
				for(String mutString : mutations){
					if(mutString.matches("[A-Z][0-9]{1,3}[A-Z](/[A-Z])*")){
						mutString = mutString.replaceFirst(((Character)mutString.charAt(0)).toString(), "PR");
						String allmuts[] = mutString.split("/");

						String posString = "";
						for(String mut : allmuts){
							mut = mut.trim();
							if(mut.length() == 1){
								mut = posString + mut;
							}else{
								posString = mut.substring(0,mut.length()-1);						
							}
							mt.addMutation(mut);			
						}
					}
				}
			}
		}
		return mt;
	}

	/**
	 * default constructor
	 */
	private MutationTable() {
		super();
	}

	/**
	 * create a new mutationtable from a  csv-exported mutationtable
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public MutationTable(File file) throws FileNotFoundException{
		super(new FileInputStream(file),false);	
	}

	/**
	 * create new mutationtable from a fastafile
	 * @param fastaFilename
	 * @param windows
	 * @throws BioException
	 * @throws FileNotFoundException
	 */
//	@Deprecated
//	public MutationTable(String fastaFilename, SelectionWindow[] windows) throws BioException, FileNotFoundException {
//		super();
//		createIdColumn();
//
//		FastaScanner scan = new FastaScanner(new File(fastaFilename));
//		if (!scan.hasNextSequence()) {
//			return; // empty file
//		}
//
//		SymbolTokenization aatok = ProteinTools.getTAlphabet().getTokenization("token"); 
//		SymbolList alignedRef = DNATools.createDNA(scan.nextSequence().getSequence());
//		SymbolList alignedTarget;
//		LocalAlignmentService las = new LocalAlignmentService();
//
//		FastaSequence fs;
//		while (scan.hasNextSequence()) {
//			fs = scan.nextSequence();
//			alignedTarget = DNATools.createDNA(fs.getSequence());
//			createNewRow(fs.getId());
//			for(Mutation m = null;;){//TODO
//				//			for(Mutation m : las.getAlignmentResult(alignedTarget, alignedRef, true).getMutations()){
//				for(SelectionWindow sw : windows){
//					if(mutationInWindow(m,sw)){
//						String protein = sw.getProtein().getAbbreviation();
//						int position = m.getAaPos()-(sw.getProtein().getStartPosition()/3);
//						for(Symbol aa : m.getTargetAminoAcids()){
//							String mutString = protein + position + aatok.tokenizeSymbol(aa);
//							addMutation(mutString);
//						}
//						//break; //already added so we don't need to check the mutation for other windows
//					}
//				}
//			}
//		}				
//	}

	public MutationTable(List<NtSequence> seqlist, SelectionWindow[] windows){
		//assumes already removed sequences from query that have deletions in the windows
		int n = seqlist.size();
		System.err.println("Making mutation table containing "+n+" sequences.");
		createNewRow("id");
		for(NtSequence seq : seqlist){
			createNewRow(seq.getViralIsolate().getSampleId());
			Set<AaSequence> aaSequences = seq.getAaSequences();
			for(AaSequence aaSequence : aaSequences){
				for(SelectionWindow win : windows){
					String sprotein = win.getProtein().getAbbreviation();
					String sorganism = win.getProtein().getOpenReadingFrame().getGenome().getOrganismName();
					if(!QueryUtils.isSequenceInRegion(aaSequence, sorganism, sprotein)){
						continue;
					}
					Iterator<AaMutInsertion> muts = AaMutInsertion.getSortedMutInsertionList(aaSequence).iterator();
					AaMutInsertion mut = muts.hasNext()? muts.next() : null;
					String ref = win.getReferenceAaSequence();
					
					for(int pos = win.getStart(); pos <= win.getStop(); pos++){
						if(mut != null && mut.getPosition() == pos && !mut.isSilent()){
							if(!mut.isInsertion() ){
								for(char m : mut.getAaMutationString().toCharArray()){
									addMutation(win.getProtein().getAbbreviation() + pos + m);
								}
							}else{
								//TODO insertions
							}
						}else{
							//reference
							addMutation(win.getProtein().getAbbreviation() + pos + ref.charAt(pos-win.getStart()));
						}
						
						if(mut != null && mut.getPosition() == pos){
							mut = muts.hasNext()? muts.next() : null;
						}
					}
				}
			}
		}
	}

	/**
	 * remove all columns representing an insertion
	 */
	public void removeInsertions(){
		deleteColumns(".*ins.*");
	}

	/**
	 * remove all columns with unknown AA-mutation
	 * these are mutations indicated with a star (like PR9*)
	 */
	public void removeUnknownMutations(){
		deleteColumns("[A-Za-z]+[0-9]+\\*");
	}

	private void createIdColumn(){
		//create id column
		ArrayList<String> ids = new ArrayList<String>();
		ids.add("id");
		addColumn(ids, 0);
	}	

	private void addMutation(String mutationString) {
		int nbCol = findInRow(0, mutationString);
		if(nbCol == -1){ //new mut
			createNewColumn(mutationString);
		}else{ //adjust mut
			setValue(nbCol, numRows()-1, "y");									
		}
	}

	private void createNewColumn(String mutation){
		ArrayList<String> newcol = new ArrayList<String>(numRows());
		newcol.add(mutation);
		for(int i = 1;i<numRows();i++){
			if(i != numRows()-1){
				newcol.add("n");
			}else{
				newcol.add("y");
			}			
		}
		addColumn(newcol);		
	}

	private void createNewRow(String id){
		ArrayList<String> newrow = new ArrayList<String>(numColumns());
		newrow.add(id);
		for(int i = 1;i<numColumns();i++){
			newrow.add("n");						
		}
		addRow(newrow);		
	}

//	private boolean mutationInWindow(Mutation m, SelectionWindow sw){
//		return m.getAaPos() >= (sw.getStartCheck()/3)+1
//		&& m.getAaPos() <= (sw.getStopCheck()/3)+1;
//	}	

}
