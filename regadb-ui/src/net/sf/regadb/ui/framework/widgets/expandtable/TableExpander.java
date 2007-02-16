package net.sf.regadb.ui.framework.widgets.expandtable;

import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WIconPair;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.WText;
import net.sf.witty.wt.widgets.WWidget;
import net.sf.witty.wt.widgets.event.WMouseEvent;

public class TableExpander 
{
    private WText label_;
    private WIconPair plusMinusIcon_;
    private WTable table_;
    private int row_;
    
    public TableExpander(WMessage labelText, WTable table, int row)
    {
        row_ = row;
        table_ = table;
        
        plusMinusIcon_ = new WIconPair("pics/nav-plus.gif", "pics/nav-minus.gif", table.elementAt(row, 0));
        label_ = new WText(labelText, table.elementAt(row, 1));
        label_.setStyleClass("table-expander-text");
        
        label_.clicked.addListener(new SignalListener<WMouseEvent>()
        {
            public void notify(WMouseEvent me)
            {
                if(plusMinusIcon_.state()==0)
                {
                    performExpand();
                    plusMinusIcon_.setState(1);
                }
                else
                {
                    performCollapse();
                    plusMinusIcon_.setState(0);
                }
            }
        });
        
        plusMinusIcon_.icon1Clicked.addListener(new SignalListener<WMouseEvent>() 
                {
                    public void notify(WMouseEvent me) 
                    {
                        performExpand();
                    }
                });
        
        plusMinusIcon_.icon2Clicked.addListener(new SignalListener<WMouseEvent>() 
                {
                    public void notify(WMouseEvent me) 
                    {
                        performCollapse();
                    }
                });
    }

    private void performExpand() 
    {
        perform(true);
    }
    
    private void performCollapse() 
    {
        perform(false);
    }
    
    private void perform(boolean expand)
    {
        WWidget cellWidget;
        for(int i = row_+1; i<table_.numRows(); i++)
        {
            if(table_.elementAt(i, 0)!=null && table_.elementAt(i, 0).children().size()!=0)
            {
                cellWidget = table_.elementAt(i, 0).children().get(0);
            }
            else
            {
                cellWidget = null;
            }
            if(!(cellWidget instanceof WIconPair))
            {
                int numCols = table_.numColumns();
                for(int j = 0; j<numCols; j++)
                {
                    if(table_.elementAt(i, j)!=null)
                    {
                        for(WWidget widget : table_.elementAt(i, j).children())
                        {
                            if(widget!=null)
                            {
                                widget.setHidden(!expand);
                            }
                        }
                    }
                }
            }
            else
            {
                break;
            }
        }
    }
    
    public void expand()
    {
        plusMinusIcon_.setState(1);
        performExpand();
    }
    
    public void collapse()
    {
        plusMinusIcon_.setState(0);
        performCollapse();
    }
}
