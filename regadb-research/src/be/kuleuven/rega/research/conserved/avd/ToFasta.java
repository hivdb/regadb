package be.kuleuven.rega.research.conserved.avd;

import java.io.FileWriter;
import java.io.IOException;

import net.sf.regadb.csv.Table;

public class ToFasta {
	public static void main(String [] args) throws IOException {
		Table t = Table.readTable(args[0]);
		FileWriter fw = new FileWriter(args[1]);
		for(int i =0; i<t.numRows(); i++) {
			String id = t.valueAt(0, i);
			String sequence = t.valueAt(2, i);
			fw.write(">"+id+"\n");
			fw.write(sequence+"\n");
		}
		fw.close();
	}
}
