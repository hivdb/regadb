package net.sf.regadb.ui.framework.widgets.datatable;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
import net.sf.witty.wt.i8n.WArgMessage;
import net.sf.witty.wt.i8n.WMessage;

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
    private WArgMessage labelMsg_;
	
	private int currentPage_ = 0;
	private int amountOfPages_ = 0;
    
    private final static WMessage emptyLiteral_ = new WMessage("", true);
    
    private ColumnHeader[] colHeaders_;
    
    private int sortColIndex_ = 0;
    
	public DataTable(IDataTable<DataType> dataTableInterface, int amountOfPageRows)
	{
		super();
        
        this.setStyleClass("datatable");
        
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
		showHideFilter_ = new WPushButton(tr("datatable.button.hideFilter"), elementAt(row, col));
		showHideFilter_.clicked.addListener(new SignalListener<WMouseEvent>()
		{
			public void notify(WMouseEvent e)
			{
				showHideFilters();
			}
		});
		row++;
		}
			
        
        colHeaders_ = new ColumnHeader[dataTableInterface_.getColNames().length];
		//put colheaders in the table
        int columnIndex = 0;
		for(String colName : dataTableInterface_.getColNames())
		{
            colHeaders_[col] = new ColumnHeader(tr(colName), elementAt(row, col));
            if(dataTableInterface_.sortableFields()[columnIndex])
            {
            colHeaders_[col].setSortNone();
            final int colHeaderIndex = col;
            colHeaders_[col].clicked.addListener(new SignalListener<WMouseEvent>()
                    {
                        public void notify(WMouseEvent a) 
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
					filter.getFilterWidget().setParent(elementAt(row, col));
				}
				col++;
			}
			applyFilter_ = new WPushButton(tr("datatable.button.applyFilter"), elementAt(row, col));
			applyFilter_.clicked.addListener(new SignalListener<WMouseEvent>()
			{
				public void notify(WMouseEvent me)
				{
					applyFilter();
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
			
            final int index = i;
            WText toPut;
			for(int j = 0; j<dataTableInterface_.getColNames().length; j++)
			{
			    toPut = new WText(noNullLt(null), elementAt(row, col));
                toPut.setStyleClass("table-cell");
                toPut.clicked.addListener(new SignalListener<WMouseEvent>()
                        {
                            public void notify(WMouseEvent a) 
                            {
                                if(dataTableInterface_.stillExists(rawDataArray_.get(index)))
                                {
                                    dataTableInterface_.selectAction(rawDataArray_.get(index));
                                }
                                else
                                {
                                    MessageBox.showWarningMessage(tr("datatable.message.alreadySelected"));
                                }
                            }
                        });
                elementAt(row, col).resize(new WLength(), new WLength(1.0,WLengthUnit.FontEm));
                textRow.add(toPut);
				col++;
			}
			row++;
			col = 0;
		}
        
        //scrolling buttons
        elementAt(row, col).setColumnSpan(dataTableInterface_.getColNames().length+1);
        WContainerWidget scrollingButtons = new WContainerWidget(elementAt(row, col));
        scrollingButtons.setContentAlignment(WHorizontalAlignment.AlignCenter);
        scrollingButtons.setStyleClass("scrollingButtons");
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
        labelMsg_ = new WArgMessage("datatable.text.pageXOfY");
        labelMsg_.addArgument("{currentPage}", 0);
        labelMsg_.addArgument("{amountOfPages}", 0);
        labelScroll_.setText(labelMsg_);
        
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
	
	private void refreshData(Transaction trans, boolean recalulateAmountOfPages)
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
			for(int j = 0; j<al.size(); j++)
			{
				al.get(j).setText(noNullLt(cols[j]));
			}
		}
        
        //clean up empty rows
        for(int i = 0; i<amountOfPageRows_-rawDataArray_.size(); i++)
        {
            List<WText> al = textMatrix_.get(i+rawDataArray_.size());
            for(int j = 0; j<al.size(); j++)
            {
                al.get(j).setText(lt(""));
            }
        }
        
        labelMsg_.changeArgument("{currentPage}", (currentPage_+1));
        labelMsg_.changeArgument("{amountOfPages}", amountOfPages_);
        labelScroll_.setText(labelMsg_);
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
	
	private WMessage noNullLt(String col)
	{
		if(col==null)
		{
			return emptyLiteral_;
		}
		return lt(col);
	}
	
	public void applyFilter()
	{
		Transaction trans = RegaDBMain.getApp().createTransaction();
        currentPage_ = 0;
		refreshData(trans, true);
        trans.commit();
	}
}
