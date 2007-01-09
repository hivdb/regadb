package net.sf.regadb.ui.framework.widgets.datatable;

import java.util.ArrayList;
import java.util.List;

import net.sf.witty.wt.widgets.WText;
import net.sf.witty.wt.widgets.table.WTable;

public class DataTable<DataType> extends WTable
{
	private IDataTable<DataType> dataTableInterface_;
	
	private List<List<WText>> textMatrix = new ArrayList<List<WText>>();
	
	public DataTable(IDataTable<DataType> dataTableInterface)
	{
		super();
		dataTableInterface_ = dataTableInterface;
		
		int row = 0;
		int col = 0;
		
		//put filters in the table
		for(IFilter filter : dataTableInterface_.getFilters())
		{
			filter.getFilterWidget().setParent(elementAt(row, col));
			col++;
		}
		row++;
		col = 0;
		
		//put colheaders in the table
		for(String colName : dataTableInterface_.getColNames())
		{
			new ColumnHeader(tr(colName), elementAt(row, col));
			col++;
		}
		row++;
		col = 0;
		
		String [] cols;
		List<WText> textRow;
		for(DataType dt : dataTableInterface_.getFirstDataBlock())
		{
			textRow = new ArrayList<WText>();
			textMatrix.add(textRow);
			
			cols = dataTableInterface_.getRowData(dt);
			for(String colEl : cols)
			{
				textRow.add(new WText(lt(colEl), elementAt(row, col)));
				col++;
			}
			row++;
			col = 0;
		}
	}
}
