package net.sf.hivgensim.preprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import net.sf.hivgensim.fastatool.FastaScanner;
import net.sf.hivgensim.fastatool.FastaSequence;
import net.sf.hivgensim.fastatool.SelectionWindow;
import net.sf.regadb.align.Mutation;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.csv.Table;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.io.SymbolTokenization;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;

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
	public MutationTable(String fastaFilename, SelectionWindow[] windows) throws BioException, FileNotFoundException {
		super();
		createIdColumn();

		FastaScanner scan = new FastaScanner(new File(fastaFilename));
		if (!scan.hasNextSequence()) {
			return; // empty file
		}

		SymbolTokenization aatok = ProteinTools.getTAlphabet().getTokenization("token"); 
		SymbolList alignedRef = DNATools.createDNA(scan.nextSequence().getSequence());
		SymbolList alignedTarget;
		LocalAlignmentService las = new LocalAlignmentService();

		FastaSequence fs;
		while (scan.hasNextSequence()) {
			fs = scan.nextSequence();
			alignedTarget = DNATools.createDNA(fs.getSequence());
			createNewRow(fs.getId());
			for(Mutation m : las.getAlignmentResult(alignedTarget, alignedRef, true).getMutations()){
				for(SelectionWindow sw : windows){
					if(mutationInWindow(m,sw)){
						String protein = sw.getProtein().getAbbreviation();
						int position = m.getAaPos()-(sw.getProtein().getStartPosition()/3);
						for(Symbol aa : m.getTargetAminoAcids()){
							String mutString = protein + position + aatok.tokenizeSymbol(aa);
							addMutation(mutString);
						}
						//break; //already added so we don't need to check the mutation for other windows
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

	private boolean mutationInWindow(Mutation m, SelectionWindow sw){
		return m.getAaPos() >= (sw.getStartCheck()/3)+1
		&& m.getAaPos() <= (sw.getStopCheck()/3)+1;
	}	

}
