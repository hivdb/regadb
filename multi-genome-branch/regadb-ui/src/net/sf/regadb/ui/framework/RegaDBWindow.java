package net.sf.regadb.ui.framework;

import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WStdLocalizedStrings;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WTableCell;

public class RegaDBWindow extends WTable
{
	private Header header_;
	private Tree tree_;
	private FormContainer container_;
	
	public RegaDBWindow()
	{
		super();
		this.setStyleClass("root");
		
		loadI18nResources();
		
		WApplication.instance().useStyleSheet("style/regadb.css");
		WApplication.instance().useStyleSheet("style/querytool.css");
		
		//! TODO make the edition configurable
		header_ = new Header(this.elementAt(0, 0), Edition.Clinical);
		WTable contentTable = new WTable(this.elementAt(1, 0));
		tree_ = new Tree(contentTable.elementAt(0, 0));
		contentTable.elementAt(0, 0).setStyleClass("main-tree");
		container_ = new FormContainer(contentTable.elementAt(0, 1));
		contentTable.elementAt(0, 1).setStyleClass("formcontent");

	}
	
	private void loadI18nResources()
	{
		WStdLocalizedStrings resourceBundle = new WStdLocalizedStrings();
		resourceBundle.use("net.sf.regadb.ui.i18n.resources.regadb");
		WApplication.instance().setLocalizedStrings(resourceBundle);
	}

	public FormContainer getContainer_()
	{
		return container_;
	}

	public Header getHeader_()
	{
		return header_;
	}

	public Tree getTree_()
	{
		return tree_;
	}
	
	public void init()
	{
		tree_.init();
	}
    
    public void newTree() {
        WTableCell cell = ((WTableCell)tree_.parent());
        cell.clear();
        tree_ = new Tree(cell);
        tree_.init();
    }
}
