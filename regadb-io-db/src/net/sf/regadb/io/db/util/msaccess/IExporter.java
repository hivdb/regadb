package net.sf.regadb.io.db.util.msaccess;

import java.io.OutputStream;
import java.sql.ResultSet;

public interface IExporter {
	public void export(ResultSet rs, OutputStream os);
}
