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
   
    public TableExpander(CharSequence labelText, WTable table, int row)
    {
        table_ = table;
        
        startTableCell_ = table.getElementAt(row, 0);
        startTableCell_.setStyleClass("table-expander");
        plusMinusIcon_ = new WIconPair("pics/nav-plus.gif", "pics/nav-minus.gif", true, startTableCell_);
        label_ = new WText(labelText, table.getElementAt(row, 0));
        label_.setStyleClass("table-expander-text");
        
        label_.clicked().addListener(table, new Signal1.Listener<WMouseEvent>()
        {
            public void trigger(WMouseEvent me)
            {
                if(plusMinusIcon_.getState()==0) {
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
        int row = startTableCell_.getRow();
        for(int i = row+1; i<table_.getRowCount(); i++)
        {
            if(table_.getElementAt(i, 0)!=null && table_.getElementAt(i, 0).getChildren().size()!=0)
            {
                cellWidget = table_.getElementAt(i, 0).getChildren().get(0);
            }
            else
            {
                cellWidget = null;
            }
            if(!(cellWidget instanceof WIconPair))
            {
                int numCols = table_.getColumnCount();
                for(int j = 0; j<numCols; j++)
                {
                    if(table_.getElementAt(i, j)!=null && table_.getElementAt(i, j).getChildren().size()>0)
                    {
                    	table_.getElementAt(i, j).getChildren().get(0).setHidden(!expand);
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
