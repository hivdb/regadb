package net.sf.regadb.ui.framework.widgets.datatable.hibernate;

import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.pair.Pair;

public class HibernateStringUtils
{
	public static HibernateFilterConstraint filterConstraintsQuery(IDataTable dt)
	{
		HibernateFilterConstraint query = new HibernateFilterConstraint();
		query.clause_ = " ";
		HibernateFilterConstraint filter = null;
		
		IFilter[] filters = dt.getFilters();
		String[] fieldNames = dt.getFieldNames();
		
		for(int i = 0; i<filters.length; i++)
		{
			if(filters[i]!=null)
			{
				filter = filters[i].getConstraint(fieldNames[i], i);
				
				if(filter!=null && filter.clause_!=null)
				{
					if(!query.clause_.equals(" "))
					{
						query.clause_ += " and ";
					}
					query.clause_ += filter.clause_ + " ";
					
					for(Pair<String, Object> arg : filter.arguments_)
					{
						query.arguments_.add(arg);
					}
				}
			}
		}
		
		return query;
	}
}
