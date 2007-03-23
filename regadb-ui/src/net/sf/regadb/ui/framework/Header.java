package net.sf.regadb.ui.framework;

import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WImage;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.WText;

public class Header extends WTable
{
	private WText mainHeader_;
	private WText subHeader_;
	private WText editionHeader_;
	
	private Edition edition_;
	
	public Header(WContainerWidget mainWindow, Edition edition)
	{
		super(mainWindow);
		
		edition_ = edition;
		
		  WImage icon = new WImage("pics/regaDBHeader.gif", this.elementAt(0, 0));
		  icon.setMargin(new WLength(5));
		  
		  WTable textTable = new WTable(this.elementAt(0, 1));
		  textTable.setMargin(new WLength(5));
		  mainHeader_ = new WText(tr("main.header.mainTitle"),textTable.elementAt(0, 0));
		  mainHeader_.setStyleClass("header-mainTitle");
		  subHeader_ = new WText(tr("main.header.subTitle"), textTable.elementAt(1, 0));
		  subHeader_.setStyleClass("header-subTitle");
		  editionHeader_ = new WText(getEditionText(), textTable.elementAt(2, 0));
		  editionHeader_.setStyleClass("header-subTitle");
	}
	
	private WMessage getEditionText()
	{
		switch(edition_)
		{
			case Clinical:
				return tr("main.header.clinicalEdition");
			case Research:
				return tr("main.header.researchEdition");
			default:
				return null;
		}
	}
	
	public void setEdition(Edition edition)
	{
		edition_ = edition;
		subHeader_.setText(getEditionText());
	}
}
