package net.sf.regadb.analysis.functions;

import java.io.IOException;
import java.io.InputStream;

public class GenerateReport 
{
    public static void main(String [] args)
    {
        
    }
    
    public void generateReport(String rtfFileContent) {
        StringBuffer rtfBuffer = new StringBuffer(new String(rtfFileContent));
        
        
    }
    
    private static void replace(StringBuffer reportBuf, String find, String replace) {
        reportBuf.replace(reportBuf.indexOf(find), reportBuf.indexOf(find) + find.length(), replace);
    }
    
    private static void appendHexdump(InputStream input, StringBuffer toAppend) throws IOException {        
        for (int b = input.read(); b != -1; b = input.read()) {
            String hex = Integer.toHexString(b);
            if (hex.length() == 1)
                toAppend.append("0");
            toAppend.append(hex);
        }
    }

    private static void writePicture(StringBuffer reportBuf, String find, InputStream input) throws IOException {
        StringBuffer pic = new StringBuffer();
        pic.append(" }{\\*\\shppict{\\pict\\pngblip\n");
        appendHexdump(input, pic);
        pic.append("}}");
        int findStart = reportBuf.indexOf(find);
        reportBuf.delete(findStart, find.length());
        reportBuf.insert(findStart, pic);
    }
}
