package net.sf.regadb.ui.framework.widgets.datatable;

public class HibernateStringUtils
{
	public static String filterConstraintsQuery(IDataTable dt)
	{
		String query = " ";
		String filter;
		
		for(int i = 0; i<dt.getFilters().length; i++)
		{
			if(dt.getFilters()[i]!=null)
			{
				filter = dt.getFilters()[i].getHibernateString(dt.getFieldNames()[i]);
				if(filter!=null)
				{
					if(!query.equals(" "))
					{
						query += " and ";
					}
					query += filter + " ";
				}
			}
		}
		
		return query;
	}
}
