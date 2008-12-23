package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.util.date.DateUtils;


public class TimestampFilter extends DateFilter {
    public TimestampFilter(String dateFormat) {
		super(dateFormat);
	}

	public Object getFirstDate()
    {
        return DateUtils.parseEuropeanDate(getDateField1().text()).getTime()+"";
    }
    
    public Object getSecondDate()
    {
        return DateUtils.parseEuropeanDate(getDateField2().text()).getTime()+"";
    }
}
