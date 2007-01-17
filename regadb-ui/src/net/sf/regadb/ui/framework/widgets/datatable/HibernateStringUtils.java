package net.sf.regadb.ui.framework.widgets.datatable;

public class HibernateStringUtils
{
	public static String filterConstraintsQuery(IDataTable dt)
	{
		String query = " ";
		String filter;
		
		for(int i = 0; i<dt.getFilters().length; i++)
		{
			filter = dt.getFilters()[i].getHibernateString(dt.getColNames()[i]);
			if(filter!=null)
			{
				if(!query.equals(" "))
				{
					query += " and ";
				}
				query += filter + " ";
			}
		}
		
		return query;
	}
}
