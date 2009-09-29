package net.sf.hivgensim.preprocessing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.AaMutInsertion;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;

/**
 *  * 
 * 
 * @author gbehey0
 * 
 */
public class MutationTable extends Table {

	//TODO refactor the constructor and helper methods to make it cleaner/easier to understand.

	/**
	 * Creates a new MutationTable from a csvfile with 2 columns.
	 * The first column contains the ids.
	 * The second column contains the mutations separated by commas.
	 * 
	 * A mutation consists of 
	 * one char wild type AA followed by
	 * the position in the protein followed by
	 * one char AA mutation (optionally) followed by
	 * /one or more (other) AA mutation(s)
	 * 
	 * @param csvfile
	 */

	@Deprecated
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
							//							mt.addMutation(mut);			
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
	public MutationTable() {
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

	public MutationTable(String filename) throws FileNotFoundException{
		super(new InputStreamReader(new BufferedInputStream(new FileInputStream(filename))), false,',');
	}

	private Set<String> mutationNames;

	private void createHeader(Set<String> mutationNames){
		this.mutationNames = mutationNames;
		ArrayList<String> temp = new ArrayList<String>();
		for(String s : mutationNames){
			temp.clear();
			temp.add(s);
			addColumn(temp);
		}

	}
	
	private void createInfoHeader(Set<String> info){
		ArrayList<String> temp = new ArrayList<String>();
		for(String s : info){
			temp.clear();
			temp.add(s);
			addColumn(temp);
		}
	}

	private void completeNewRow(NtSequence seq, SelectionWindow[] windows){
		Set<AaSequence> aaSequences = seq.getAaSequences();
		for(AaSequence aaSequence : aaSequences){
			for(SelectionWindow win : windows){
				String sprotein = win.getProtein().getAbbreviation();
				String sorganism = win.getProtein().getOpenReadingFrame().getGenome().getOrganismName();
				if(!AaSequenceUtils.coversRegion(aaSequence, sorganism, sprotein)){
					continue;
				}
				Iterator<AaMutInsertion> muts = AaMutInsertion.getSortedMutInsertionList(aaSequence).iterator();
				//FIXME change it so this works even if pos doesn't start with one
				AaMutInsertion mut = muts.hasNext()? muts.next() : null;
				String ref = win.getReferenceAaSequence();
				for(int pos = win.getStart(); pos <= win.getStop(); pos++){
					if(mut == null){
						break;
					}

					if(mut.getPosition() == pos){
						char[] chars = mut.getAaMutationString().toCharArray();
						for(String s : mutationNames){
							if(s.matches(win.getProtein().getAbbreviation() + pos + ".*")){
								boolean found = false;
								if(chars.length == 0 && s.endsWith("del")){
									//deletion
									setValue(findInRow(0,s),numRows()-1,"y");
									found = true;
								}
								if(!found && !mut.isInsertion()){
									for(char c : chars){
										if(c == s.charAt(s.length()-1)){
											setValue(findInRow(0, s), numRows()-1, "y");
											found = true;
											break;
										}
									}
								}
								if(!found && mut.isInsertion()){
									for(char c : chars){
										if(c == s.charAt(s.length()-4)){
											setValue(findInRow(0, s), numRows()-1, "y");
											found = true;
											break;
										}
									}
								}
								if(!found){
									setValue(findInRow(0, s), numRows()-1, "n");
								}
							}
						}							
						mut = muts.hasNext()? muts.next() : null;
					}else{
						//reference
						for(String s : mutationNames){
							if(s.matches(win.getProtein().getAbbreviation() + pos + ".*")){
								if(ref.charAt(pos-win.getStart()) == s.charAt(s.length()-1)){
									setValue(findInRow(0, s), numRows()-1, "y");
								}
								else{
									setValue(findInRow(0, s), numRows()-1, "n");
								}
							}
						}							
					}
				}
			}
		}	
	}

	public void addSequence(NtSequence seq, SelectionWindow[] windows, ArrayList<String> infoCols){
		createNewRow(seq.getViralIsolate().getSampleId());
		for(String info : infoCols){
			setValue(findInRow(0, info), numRows()-1,"y");
		}
		completeNewRow(seq, windows);
	}

	public void addSequence(NtSequence seq, SelectionWindow[] windows){
		createNewRow(seq.getViralIsolate().getSampleId());
		completeNewRow(seq, windows);
	}

	public MutationTable(Set<String> mutationNames){
		createIdColumn();
		createHeader(mutationNames);
	}
	
	public MutationTable(Set<String> mutationNames, Set<String> info){
		createIdColumn();
		createInfoHeader(info);
		createHeader(mutationNames);
	}

	public MutationTable(List<NtSequence> seqlist, SelectionWindow[] windows){
		//assumes already removed sequences from query that have deletions in the windows
		int n = seqlist.size();
		System.err.println("Making mutation table containing "+n+" sequences.");

		createIdColumn();
		createHeader(getAllMutations(seqlist, windows));

		for(NtSequence seq : seqlist){
			addSequence(seq, windows);			
		}
	}

	private Set<String> getAllMutations(List<NtSequence> seqlist, SelectionWindow[] windows){
		Set<String> allMutations = new TreeSet<String>();
		for(NtSequence seq : seqlist){	
			allMutations.addAll(Utils.getAllMutations(seq, windows));
		}
		return allMutations;
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

	public void removeMutationsOutsideRange(int start,int stop){
		Pattern p = Pattern.compile("[A-Za-z]+([0-9]+).");//TODO del and ins
		Matcher m = null;
		ArrayList<Integer> toBeDeleted = new ArrayList<Integer>();
		ArrayList<String> header = getRow(0);
		for(int i = 0 ; i < header.size();i++){
			String s = header.get(i);
			m = p.matcher(s);
			if(!m.matches()){
				System.err.println(s + " isn't a mutation string");
				continue;
			}
			int pos = Integer.parseInt(m.group(1));
			if(pos < start || pos > stop){
				toBeDeleted.add(i);				
			}			
		}
		deleteColumns(toBeDeleted);
	}
	
	public void removeNonPrevalingMutations(ArrayList<String> ignoredHeaders){
		ArrayList<Integer> toBeDeleted = new ArrayList<Integer>();
		ArrayList<String> col;
		int i;
		for(String mut : getRow(0)){
			if(ignoredHeaders.contains(mut)){
				continue;
			}
			i = findColumn(mut);
			col = getColumn(i);
			if(!col.contains("y")){
				toBeDeleted.add(i);
			}
		}
		deleteColumns(toBeDeleted);
	}

	private void createIdColumn(){
		ArrayList<String> ids = new ArrayList<String>();
		ids.add("id");
		addColumn(ids, 0);
	}

	private void createNewRow(String id){
		ArrayList<String> newrow = new ArrayList<String>(numColumns());
		newrow.add(id);
		
		//mutations
		for(int i = 0; i < numColumns(); i++){
			if(MUT_PATTERN.matcher(valueAt(0, i)).matches()){
				newrow.add("");
			}else{
				newrow.add("n");
			}			
		}
		addRow(newrow);		
	}
	
	public static final Pattern MUT_PATTERN = Pattern.compile("([A-Z]+)([0-9]+)([A-Z]*|del|ins)");	
	
}
