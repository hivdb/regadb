package net.sf.regadb.ui.framework.widgets.expandtable;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WIconPair;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WTableCell;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WWidget;

public class TableExpander 
{
    private WText label_;
    private WIconPair plusMinusIcon_;
    private WTable table_;
    
    private WTableCell startTableCell_;
   
    public TableExpander(WString labelText, WTable table, int row)
    {
        table_ = table;
        
        startTableCell_ = table.elementAt(row, 0);
        startTableCell_.setStyleClass("table-expander");
        plusMinusIcon_ = new WIconPair("pics/nav-plus.gif", "pics/nav-minus.gif", true, startTableCell_);
        label_ = new WText(labelText, table.elementAt(row, 0));
        label_.setStyleClass("table-expander-text");
        
        label_.clicked().addListener(table, new Signal1.Listener<WMouseEvent>()
        {
            public void trigger(WMouseEvent me)
            {
                if(plusMinusIcon_.state()==0) {
                    expand(true);
                } else {
                    expand(false);
                }
            }
        });
        
        plusMinusIcon_.icon1Clicked().addListener(table, new Signal1.Listener<WMouseEvent>() 
                {
                    public void trigger(WMouseEvent me) 
                    {
                        expand(true);
                    }
                });
        
        plusMinusIcon_.icon2Clicked().addListener(table, new Signal1.Listener<WMouseEvent>() 
                {
                    public void trigger(WMouseEvent me) 
                    {
                        expand(false);
                    }
                });
    }
    
    public void expand(boolean expand)
    {
    	plusMinusIcon_.setState(expand?1:0);
    	
        WWidget cellWidget;
        int row = startTableCell_.row();
        for(int i = row+1; i<table_.rowCount(); i++)
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
                int numCols = table_.columnCount();
                for(int j = 0; j<numCols; j++)
                {
                    if(table_.elementAt(i, j)!=null && table_.elementAt(i, j).children().size()>0)
                    {
                    	table_.elementAt(i, j).children().get(0).setHidden(!expand);
                    }
                }
            }
            else
            {
                break;
            }
        }
    }
}
