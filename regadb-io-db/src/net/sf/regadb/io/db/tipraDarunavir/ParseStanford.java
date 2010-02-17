package net.sf.regadb.io.db.tipraDarunavir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import net.sf.regadb.csv.Table;

public class ParseStanford 
{
	Table _isoalates=new Table();
	Table _therapies=new Table();
	String _patient= new String();
	public ParseStanford() 
	{
		ArrayList<String> isolateHeader= new ArrayList<String>();
		ArrayList<String> therapyHeader= new ArrayList<String>();
		isolateHeader.add("patient_id");
		isolateHeader.add("isolate");
		isolateHeader.add("region");
		isolateHeader.add("year");
		isolateHeader.add("species");
		isolateHeader.add("subtype");
		isolateHeader.add("source");
		isolateHeader.add("cloneMethod");
		_isoalates.addRow(isolateHeader);

		therapyHeader.add("patient_id");
		therapyHeader.add("order");
		therapyHeader.add("regimen");
		therapyHeader.add("weeks");
		_therapies.addRow(therapyHeader);

	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */

	public static void main(String[] args) throws FileNotFoundException 
	{
		String workPath="/home/rsanged0/project/TPV/data/stanford/therapy1";
		String workPath2="/home/rsanged0/project/TPV/data/stanford/therapy1";
		String fastaFile="/home/rsanged0/project/TPV/data/stanford/TPV_cross.fasta";
		ParseStanford parseStanford= new ParseStanford();
		int wk1 = parseStanford.parseFiles(workPath);
		int wk2 = parseStanford.parseFiles(workPath2);
		
		System.err.println("Finished Iporting a total of " + (wk1+wk2 ));
		parseStanford.exportToCsv();
		try {
			parseStanford.fasta2Csv(fastaFile);
		} catch (IOException e) 
		{

			e.printStackTrace();
		}
		
	}

	private void fasta2Csv(String fastaFile) throws IOException 
	{
		Table csvTable=new Table();
		BufferedReader reader;
		String patient_id;
		String seqId;
		String seqDate;
		String seqLine ;

		reader = new BufferedReader(new InputStreamReader(new FileInputStream(fastaFile)));

		
		patient_id="patient_id";
		 seqId="seqId";
		 seqDate="seqDate";
		 seqLine = "genotype";
		 
		 String line = reader.readLine();
		while (line!=null)
		{
			ArrayList<String> row=new ArrayList<String>();
			
			if(line.startsWith(">"))
			{
				
				row.add(patient_id);
				row.add(seqId);
				row.add(seqDate);
				row.add(seqLine);
				//System.err.println(patient_id + "," +seqId + "," + seqDate + ","+ seqLine);
				csvTable.addRow(row);
				seqLine = "";
				seqId=line.substring(1);
				String [] seqHeader = line.split("_");
				patient_id=seqHeader[0].substring(1);
				String [] dateParts=seqHeader[1].split("-");
				seqDate=dateParts[1]+"/"+"01/"+dateParts[0];
				
			}
			
			else
			{
				seqLine+=line;
			}
			line = reader.readLine();
		}
		String fileCsv=fastaFile.substring(0,fastaFile.indexOf(".fasta"))+".csv";
		csvTable.exportAsCsv(new FileOutputStream(fileCsv));
		csvTable.merge(_therapies, 0, 0, false) ;//merge to geno_therapy
		String fileCsvgenotherapy=fileCsv.substring(0,fileCsv.lastIndexOf("/")) + "/therapy_and_geno.csv";
		csvTable.exportAsCsv(new FileOutputStream(fileCsvgenotherapy));
		

	}

	private void exportToCsv() 
	{

		try {
			_isoalates.exportAsCsv(new FileOutputStream("/home/rsanged0/project/TPV/data/stanford/patients_parsed.csv"));
			_therapies.exportAsCsv(new FileOutputStream("/home/rsanged0/project/TPV/data/stanford/therapy_parsed.csv"));
		} catch (FileNotFoundException e) 
		{

			e.printStackTrace();
		}

	}

	public int parseFiles(String workPath) 
	{
		File folder = new File(workPath);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			//System.err.println("Parsing File " + listOfFiles[i].getName() + " ...");
			try {
				parsePatientIsolate(listOfFiles[i].getAbsoluteFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return listOfFiles.length;

	}

	public  void parsePatientIsolate(File fPath) throws IOException 
	{
		try 
		{
			BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(fPath)));

			String line = reader.readLine();
			while (line!=null)
			{

				if (line.indexOf("<TD WIDTH=14%><b>Clone Method</b></TD></TR>") != -1)
				{

					String patient;
					String isolate;
					String region;
					String year;
					String species;
					String subtype;
					String source;
					String cloneMethod;


					ArrayList<String> isolateRow= new ArrayList<String>();
					isolateRow= new ArrayList<String>();
					line = reader.readLine(); //advance one line
					line = reader.readLine();// and another
					patient=getFieldValue(line,"<TR><TD>","</TD>", false);
					_patient=patient;
					//list.add(g);
					line=reader.readLine();

					isolate=getFieldValue(line,"<TD>" ,"</TD>", false);
					line=reader.readLine();

					region=getFieldValue(line, "<TD>", "</TD>", false);
					line=reader.readLine();

					year=getFieldValue(line,"<TD>" ,"</TD>", false );
					line=reader.readLine();
					species=getFieldValue(line,"<TD>" ,"</TD>", false );
					line=reader.readLine();
					subtype=getFieldValue(line,"<TD>" , "</TD>", false);
					line=reader.readLine();
					source=getFieldValue(line,"<TD>" , "</TD>",false);
					line=reader.readLine();
					cloneMethod=getFieldValue(line,"<TD>" , "</TD>",false);
					isolateRow.add(patient);
					isolateRow.add(isolate);
					isolateRow.add(region);
					isolateRow.add(year);
					isolateRow.add(species);
					isolateRow.add(subtype);
					isolateRow.add(source);
					isolateRow.add(cloneMethod);
					_isoalates.addRow(isolateRow);
					//System.err.println(patient +","+isolate+","+region+","+year+","+species+","+subtype+","+source+","+cloneMethod);

				}
				if (line.indexOf("<TD WIDTH=20%><b>Weeks</b>") != -1)
				{						
					boolean firstEntry=true;

					while(line.indexOf("</TABLE><BR><p><div class='block2'><div class='title2'>Protease Sequence") == -1)
					{
						String   order=new String();
						String   regimen;
						String   weeks=new String();
						ArrayList<String> therapyRow= new ArrayList<String>();


						if (firstEntry)
						{
							order=getFieldValue(line,"</TR><TR><TD>","</TD>",false);
							firstEntry=false;
						}
						else
						{
							order=getFieldValue(line,"</TR><TR><TD>","</TD>",true);
						}

						line=reader.readLine();
						regimen=getFieldValue(line,"<TD>" ,"</TD>", false );
						line=reader.readLine();
						if (firstEntry)
						{
							weeks=getFieldValue(line,"<TD>" ,"</TD>" ,true);
						}
						else
						{
							weeks=getFieldValue(line,"<TD>" ,"</TD>" ,false);
						}

						therapyRow.add(_patient);
						therapyRow.add(order);
						therapyRow.add(regimen);
						therapyRow.add(weeks);
						//System.err.println(_patient + "," + order + "," + regimen + "," + weeks);
						_therapies.addRow(therapyRow);

					}

					//line=reader.readLine(); not neccessary
				}



				line = reader.readLine();
			}


		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}

	private String getFieldValue(String line, String leftString, String rightString,
			boolean lastIndex) 
	{
		int startpos= line.indexOf(leftString);
		int endpos;
		if(lastIndex)
		{
			endpos = line.lastIndexOf(rightString);
		}
		else
			endpos = line.indexOf(rightString);


		int leftOffSet=leftString.length();
		return line.substring(startpos +leftOffSet,endpos );
	}

}
