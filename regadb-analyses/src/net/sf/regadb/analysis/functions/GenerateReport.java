package net.sf.regadb.analysis.functions;

import java.io.IOException;
import java.io.InputStream;

public class GenerateReport 
{
    StringBuffer rtfBuffer_;
    
    public GenerateReport(byte[] rtfFileContent) 
    {
        rtfBuffer_ = new StringBuffer(new String(rtfFileContent));
    }
    
    public byte[] getReport() {        
        return rtfBuffer_.toString().getBytes();
    }
    
    public void replace(String find, String replace) {
        int indexOf = rtfBuffer_.indexOf(find);
        if(indexOf!=-1)
            rtfBuffer_.replace(indexOf, indexOf + find.length(), replace);
    }
    
    public void appendHexdump(InputStream input, StringBuffer toAppend) throws IOException {        
        for (int b = input.read(); b != -1; b = input.read()) {
            String hex = Integer.toHexString(b);
            if (hex.length() == 1)
                toAppend.append("0");
            toAppend.append(hex);
        }
    }

    public void writePicture(StringBuffer reportBuf, String find, InputStream input) throws IOException {
        StringBuffer pic = new StringBuffer();
        pic.append(" }{\\*\\shppict{\\pict\\pngblip\n");
        appendHexdump(input, pic);
        pic.append("}}");
        int findStart = reportBuf.indexOf(find);
        reportBuf.delete(findStart, find.length());
        reportBuf.insert(findStart, pic);
    }
}
