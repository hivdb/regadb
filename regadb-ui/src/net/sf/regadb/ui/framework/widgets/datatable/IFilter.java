package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.witty.wt.widgets.WContainerWidget;

public interface IFilter
{
	public String getHibernateString(String varName);
	public WContainerWidget getFilterWidget();
	public void setVisible(boolean vis);
	public boolean isVisible();
}
