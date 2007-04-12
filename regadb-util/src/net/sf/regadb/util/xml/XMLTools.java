package net.sf.regadb.util.xml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XMLTools 
{
    private static DateFormat df_ = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    private static DateFormat dfRelaxNg_ = new SimpleDateFormat("yyyy-MM-dd");
    
    public static String dateToString(Date date)
    {
        return df_.format(date);
    }
    
    public static String dateToRelaxNgString(Date date)
    {
        return dfRelaxNg_.format(date);
    }
}
