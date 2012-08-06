package net.sf.regadb.ui.framework.widgets.datatable;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

public class DataTable<DataType> extends WTable
{
	private IDataTable<DataType> dataTableInterface_;
	
	private List<List<WText>> textMatrix_ = new ArrayList<List<WText>>();
    
    private List<DataType> rawDataArray_;
	
	private int amountOfPageRows_;
	
	private WPushButton showHideFilter_;
	private WPushButton applyFilter_;
    
    private WPushButton firstScroll_;
    private WPushButton previousScroll_;
    private WPushButton nextScroll_;
    private WPushButton lastScroll_;
    private WText labelScroll_;
    private WString labelMsg_;
	
	private int currentPage_ = 0;
	private int amountOfPages_ = 0;
    
    private ColumnHeader[] colHeaders_;
    
    private int sortColIndex_ = -1;
    
	public DataTable(IDataTable<DataType> dataTableInterface, int amountOfPageRows)
	{
		super();
        
        this.setStyleClass("datatable datatable-grid");
        
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
			getElementAt(row, col).setColumnSpan(dataTableInterface_.getColumnWidths().length);
			getElementAt(row, col).setStyleClass("navigation");
			showHideFilter_ = new WPushButton(tr("datatable.button.hideFilter"), getElementAt(row, col));
			showHideFilter_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
			{
				public void trigger(WMouseEvent e)
				{
					showHideFilters();
				}
			});
			applyFilter_ = new WPushButton(tr("datatable.button.applyFilter"), getElementAt(row, col));
			applyFilter_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
			{
				public void trigger(WMouseEvent me)
				{
					applyFilter();
				}
			});
			row++;
		}
		
        colHeaders_ = new ColumnHeader[dataTableInterface_.getColNames().length];
		//put colheaders in the table
        int columnIndex = 0;
		for(CharSequence colName : dataTableInterface_.getColNames())
		{
            colHeaders_[col] = new ColumnHeader(colName, getElementAt(row, col));
            getElementAt(row, col).resize(new WLength(dataTableInterface.getColumnWidths()[col], WLength.Unit.Percentage), new WLength());
            getElementAt(row, col).setStyleClass("column-title");
            if(dataTableInterface_.sortableFields()[columnIndex])
            {
                if(sortColIndex_ == -1)
                    sortColIndex_ = columnIndex;
                
                colHeaders_[col].setSortNone();
                final int colHeaderIndex = col;
                colHeaders_[col].clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                        {
                    public void trigger(WMouseEvent a) 
                    {
                        //a header is selected where sorting is already enabled
                        if(colHeaderIndex==sortColIndex_)
                        {
                            colHeaders_[sortColIndex_].setSortOpposite();
                        }
                        else
                        {
                            colHeaders_[sortColIndex_].setSortNone();
                            sortColIndex_ = colHeaderIndex;
                            colHeaders_[sortColIndex_].setSortDesc();
                        }
                        Transaction trans = RegaDBMain.getApp().createTransaction();
                        refreshData(trans, true);
                        trans.commit();
                    }
                        });
            }
            columnIndex++;
			col++;
		}
		if(sortColIndex_ == -1)
		    sortColIndex_ = 0;
		
		row++;
		col = 0;
        colHeaders_[col].setSortDesc();
		
		//put filters in the table
		if(dataTableInterface_.getFilters()!=null)
		{
			for(IFilter filter : dataTableInterface_.getFilters())
			{
				if(filter!=null)
				{
					getElementAt(row, col).clear();
					getElementAt(row, col).addWidget(filter.getFilterWidget());
				}
				getElementAt(row, col).setStyleClass("filter");

				col++;
			}
			row++;
			col = 0;
		}	
		
		//put the necessary textfields
		List<WText> textRow;
		for(int i = 0; i<amountOfPageRows_; i++)
		{
			textRow = new ArrayList<WText>();
			textMatrix_.add(textRow);
			
            final int index = i;
            WText toPut;
			for(int j = 0; j<dataTableInterface_.getColNames().length; j++)
			{
			    toPut = new WText(noNullLt(null), getElementAt(row, col));
			    toPut.setTextFormat(TextFormat.PlainText);
                toPut.setStyleClass("table-cell");
                toPut.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                        {
                            public void trigger(WMouseEvent a) 
                            {
                                if(stillExists(rawDataArray_.get(index)))
                                {
                                    dataTableInterface_.selectAction(rawDataArray_.get(index));
                                }
                                else
                                {
                                	UIUtils.showWarningMessageBox(DataTable.this, tr("datatable.message.alreadySelected"));
                                }
                            }
                        });
                getElementAt(row, col).resize(new WLength(), new WLength(1.0,WLength.Unit.FontEm));
                textRow.add(toPut);
				col++;
			}
			row++;
			col = 0;
		}
        
        //scrolling buttons
		getElementAt(row, col).setColumnSpan(dataTableInterface_.getColNames().length);
		getElementAt(row, col).setStyleClass("bottom-navigation");
        WContainerWidget scrollingButtons = new WContainerWidget(getElementAt(row, col));
        scrollingButtons.setStyleClass("scrollingButtons");
        firstScroll_ = new WPushButton(tr("datatable.button.firstScroll"), scrollingButtons);
        firstScroll_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a) 
                    {
                        firstScroll();
                    }
               });
        previousScroll_ = new WPushButton(tr("datatable.button.previousScroll"), scrollingButtons);
        previousScroll_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a) 
                    {
                        previousScroll();
                    }
               });
        
        labelScroll_ = new WText(scrollingButtons);
        labelScroll_.setText(tr("datatable.text.pageXOfY").arg(0).arg(0));
        
        nextScroll_ = new WPushButton(tr("datatable.button.nextScroll"), scrollingButtons);
        nextScroll_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a) 
                    {
                        nextScroll();
                    }
               });
        lastScroll_ = new WPushButton(tr("datatable.button.lastScroll"), scrollingButtons);
        lastScroll_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a) 
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
        currentPage_ = 0;
        refreshData(trans, true);
        trans.commit();
    }
    
    private void previousScroll()
    {
        Transaction trans = RegaDBMain.getApp().createTransaction();
        if(currentPage_!=0)
        {
            currentPage_--;
            amountOfPages_ = getAmountOfPages(trans);
            if(currentPage_+1>amountOfPages_)
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
        currentPage_++;
        amountOfPages_  = getAmountOfPages(trans);
        if(currentPage_+1>amountOfPages_)
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
        currentPage_ = amountOfPages_ -1;
        refreshData(trans, false);
    }
    
    private int getAmountOfPages(Transaction trans)
    {
        return (int)(((dataTableInterface_.getDataSetSize(trans)-1)/amountOfPageRows_)+1);
    }
	
	public void refreshData(Transaction trans, boolean recalulateAmountOfPages)
	{
        if(recalulateAmountOfPages)
        {
            amountOfPages_ = getAmountOfPages(trans);
        }
        
	    rawDataArray_ = dataTableInterface_.getDataBlock(trans, (currentPage_)*amountOfPageRows_ , amountOfPageRows_, sortColIndex_, colHeaders_[sortColIndex_].isAsc());
		for(int i = 0; i<rawDataArray_.size(); i++)
		{
			List<WText> al = textMatrix_.get(i);
			String [] cols = dataTableInterface_.getRowData(rawDataArray_.get(i));
			String [] tooltips = dataTableInterface_.getRowTooltips(rawDataArray_.get(i));
			for(int j = 0; j<al.size(); j++)
			{
				al.get(j).setText(noNullLt(cols[j]));
				if(tooltips!=null && tooltips[j]!=null) {
					al.get(j).setToolTip(tooltips[j]);
				}
			}
		}
        
        //clean up empty rows
        for(int i = 0; i<amountOfPageRows_-rawDataArray_.size(); i++)
        {
            List<WText> al = textMatrix_.get(i+rawDataArray_.size());
            for(int j = 0; j<al.size(); j++)
            {
                al.get(j).setText("");
            }
        }
        
        labelScroll_.setText(tr("datatable.text.pageXOfY").arg((currentPage_+1)).arg(amountOfPages_));
	}
	
	private void showHideFilters()
	{
		if(dataTableInterface_.getFilters()[0].getFilterWidget().isHidden())
		{
			showHideFilter_.setText(tr("datatable.button.hideFilter"));
		}
		else
		{
			showHideFilter_.setText(tr("datatable.button.showFilter"));
		}
		
		for(IFilter i : dataTableInterface_.getFilters())
		{
			if(i!=null)
			{
				i.getFilterWidget().setHidden(!i.getFilterWidget().isHidden());
			}
		}
		applyFilter_.setHidden(!applyFilter_.isHidden());	
	}
	
	private CharSequence noNullLt(String col)
	{
		if(col==null)
		{
			return "";
		}
		return col;
	}
	
	public void applyFilter()
	{
		for(IFilter f : dataTableInterface_.getFilters())
			if(f != null && !f.isValid())
				return;
		
		Transaction trans = RegaDBMain.getApp().createTransaction();
        currentPage_ = 0;
		refreshData(trans, true);
        trans.commit();
	}
    
    public boolean stillExists(Object obj) {
    	//TODO: find a solution compatible with Hibernate 3.3.0+ 
//        Transaction trans = RegaDBMain.getApp().createTransaction();
//        boolean state = trans.stillExists(obj);
//        trans.commit();
//        return state;
    	return true;
    }
}
