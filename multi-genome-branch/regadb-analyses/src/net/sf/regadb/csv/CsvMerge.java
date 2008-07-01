package net.sf.regadb.csv;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/*
 * Created on Jan 5, 2004
*/

/**
 * @author kdf
 */
public class CsvMerge {

	public static void main(String[] args) {
		if (args.length != 5) {
			System.err.println("Usage: csvmerge (inner|outer) file1 file2 key1 key2");
			System.exit(-1);
		}

		boolean innerJoin = args[0].equals("inner");
		String file1 = args[1];
		String file2 = args[2];
		int key1 = Integer.parseInt(args[3]);
		int key2 = Integer.parseInt(args[4]);

		Table table1;
		Table table2;
		try {
			System.err.println("Reading...");
			table1 = new Table(new BufferedInputStream(new FileInputStream(file1)), false);
			table2 = new Table(new BufferedInputStream(new FileInputStream(file2)), false);

			System.err.println("Merging...");
			table1.merge(table2, key1, key2, innerJoin);

			System.err.println("Saving...");
			table1.exportAsCsv(new BufferedOutputStream(System.out));
		} catch (FileNotFoundException e) {
			System.err.println("Could not open file");
			System.exit(1);
		}
	}
}
