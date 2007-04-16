package net.sf.wts.build.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.tools.ant.Task;

public class ImplementSoapBinding extends Task
{
    private File soapBindingFile;
    private String adapterClassName;
    
    public void execute()
    {
        StringBuffer soapBindingContent = new StringBuffer(readStringFromFile(soapBindingFile));
        StringBuffer result = new StringBuffer();
        
        ArrayList<String> functionDefs = new ArrayList<String>();
        
        int position = 0;
        String methodDef;
        
        int classStartPosition = soapBindingContent.indexOf("{");
        int pos = classStartPosition;
        int startMethodDefPos;
        result.append(soapBindingContent.substring(0, classStartPosition+1));
        final String var = "adapterVarName";
        result.append("\n " + adapterClassName + " " + var + " = new " + adapterClassName + "();\n");
        
        do
        {   
            pos = soapBindingContent.indexOf("{", pos+1);
            startMethodDefPos = soapBindingContent.lastIndexOf("\n", pos);
            if(pos!=-1)
            {
                functionDefs.add(soapBindingContent.substring(startMethodDefPos+1, pos));
            }
        }
        while(pos!=-1);
        
        for(String m : functionDefs)
        {
            result.append(createFunction(var, m));
        }
        
        result.append("}");
        
        int public_class_pos = result.indexOf("public class");
                
        FileWriter fw;
        try 
        {
            fw = new FileWriter(soapBindingFile);
            fw.write(result.toString());
            fw.flush();
            fw.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    private String createFunction(String var, String funcSig)
    {
        String tmpFuncSig = funcSig.trim();
        String toReturn = funcSig + "\n{\n";
        StringTokenizer tok = new StringTokenizer(tmpFuncSig, " ");

        tok.nextToken(); //public
        String type = tok.nextToken();
        
        int startPos = tmpFuncSig.indexOf(type) + type.length();
        int endPos = tmpFuncSig.indexOf("throws");
        String func = tmpFuncSig.substring(startPos, endPos);
        
        if(!type.equals("void"))
        {
            toReturn += "return ";
        }
        
        toReturn += var + "." + prepareCallFunc(func) + ";\n}\n";
        
        return toReturn;
    }
    
    private String prepareCallFunc(String callFunc)
    {
        int start = callFunc.indexOf('(');
        int stop = callFunc.indexOf(')');
        
        String toReturn = callFunc.substring(0,start+1);
                
        String args = callFunc.substring(start+1,stop);
        
        StringTokenizer tok = new StringTokenizer(args, ",");
        while(tok.hasMoreTokens())
        {
            StringTokenizer tokArg = new StringTokenizer(tok.nextToken(), " ");
            tokArg.nextToken();
            toReturn += tokArg.nextToken();
            if(tok.hasMoreTokens())
            {
                toReturn += ", ";
            }
        }
        
        toReturn += ")";

        return toReturn.trim();
    }
    
    private String readStringFromFile(File fileToRead)
    {
        String thisLine;
        StringBuilder builder = new StringBuilder();
        try
        {
            InputStream is = new FileInputStream(fileToRead);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((thisLine = br.readLine()) != null)
            {
                builder.append(thisLine+"\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public String getAdapterClassName() {
        return adapterClassName;
    }

    public void setAdapterClassName(String adapterClassName) {
        this.adapterClassName = adapterClassName;
    }

    public File getSoapBindingFile() {
        return soapBindingFile;
    }

    public void setSoapBindingFile(File soapBindingFile) {
        this.soapBindingFile = soapBindingFile;
    }
    
    public static void main(String [] args)
    {
        ImplementSoapBinding isb = new ImplementSoapBinding();
        isb.setSoapBindingFile(new File(args[0]));
        isb.setAdapterClassName(args[1]);
        isb.execute();
    }
}
