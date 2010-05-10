package net.sf.regadb.io.db.mateibals;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.util.xls.ExcelTable;

public class ImportMateibalsIsolates {
	public static void main(String [] args) {
		ImportMateibalsIsolates importIsolates = new ImportMateibalsIsolates(new File(args[0]), new File(args[1]));
		importIsolates.run();
	}
	
	private ExcelTable table = new ExcelTable("dd.MM.yyyy");
	private Map<String, Integer> colNameMappings = new HashMap<String, Integer>();
	private Map<String, String> sequences = new HashMap<String, String>();
	private Map<String, String> sequencesInfo = new HashMap<String, String>();
	
	public ImportMateibalsIsolates(File xlsFile, File fastaDir) {
		try {
			table.loadFile(xlsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int c = 0; c < table.columnCount(); c++) {
			String header = table.getCell(0, c).trim();
			if (header.equals("")) {
				System.err.println("Empty col name at column " + c);
				return;
			} else {
				colNameMappings.put(header, c);
			}
		}
		
		for (File d : fastaDir.listFiles()) {
			for (File fasta : d.listFiles()) {
				if (fasta.isFile()) {
					FastaRead fr = FastaHelper.readFastaFile(fasta, true);

					switch (fr.status_) {
			        	case Valid:
			            case ValidButFixed:
			            	String name = fasta.getName();
			            	name = name.substring(0, name.lastIndexOf('.'));
			            	name = fixIsolateName(name);
			            	
			            	if (sequences.put(name, fr.xna_) != null) {
			            		System.err.println("Value exists already for isolate: " + name);
			            	}
			                break;
			            case MultipleSequences:
			            case FileNotFound:
			            case Invalid:
			                System.err.println("invalid fasta " + fasta.getAbsolutePath());
			                continue;
					}
				}
			}
		}
	}
	
	public void run() {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		df.setLenient(false);
		
		Set<String> county = new HashSet<String>();
		Set<String> residence = new HashSet<String>();
		Set<String> epid = new HashSet<String>();
		
		for (int r = 1; r < table.rowCount(); r++) {
			Date drawnDate = 
				MateibalsUtils.parseDate(df, getValue(r, "Drawn date"), MateibalsUtils.createDate(df, "01.01.2000"), r + 1, "Drawn date");
			Date birthDate = 
				MateibalsUtils.parseDate(df, getValue(r, "Birth date"), MateibalsUtils.createDate(df, "01.01.1920"), r + 1, "Drawn date");
						
			residence.add(getValue(r, "Residence"));
			
			county.add(getValue(r, "County"));
			
			epid.add(getValue(r, "Epidem info"));
			epid.add(getValue(r, "extra"));
			
			if (sequences.get(fixIsolateName(getValue(r, "Registration No"))) == null) {
				//System.err.println("no seq:" + getValue(r, "Registration No"));
			}
		}
		
		for (Map.Entry<String, String> e : sequencesInfo.entrySet()) {
			//System.err.println( e.getValue());
		}
		
		for (String c  : county) {
			System.err.println(c);
		}
		
		System.err.println(table.rowCount());
	}
	
	private String getValue(int r, String string) {
		return MateibalsUtils.getValue(table, r, string);
	}
	
	public String fixIsolateName(String isolateName) {
		StringBuilder buffer = new StringBuilder();
		
		StringBuilder infoBuffer = new StringBuilder();
		
		for (int i = 0; i < isolateName.length(); i++) {
			char c = isolateName.charAt(i);
			if (Character.isDigit(c) || c == '_') 
				buffer.append(c);
			else 
				infoBuffer.append(c);
		}
		
		if(infoBuffer.toString().trim().length() > 0)
			sequencesInfo.put(buffer.toString(), infoBuffer.toString().trim());
		
		return buffer.toString();
	}
}
