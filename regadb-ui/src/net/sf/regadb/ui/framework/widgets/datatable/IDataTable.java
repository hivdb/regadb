package net.sf.regadb.ui.framework.widgets.datatable;

import java.util.ArrayList;

public interface IDataTable
{
	public String[] getColNames();
	
	public int getAmountOfRows();
	
	public ArrayList getPreviousDataBlock();
	public ArrayList getNextDataBlock();
	public ArrayList getFirstDataBlock();
	public ArrayList getLastDataBlock();
	
	public void setHibernateFilterConstraints(String [] constraints);
	public void setSortedColumn(int index);
}
