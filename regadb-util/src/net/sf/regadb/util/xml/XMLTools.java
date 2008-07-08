package net.sf.regadb.util.xml;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class XMLTools 
{
    private static DateFormat df_ = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
    private static DateFormat dfRelaxNg_ = new SimpleDateFormat("yyyy-MM-dd");
    
    public synchronized static String dateToString(Date date)
    {
        return df_.format(date);
    }
    
    public synchronized static String dateToRelaxNgString(Date date)
    {
        return dfRelaxNg_.format(date);
    }
    
    public static String base64Encoding(byte [] data)
    {
        return (new BASE64Encoder()).encode(data);
    }
    
    public static byte[] base64Decoding(String data) throws IOException
    {
        return (new BASE64Decoder()).decodeBuffer(data);
    }
}
