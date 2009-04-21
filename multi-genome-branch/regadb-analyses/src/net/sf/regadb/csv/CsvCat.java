/*
 * Created on Feb 17, 2004
 */
package net.sf.regadb.csv;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 */
public class CsvCat {

	public static void main(String[] args) {
		/*
		 * Open two csv files, and merge them columnwise.
		 */
		if(args.length != 3){
			System.err.println("Usage: csvcat in.csv in2.csv out.csv");
			return;
		}
		String file1 = args[0];
		String file2 = args[1];
        String prep1 = null;
        String prep2 = null;
        if (args.length == 4) {
            prep1 = args[2];
            prep2 = args[3];
        }

		Table table1;
		Table table2;
		try {
			table1 = new Table(new BufferedInputStream(new FileInputStream(file1)), false);
			table2 = new Table(new BufferedInputStream(new FileInputStream(file2)), false);

            if (prep1 != null) {
                ArrayList<String> column = new ArrayList<String>();
                column.add("file");
                for (int i = 1; i < table1.numRows(); ++i)
                    column.add(prep1);
                table1.addColumn(column, 0);

                column = new ArrayList<String>();
                column.add("file");
                for (int i = 1; i < table2.numRows(); ++i)
                    column.add(prep2);
                table2.addColumn(column, 0);
            }

			table1.append(table2);

			table1.exportAsCsv(System.out);
			System.out.flush();
		} catch (FileNotFoundException e) {
			System.err.println("Could not open file");
			System.exit(1);
		}
		
	}
}
