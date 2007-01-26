package net.sf.regadb.ui.framework.widgets.datatable;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WPushButton;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.WText;
import net.sf.witty.wt.widgets.event.WMouseEvent;

public class DataTable<DataType> extends WTable
{
	private IDataTable<DataType> dataTableInterface_;
	
	private List<List<WText>> textMatrix_ = new ArrayList<List<WText>>();
	
	private int amountOfPageRows_;
	
	private WPushButton _showHideFilter;
	private WPushButton _applyFilter;
    
    private WPushButton firstScroll_;
    private WPushButton previousScroll_;
    private WPushButton nextScroll_;
    private WPushButton lastScroll_;
    private WText labelScroll_;
	
	private int currentPage = 0;
	private int amountOfPages_ = 0;
	
	public DataTable(IDataTable<DataType> dataTableInterface, int amountOfPageRows)
	{
		super();
		dataTableInterface_ = dataTableInterface;
		amountOfPageRows_ = amountOfPageRows;
		
		Transaction t = RegaDBMain.getApp().createTransaction();
		dataTableInterface_.init(t);
		t.commit();
		
		//This table is represented as a WTable
		//We use the row/col var to put the cells on their correct place
		int row = 0;
		int col = 0;
		
		if(dataTableInterface_.getFilters()!=null)
		{
		_showHideFilter = new WPushButton(tr("datatable.button.hideFilter"), elementAt(row, col));
		_showHideFilter.clicked.addListener(new SignalListener<WMouseEvent>()
		{
			public void notify(WMouseEvent e)
			{
				showHideFilters();
			}
		});
		row++;
		}
				
		//put colheaders in the table
		for(String colName : dataTableInterface_.getColNames())
		{
			new ColumnHeader(tr(colName), elementAt(row, col));
			col++;
		}
		row++;
		col = 0;
		
		//put filters in the table
		if(dataTableInterface_.getFilters()!=null)
		{
			for(IFilter filter : dataTableInterface_.getFilters())
			{
				filter.getFilterWidget().setParent(elementAt(row, col));
				col++;
			}
			_applyFilter = new WPushButton(tr("datatable.button.applyFilter"), elementAt(row, col));
			_applyFilter.clicked.addListener(new SignalListener<WMouseEvent>()
			{
				public void notify(WMouseEvent me)
				{
                    Transaction trans = RegaDBMain.getApp().createTransaction();
                    currentPage = 0;
					refreshData(trans, true);
                    trans.commit();
				}
			});
			row++;
			col = 0;
		}
		
		//put the necessary textfields
		List<WText> textRow;
		for(int i = 0; i<amountOfPageRows_; i++)
		{
			textRow = new ArrayList<WText>();
			textMatrix_.add(textRow);
			
			for(int j = 0; j<dataTableInterface_.getColNames().length; j++)
			{
				textRow.add(new WText(noNullLt(""), elementAt(row, col)));
				col++;
			}
			row++;
			col = 0;
		}
        
        //scrolling buttons
        elementAt(row, col).setRowSpan(dataTableInterface_.getColNames().length);
        WContainerWidget scrollingButtons = new WContainerWidget(elementAt(row, col));
        firstScroll_ = new WPushButton(tr("datatable.button.firstScroll"), scrollingButtons);
        firstScroll_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        firstScroll();
                    }
               });
        previousScroll_ = new WPushButton(tr("datatable.button.previousScroll"), scrollingButtons);
        previousScroll_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        previousScroll();
                    }
               });
        labelScroll_ = new WText(scrollingButtons);
        nextScroll_ = new WPushButton(tr("datatable.button.nextScroll"), scrollingButtons);
        nextScroll_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        nextScroll();
                    }
               });
        lastScroll_ = new WPushButton(tr("datatable.button.lastScroll"), scrollingButtons);
        lastScroll_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        Transaction trans = RegaDBMain.getApp().createTransaction();
                        lastScroll(trans, getAmountOfPages(trans));
                        trans.commit();
                    }
               });
        
		//load text in to the necessary textfields
        Transaction trans = RegaDBMain.getApp().createTransaction();
		refreshData(trans, true);
        trans.commit();
	}
    
    private void firstScroll()
    {
        Transaction trans = RegaDBMain.getApp().createTransaction();
        currentPage = 0;
        refreshData(trans, true);
        trans.commit();
    }
    
    private void previousScroll()
    {
        Transaction trans = RegaDBMain.getApp().createTransaction();
        if(currentPage!=0)
        {
            currentPage--;
            amountOfPages_ = getAmountOfPages(trans);
            if(currentPage>amountOfPages_)
            {
                lastScroll(trans, amountOfPages_);
            }
            else
            {
                refreshData(trans, false);
            }
        }
        trans.commit();
    }
    
    private void nextScroll()
    {
        Transaction trans = RegaDBMain.getApp().createTransaction();
        currentPage++;
        amountOfPages_  = getAmountOfPages(trans);
        if(currentPage>amountOfPages_)
        {
            lastScroll(trans, amountOfPages_);
        }
        else
        {
            refreshData(trans, false);
        }
        trans.commit();
    }
    
    private void lastScroll(Transaction trans, long amountOfPages)
    {
        amountOfPages_ = getAmountOfPages(trans);
        currentPage = amountOfPages_ -1;
        refreshData(trans, false);
    }
    
    private int getAmountOfPages(Transaction trans)
    {
        return (int)(((dataTableInterface_.getDataSetSize(trans)-1)/amountOfPageRows_)+1);
    }
	
	private void refreshData(Transaction trans, boolean recalulateAmountOfPages)
	{
        if(recalulateAmountOfPages)
        {
            amountOfPages_ = getAmountOfPages(trans);
        }
        
	    List<DataType> dataTypes = dataTableInterface_.getDataBlock(trans, currentPage , amountOfPageRows_);
		for(int i = 0; i<dataTypes.size(); i++)
		{
			List<WText> al = textMatrix_.get(i);
			String [] cols = dataTableInterface_.getRowData(dataTypes.get(i));
			for(int j = 0; j<al.size(); j++)
			{
				al.get(j).setText(noNullLt(cols[j]));
			}
		}
        
        //clean up empty rows
        for(int i = 0; i<amountOfPageRows_-dataTypes.size(); i++)
        {
            List<WText> al = textMatrix_.get(i+dataTypes.size());
            for(int j = 0; j<al.size(); j++)
            {
                al.get(j).setText(lt(""));
            }
        }
        
        labelScroll_.setText(lt("Page "+(currentPage+1)+" of " + amountOfPages_));
	}
	
	private void showHideFilters()
	{
		if(!dataTableInterface_.getFilters()[0].isVisible())
		{
			_showHideFilter.setText(tr("datatable.button.hideFilter"));
		}
		else
		{
			_showHideFilter.setText(tr("datatable.button.showFilter"));
		}
		
		for(IFilter i : dataTableInterface_.getFilters())
		{
			i.setVisible(!i.isVisible());
		}
		_applyFilter.setHidden(!_applyFilter.isHidden());	
	}
	
	private WMessage noNullLt(String col)
	{
		if(col==null)
		{
			return lt("");
		}
		return lt(col);
	}
}
