package net.sf.regadb.ui.framework.widgets.datatable;

import java.util.List;

import net.sf.regadb.db.Transaction;


public interface IDataTable <DataType>
{
	public String[] getColNames();
	
	public void init(Transaction t);
	
	public List<DataType> getDataBlock(Transaction t, int startIndex, int amountOfRows);
	
	public String[] getRowData(DataType type);
	
	//return null if you do not want to use any filters
	public IFilter[] getFilters();

	public void setSortedColumn(int index);
    
    public String[] getFieldNames();
    
    public long getDataSetSize(Transaction t);
}
