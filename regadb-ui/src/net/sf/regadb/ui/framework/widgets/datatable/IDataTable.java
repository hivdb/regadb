package net.sf.regadb.ui.framework.widgets.datatable;

import java.util.List;


public interface IDataTable <DataType>
{
	public String[] getColNames();
	
	public int getAmountOfRows();
	
	public List<DataType> getFirstDataBlock();
	public List<DataType> getLastDataBlock();
	
	/*
	 * The start index needs to be provided
	 * The size of the data block we know already,
	 * we use the size of the returned List from the
	 * getFirstDataBlock() method
	 * */
	public List<DataType> getDataBlock(int startIndex);
	
	public String[] getRowData(DataType type);
	
	public IFilter[] getFilters();
	
	public void setHibernateFilterConstraints(String [] constraints);
	public void setSortedColumn(int index);
}
