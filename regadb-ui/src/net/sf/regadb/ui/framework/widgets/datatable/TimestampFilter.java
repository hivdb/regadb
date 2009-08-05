package net.sf.regadb.ui.framework.widgets.datatable;

import java.util.Date;

import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;


public class TimestampFilter extends DateFilter {
    public TimestampFilter(String dateFormat) {
		super(dateFormat);
	}

	public Object getFirstDate()
    {
	    Date d = DateUtils.parse(getDateField1().getText());
	    if(d == null)
	        d = new Date();
	    
        return d.getTime();
    }
    
    public Object getSecondDate()
    {
        Date d = DateUtils.parse(getDateField2().getText());
        if(d == null)
            d = new Date();

        return d.getTime();
    }
    
    public HibernateFilterConstraint getConstraint(String varName, int filterIndex) {
    	varName = "cast("+ varName +", long)";
    	return super.getConstraint(varName, filterIndex);
    }
}
