package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import eu.webtoolkit.jwt.WContainerWidget;

public interface IFilter
{
	public WContainerWidget getFilterWidget();
	public HibernateFilterConstraint getConstraint(String varName, int filterIndex);
}
