package net.sf.hivgensim.queries.input;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.regadb.db.Patient;

public class FromSnapshot extends QueryInput {
	private List<Patient> patientsCache;

	private List<File> files;

	public FromSnapshot(File[] files, IQuery<Patient> nextQuery) {
		super(nextQuery);
		this.files = new ArrayList<File>();
		Collections.addAll(this.files, files);
	}

	public FromSnapshot(List<File> files, IQuery<Patient> nextQuery) {
		super(nextQuery);
		this.files = files;
	}

	public FromSnapshot(File file, IQuery<Patient> nextQuery) {
		super(nextQuery);
		this.files = new ArrayList<File>();
		this.files.add(file);
	}

	public void run() {
		if (patientsCache == null) {
			patientsCache = new ArrayList<Patient>();
			for (File f : files) {
				processFile(f);
			}
		} else {
			for (Patient p : patientsCache) {
				getNextQuery().process(p);
			}
		}
	}

	private void processFile(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fis);
			Patient p;
			int i = 0;
			while ((p = (Patient) in.readObject()) != null) {
				getNextQuery().process(p);
				patientsCache.add(p);
				i++;
				if (i % 100 == 0) {
					System.err.println(i);
				}
			}
			getNextQuery().close();
			in.close();
		} catch (EOFException e) {
			getNextQuery().close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}