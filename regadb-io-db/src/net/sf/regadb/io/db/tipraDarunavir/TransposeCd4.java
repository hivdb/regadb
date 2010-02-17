package net.sf.regadb.io.db.tipraDarunavir;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import net.sf.regadb.csv.Table;

public class TransposeCd4 {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException 
	{
		Table cd4Table = new Table(new FileInputStream("/home/rsanged0/project/TPV/data/spain/cd4_not_transposed.csv"), false);
		transposeCd4File(cd4Table, "/home/rsanged0/project/TPV/data/spain/cd4_transposed.csv");
		//viral load
		Table vlTable = new Table(new FileInputStream("/home/rsanged0/project/TPV/data/spain/vl_not_transposed.csv"), false);
		transposeVlFile(vlTable, "/home/rsanged0/project/TPV/data/spain/vl_transposed.csv");

	}
	public static void transposeCd4File(Table cd4Table, String newCsvFile) 
	{
		Table table=new Table();

		ArrayList<String> header = new ArrayList<String>();
		ArrayList<String> row;
		header.add("patient_id");
		header.add("cd4");
		header.add("cd4Date");
		table.addRow(header);
		for(int i =1;i<cd4Table.numRows();i++)
		{
			String Patient_id=cd4Table.valueAt(0, i);

			for(int j = 3; j<cd4Table.numColumns()-3; j+=2) 
			{
				row = new ArrayList<String>();
				String vlValue=cd4Table.valueAt(j, i);
				String vlDate=cd4Table.valueAt(j+1, i);
				if(cd4Table.valueAt(j, i).length()==0)
				{

				}else
				{
					row.add(Patient_id);
					row.add(vlValue);
					row.add(vlDate);
					System.err.println(Patient_id + "; "+ vlValue+ ";"+ vlValue.length() +"; "+ vlDate);
					table.addRow(row);
				}
			}

		}	
		try {
			table.exportAsCsv(new FileOutputStream(newCsvFile));
		} catch (FileNotFoundException e) 
		{

			e.printStackTrace();
		}
	}
	public static void transposeVlFile(Table vlTable, String newCsvFile) 
	{
		Table table=new Table();

		ArrayList<String> header = new ArrayList<String>();
		ArrayList<String> row;
		header.add("patient_id");
		header.add("vl");
		header.add("vlDate");
		table.addRow(header);
		for(int i =1;i<vlTable.numRows();i++)
		{
			String Patient_id=vlTable.valueAt(0, i);

			for(int j = 3; j<vlTable.numColumns()-3; j+=2) 
			{
				row = new ArrayList<String>();
				String Cd4Count=vlTable.valueAt(j, i);
				String cd4Date=vlTable.valueAt(j+1, i);
				if(vlTable.valueAt(j, i).length()==0)
				{

				}else
				{
					row.add(Patient_id);
					row.add(Cd4Count);
					row.add(cd4Date);
					System.err.println(Patient_id + "; "+ Cd4Count+ ";"+ Cd4Count.length() +"; "+ cd4Date);
					table.addRow(row);
				}
			}

		}	
		try {
			table.exportAsCsv(new FileOutputStream(newCsvFile));
		} catch (FileNotFoundException e) 
		{

			e.printStackTrace();
		}
	}
}
