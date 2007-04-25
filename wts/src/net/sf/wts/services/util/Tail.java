package net.sf.wts.services.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class Tail 
{
    public static byte[] getTail(File file, int numLines)
    {
        StringBuffer buffer = new StringBuffer();
        try
        {
        LineNumberReader lineCounter = new LineNumberReader(new FileReader(file));
        int amountOfLines = 0;
        
        while(lineCounter.readLine()!=null)
            {
                
            }
        amountOfLines = lineCounter.getLineNumber();
        
        if(amountOfLines>numLines)
        {
            lineCounter = new LineNumberReader(new FileReader(file));
            String read;
            do
            {
                read = lineCounter.readLine();
                if(lineCounter.getLineNumber()>=amountOfLines-numLines+1 && read!=null)
                {
                    buffer.append(read);
                    buffer.append('\n');
                }
            }
            while(lineCounter.getLineNumber()<=amountOfLines && read!=null);
        }
        else
        {
            String read;
            lineCounter = new LineNumberReader(new FileReader(file));
            do
            {
                read = lineCounter.readLine();
                if(read!=null)
                {
                    buffer.append(read);
                    buffer.append('\n');
                }
            }
            while(read!=null);
        }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        
        return buffer.toString().getBytes();
    }
}
