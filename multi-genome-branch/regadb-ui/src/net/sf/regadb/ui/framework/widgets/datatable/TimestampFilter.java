package net.sf.regadb.ui.framework.widgets.datatable;

import java.util.Date;

import net.sf.regadb.util.date.DateUtils;


public class TimestampFilter extends DateFilter {
    public TimestampFilter(String dateFormat) {
		super(dateFormat);
	}

	public Object getFirstDate()
    {
	    Date d = DateUtils.parseEuropeanDate(getDateField1().text());
	    if(d == null)
	        d = new Date();
	    
        return d.getTime()+"";
    }
    
    public Object getSecondDate()
    {
        Date d = DateUtils.parseEuropeanDate(getDateField2().text());
        if(d == null)
            d = new Date();

        return d.getTime()+"";
    }
}
