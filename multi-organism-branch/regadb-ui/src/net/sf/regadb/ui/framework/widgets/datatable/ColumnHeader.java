package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

public class ColumnHeader extends WContainerWidget
{
	private WText header_;
    
    private WImage ascendingSort_;
    private WImage descendingSort_;
    private WImage noSort_;
	
	public ColumnHeader(WMessage intlName, WContainerWidget parent)
	{
		super(parent);
		
		header_ = new WText(intlName, this);
        header_.setStyleClass("general-clickable-text");
        
        ascendingSort_ = new WImage("pics/sort-arrow-up.gif", this);
        descendingSort_ = new WImage("pics/sort-arrow-down.gif", this);
        noSort_ = new WImage("pics/sort-arrow-none.gif", this);
        hideAllImages();
    }
    
    public void setSortAsc()
    {
        hideAllImages();
        ascendingSort_.setHidden(false);
    }
    
    public void setSortDesc()
    {
        hideAllImages();
        descendingSort_.setHidden(false);
    }
    
    public void setSortOpposite()
    {
        ascendingSort_.setHidden(!ascendingSort_.isHidden());
        descendingSort_.setHidden(!descendingSort_.isHidden());
    }
    
    public void setSortNone()
    {
        hideAllImages();
        noSort_.setHidden(false);
    }
    
    public boolean isAsc()
    {
        return !ascendingSort_.isHidden();
    }
    
    private void hideAllImages()
    {
        ascendingSort_.setHidden(true);
        descendingSort_.setHidden(true);
        noSort_.setHidden(true);
    }
}
