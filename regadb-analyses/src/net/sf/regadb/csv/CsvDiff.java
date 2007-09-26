/*
 * Created on Apr 28, 2005
 */
package net.sf.regadb.csv;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * @author kdforc0
 */
public class CsvDiff {

	public static void main(String[] args) throws FileNotFoundException {
        
        if(args.length<2) {
            System.err.println("Usage: csvdiff file1 file2 (output1)");
            return;
        }
		/*
		 * find changes from n to y in a column. if there is such a change,
		 * then create a variable s$colname and indicate s,-,r when there are
		 * changes of n-y, no change, or y-n.
		 */
		Table t1 = new Table(new BufferedInputStream(new FileInputStream(args[0])), false);
		Table t2 = new Table(new BufferedInputStream(new FileInputStream(args[1])), false);
		Table result = new Table();
		ArrayList<Integer> columns = new ArrayList<Integer>();

		result.addColumn(t1.getColumn(0), 0);
		columns.add(0);

		final int method = 3;

		if (method == 1) {
			for (int i = 0; i < t1.numColumns(); ++i) {
				ArrayList<String> s = new ArrayList<String>();
				s.add("s" + t1.valueAt(i, 0));
				ArrayList<String> r = new ArrayList<String>();
				r.add("r" + t1.valueAt(i, 0));
				for (int j = 1; j < t1.numRows(); ++j) {
					if (t1.valueAt(i, j).equals("n") && t2.valueAt(i, j).equals("y"))
						s.add("y");
					else
						s.add("n");
					if (t1.valueAt(i, j).equals("y") && t2.valueAt(i, j).equals("n"))
						r.add("y");
					else
						r.add("n");
				}
			
				result.addColumn(s, result.numColumns());
				result.addColumn(r, result.numColumns());
			}
		}

		if (method == 2) {
			final int S_R_threshold = 3;
			final int Y_N_threshold = t1.numRows() / 10;
			for (int i = 0; i < t1.numColumns(); ++i) {
				int y = 0, r = 0, n = 0, s = 0;
				ArrayList<String> c = new ArrayList<String>();
				c.add(t1.valueAt(i, 0));
				for (int j = 1; j < t1.numRows(); ++j) {
					if (t1.valueAt(i, j).equals(""))
						c.add(t2.valueAt(i, j));
					else if (t2.valueAt(i, j).equals(""))
						c.add(t1.valueAt(i, j));
					else if (t1.valueAt(i, j).equals("y"))
						if (t2.valueAt(i, j).equals("y")) {
							c.add("y");
							++y;
						} else {
							c.add("r");
							++r;
						}
					else
						if (t2.valueAt(i, j).equals("y")) {
							c.add("s");
							++s;
						} else {
							c.add("n");
							++n;
						}
				}

				if ((s >= S_R_threshold) || (r >= S_R_threshold)
					|| (((y + s) >= Y_N_threshold) && ((n + r) >= Y_N_threshold))) {
						result.addColumn(c, result.numColumns());
						columns.add(new Integer(i));
					}
			}

			for (int j = 1; j < t1.numRows(); ++j) {
				ArrayList<String> r = new ArrayList<String>();
				for (int i = 0; i < result.numColumns(); ++i) {
					int ii = ((Integer) columns.get(i)).intValue();
					r.add(t1.valueAt(ii, j));
				}
				result.rows.add(r);
			}
		
			ArrayList<String> t = new ArrayList<String>();
			t.add("eLPV");
			for (int j = 1; j < t1.numRows(); ++j) {
				t.add("y");
			}
			for (int j = 1; j < t1.numRows(); ++j) {
				t.add("n");
			}
			result.addColumn(t, result.numColumns());
		}
		
		if (method == 3) {
			final int S_R_threshold = 3;
			final int Y_N_threshold = t1.numRows() / 10;

			/*
			 * keep original variable if reaches Y/N threshold
			 * make new variable with s, r, -.
			 */

			for (int i = 0; i < t1.numColumns(); ++i) {
				int y = 0, r = 0, n = 0, s = 0;
				ArrayList<String> c = new ArrayList<String>();
				String nn = t1.valueAt(i, 0);
				c.add(nn.substring(0, nn.length() - 1) + nn.substring(nn.length() - 1).toLowerCase());
				for (int j = 1; j < t1.numRows(); ++j) {
					if (t1.valueAt(i, j).equals(""))
						c.add("-");
					else if (t2.valueAt(i, j).equals(""))
						c.add("-");
					else if (t1.valueAt(i, j).equals("y"))
						if (t2.valueAt(i, j).equals("y")) {
							c.add("-");
							++y;
						} else {
							c.add("r");
							++r;
						}
					else
						if (t2.valueAt(i, j).equals("y")) {
							c.add("s");
							++s;
						} else {
							c.add("-");
							++n;
						}
				}

				if (false) {
					if (((y + r ) >= Y_N_threshold) && ((n + s) >= Y_N_threshold)) {
						result.addColumn(t1.getColumn(i), result.numColumns());
					}
				} else {
					if ((s >= S_R_threshold) || (r >= S_R_threshold)) {
						result.addColumn(c, result.numColumns());
					}
				}
			}
			
		}
		
        if(args.length==2) {
            result.exportAsCsv(System.out);
        } else {
            result.exportAsCsv(new BufferedOutputStream(new FileOutputStream(args[2])));
        }
	}
}
