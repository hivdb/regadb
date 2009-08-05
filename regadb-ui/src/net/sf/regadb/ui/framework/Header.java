package net.sf.regadb.ui.framework;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

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
		
		  WImage icon = new WImage("pics/regaDBHeader.gif", this.getElementAt(0, 0));
		  icon.setMargin(new WLength(5));
		  this.getElementAt(0, 1).setStyleClass("header");
		  WTable textTable = new WTable(this.getElementAt(0, 1));
		  textTable.setMargin(new WLength(5));
		  mainHeader_ = new WText(tr("main.header.mainTitle"),textTable.getElementAt(0, 0));
		  mainHeader_.setStyleClass("header-mainTitle");
		  subHeader_ = new WText(tr("main.header.subTitle"), textTable.getElementAt(1, 0));
		  subHeader_.setStyleClass("header-subTitle");
		  editionHeader_ = new WText(getEditionText(), textTable.getElementAt(2, 0));
		  editionHeader_.setStyleClass("header-subTitle");
	}
	
	private WString getEditionText()
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
