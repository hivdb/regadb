package net.sf.regadb.ui.framework;

import net.sf.witty.wt.core.WApplication;
import net.sf.witty.wt.core.WEnvironment;

public class RegaDBApplication extends WApplication
{

	public RegaDBApplication(WEnvironment env)
	{
		super(env);
		
		root().addWidget(new RegaDBWindow());
	}

	public RegaDBWindow getWindow()
	{
		return (RegaDBWindow)root().getChildren_().get(0);
	}
	
	public Tree getTree()
	{
		return getWindow().getTree_();
	}
	
	public Header getHeader()
	{
		return getWindow().getHeader_();
	}
	
	public FormContainer getFormContainer()
	{
		return getWindow().getContainer_();
	}
}