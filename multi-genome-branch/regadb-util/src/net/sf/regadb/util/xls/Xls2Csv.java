package net.sf.regadb.util.xls;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class Xls2Csv {
	
	public static void main(String[] args){
		Xls2Csv xls = new Xls2Csv();
		xls.process(new File(args[0]));
	}

	public Collection<File> process(File xlsFile){
		List<File> csvFiles = new ArrayList<File>();
		
		try {
			Workbook book = Workbook.getWorkbook(xlsFile);
			
			for(Sheet sheet : book.getSheets()){
				File f = new File(xlsFile.getAbsolutePath() +"_"+ sheet.getName() +".csv");
				process(sheet, f);
				csvFiles.add(f);
			}
			
			
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return csvFiles;
	}
	
	public void process(Sheet sheet, File out){
		try {
			if((sheet.getRows() + sheet.getColumns()) > 0){
				PrintStream ps = new PrintStream(new FileOutputStream(out));
				for(int i = 0; i < sheet.getRows(); ++i){
					StringBuffer line = new StringBuffer();
					boolean hasValues = false;
					for(int j = 0; j < sheet.getColumns(); ++j){
						if(!sheet.getCell(j, i).getContents().trim().equals("")) {
							hasValues = true;
						}
						line.append(","+ format(sheet.getCell(j, i).getContents()) +"");
					}
					if(hasValues)
						ps.println(line.substring(1));
				}
				ps.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String format(String value){
		return value;
	}
}
