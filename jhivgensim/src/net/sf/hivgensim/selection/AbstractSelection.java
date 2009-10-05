package net.sf.hivgensim.selection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public abstract class AbstractSelection {
	
	private File completeTable;
	private File selectionTable;
	
	public AbstractSelection(File completeTable, File selectionTable) throws FileNotFoundException{
		if(!completeTable.exists()){
			throw new FileNotFoundException();
		}
		setCompleteTable(completeTable);
		setSelectionTable(selectionTable);
	}

	public File getCompleteTable() {
		return completeTable;
	}

	private void setCompleteTable(File completeTable) {
		this.completeTable = completeTable;
	}

	public File getSelectionTable() {
		return selectionTable;
	}

	private void setSelectionTable(File selectionTable) {
		this.selectionTable = selectionTable;
	}
	
	public String[] getNames(){
		Scanner s = null;
		try {
			s = new Scanner(getCompleteTable());
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
		String[] names = s.nextLine().trim().split(",");
		s.close();
		return names;
	}
	
	public int getNumberOfRows(){
		Scanner s = null;
		try {
			s = new Scanner(getCompleteTable());
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
		if(!s.hasNextLine()){
			return 0;
		}
		s.nextLine();
		int i = 0;		
		while(s.hasNextLine()){
			s.nextLine();
			i++;
		}
		s.close();
		return i;
	}
	
	public void select(){
		Scanner s = null;
		PrintStream ps = null;
		try {
			s = new Scanner(getCompleteTable());
			ps = new PrintStream(getSelectionTable());
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
		boolean[] sel = calculateSelection();
		while(s.hasNextLine()){
			String[] parts = s.nextLine().split(",");
			assert(sel.length == parts.length);
			boolean first = true;
			for(int i = 0; i < sel.length; i++){
				if(sel[i]){
					if(first){
						first = false;						
					}else{
						ps.print(",");
					}
					ps.print(parts[i]);
				}				
			}
			ps.println();
		}
		s.close();
		ps.flush();
		ps.close();		
	}
	
	protected abstract boolean[] calculateSelection();

}
