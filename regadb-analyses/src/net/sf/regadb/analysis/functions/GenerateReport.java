package net.sf.regadb.analysis.functions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
    
    private void appendHexdump(File inputFile, StringBuffer toAppend) throws IOException {        
        FileInputStream fis = new FileInputStream(inputFile);
        for (int b = fis.read(); b != -1; b = fis.read()) {
            String hex = Integer.toHexString(b);
            if (hex.length() == 1)
                toAppend.append("0");
            toAppend.append(hex);
        }
    }

    public void writePicture(String find, File input) throws IOException {
        StringBuffer pic = new StringBuffer();
        pic.append(" }{\\*\\shppict{\\pict\\pngblip\n");
        appendHexdump(input, pic);
        pic.append("}}");
        int findStart = rtfBuffer_.indexOf(find);
        if(findStart!=-1)
            rtfBuffer_.replace(findStart, findStart + find.length(), pic.toString());
    }
}
