package net.sf.regadb.io.db.mateibals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.regadb.util.xls.ExcelTable;

public class MateibalsUtils {
	public static Date createDate(SimpleDateFormat df, String date) {
		try {
			return df.parse(date);
		} catch (ParseException pe) {
			pe.printStackTrace();
			return null;
		}
	}
	
	public static Date parseDate(SimpleDateFormat df, String date, Date lower, int row, String colName) {
		String error = "Illegal date: row=\"" + row + "\" col=\"" + colName + "\" date=\""+date + "\""; 
		
		if (!date.equals("")) {
			try {
				Date birthDate = df.parse(date);
				
				if (birthDate.before(lower) || birthDate.after(new Date())) {
					System.err.println(error);
					return null;
				} else {
					return birthDate;
				}
			} catch (ParseException e) {
				System.err.println(error);
				return null;
			}
		} else {
			return null;
		}
	}
	
    public static String parseViralLoad(int row, String value_orig) {
        String value = value_orig;
        
        if(value==null || "".equals(value)) {
            return null;
        }
        
        String val = null;
        
        value = value.replace(',', '.');
        
        if(Character.isDigit(value.charAt(0))) {
            value = "=" + value;
        }
        if(value.charAt(0)=='>' || value.charAt(0)=='<' || value.charAt(0)=='=') {
            try {
                Double.parseDouble(value.substring(1));
                val = value;
            } catch(NumberFormatException nfe) {
                System.err.println("Cannot parse Viral Load value at row=\"" + row + "\" value=\"" + value + "\"");
                return null;
            }
        }
        
        return val;
    }
    
	public static String getValue(ExcelTable table, int row, String columnName) {
		int col = -1;
		for (int c = 0; c < table.columnCount(); c++) {
			if (table.getCell(0, c).trim().equals(columnName)) {
				col = c;
				break;
			}
		}
		
		if (col == -1)
			System.err.println("No column named \"" + columnName + "\"");
		
		return table.getCell(row, col).trim();
	}
}
