package net.sf.regadb.ui.framework.widgets.datatable;

import java.util.List;


public interface IDataTable <DataType>
{
	public String[] getColNames();
	
	public int getAmountOfRows();
	
	public List<DataType> getPreviousDataBlock();
	public List<DataType> getNextDataBlock();
	public List<DataType> getFirstDataBlock();
	public List<DataType> getLastDataBlock();
	
	public String[] getRowData(DataType type);
	
	public IFilter[] getFilters();
	
	public void setHibernateFilterConstraints(String [] constraints);
	public void setSortedColumn(int index);
}
