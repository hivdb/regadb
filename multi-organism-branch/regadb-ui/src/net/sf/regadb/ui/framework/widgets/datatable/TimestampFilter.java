package net.sf.regadb.ui.framework.widgets.datatable;

import net.sf.regadb.util.date.DateUtils;


public class TimestampFilter extends DateFilter {
    public Object getFirstDate()
    {
        return DateUtils.parserEuropeanDate(getDateField1().text()).getTime()+"";
    }
    
    public Object getSecondDate()
    {
        return DateUtils.parserEuropeanDate(getDateField2().text()).getTime()+"";
    }
}
