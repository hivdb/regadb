package cpp2java.preproc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cpp2java.utils.Utils;

public class PreprocCpp {
    private Map<String,Integer> templateClasses = new HashMap<String,Integer>();
    
    public static void main(String [] args) {
        PreprocCpp preproc = new PreprocCpp();
        preproc.performChangesOnWitty(args[0]);
    }
    
    public void performChangesOnWitty(String wtSrcDir) {
        this.performChangesOnFilesInDir(wtSrcDir + File.separatorChar + "Wt");
        this.performChangesOnFilesInDir(wtSrcDir + File.separatorChar + "Wt/Chart");
        this.performChangesOnFilesInDir(wtSrcDir + File.separatorChar + "Wt/Ext");
        this.performChangesOnFilesInDir(wtSrcDir + File.separatorChar + "web");
    }
    
    public PreprocCpp(){
        getTemplateClasses().put("WSignalMapper", 2);
        getTemplateClasses().put("JSignal", 6);
    }
    
    public void performChangesOnFilesInDir(String dir) {
    	File dirF = new File(dir);
    	for(File f : dirF.listFiles()) {
    	    if(f.isFile())
    	        performChangesOnFile(f);
    	}
    }
        
    public void performChangesOnFile(File f) {
        System.err.println("Preprocess file: " + f.getAbsolutePath());
        
        StringBuffer sb = Utils.readFileAsString(f.getAbsolutePath());
        
        System.err.println("\t handle includes");
        sb = handleIncludes(sb);
        System.err.println("\t handle usings");
        sb = handleUsings(sb);
        System.err.println("\t handle Void Template Arg");
        handleVoidTemplateArg(sb);
        System.err.println("\t handle Default Template Arg");
        handleDefaultTemplateArg(sb);
        System.err.println("\t handle ImplementStateless");
        handleImplementStateless(sb);
        System.err.println("\t handle bitset<>");
        sb = new StringBuffer(sb.toString().replaceAll("std::bitset\\<[0-9]*\\>", "std::bitset"));
        
        removeMethodContent(f, "WCalendar.C", sb, "WCalendar::dateForCell", "{return date();}");
        removeMethodContent(f, "WCalendar.C", sb, "WCalendar::renderMonth", "{}");
        removeMethodContent(f, "WEnvironment.C", sb, "WEnvironment::libraryVersion", "{return std::string();}");
        removeMethodContent(f, "WEnvironment.C", sb, "WEnvironment::libraryVersion(int", "{}");
        removeMethodContent(f, "WFileResource.C", sb, "WFileResource::streamResourceData", "{return true;}");
        removeMethodContent(f, "TableView.C", sb, "parseNumberList", "{}");
        removeMethodContent(f, "WLogger.C", sb, "WLogger::setFile", "{return true;}");
        
        writeFile(f, sb);
    }
    
    public void removeMethodContent(File f, String fileName, StringBuffer fileContent, String methodName, String replacement) {
    	if(f.getName().equals(fileName)) {
	    	System.err.println("remove method contents in file: " + f.getAbsolutePath());
    		int methodNamePos = fileContent.indexOf(methodName);
	    	int firstBracket = fileContent.indexOf("{", methodNamePos);
	    	int matchBracket = findMatchingBracket(fileContent, firstBracket);
	    	
	    	fileContent.replace(firstBracket, matchBracket+1, replacement);
    	}
    }
    
    private void handleVoidTemplateArg(StringBuffer sb) {
        String [] toReplace = {"<void>"};
        String [] toReplaceWith = {"<DummyClass>"};
    
        replaceStrings(toReplace, toReplaceWith, sb);
    }
    
    public void handleDefaultTemplateArg(StringBuffer sb){
        //TODO make it work with nested templates: WClass<WClass2<T,S>>

        int a = 0;
        int b = 0;
        for(Map.Entry<String, Integer> e : getTemplateClasses().entrySet()){
            while((a = sb.indexOf(e.getKey()+"<",b)) > -1){
                b = sb.indexOf(">",a);
                
                if(a > -1 && b > -1){
                    a += e.getKey().length()+1;
                    String templates = sb.substring(a, b);
                    
                    int nTpl;
                    if(templates.trim().length() == 0)
                    	nTpl = 0;
                    else{
                    	String [] t = templates.split(",");
                    	nTpl = t.length;
                    }
                    
                    for(int i = nTpl; i < e.getValue(); ++i){
                    	if(i == 0)
                    		templates += "NoClass";
                    	else
                    		templates += ",NoClass";
                    }
                    
                    sb.replace(a, b, templates);
                }
            }
        }
    }
    
    public void handleImplementStateless(StringBuffer sb){
        String fnct = "implementStateless";
        
        Pattern p = Pattern.compile("([^\\s]*)[\\s]*"+ fnct +"[\\s]*(\\([^;]*\\));");
        Matcher m = p.matcher(sb);
        
        List<String> patterns = new ArrayList<String>();
        List<String> replacements = new ArrayList<String>();
        
        while(m.find()){
            String s = m.group();
            
            if(!s.startsWith("void")){
                patterns.add(s);
                
                int a = s.indexOf(fnct);
                a = s.indexOf('(',a);
                
                String r = s.substring(a).replace("\n","").replace("(", "(\"").replace(",", "\",\"").replace(")", "\")");
                r = s.replace(s.substring(a), r);
                
                replacements.add(r);
            }
        }
        
        replaceStrings(patterns, replacements, sb);
    }

    public void writeFile(File f, StringBuffer sb) {
        FileWriter out;
        try {
            out = new FileWriter(f);
            out.write(sb.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public StringBuffer handleIncludes(StringBuffer fileContent) {
        int pos = fileContent.indexOf("#include", 0);
        int endpos;
        
        while(pos != -1) {
            endpos = fileContent.indexOf("\n", pos);
            String textToReplace = fileContent.substring(pos, endpos);
            String textToReplaceWith = null;
            if(textToReplace.contains("iostream")) {
                textToReplaceWith = "#include <myiostream.h>";
            } else if(textToReplace.contains("map")) {
                textToReplaceWith = "#include <mymap.h>";
            } else if(textToReplace.contains("vector")) {
                textToReplaceWith = "#include <myvector.h>";
            } else if(textToReplace.contains("string")) {
                textToReplaceWith = "#include <mystring.h>";
            } else if(textToReplace.contains("iosfwd")) {
                textToReplaceWith = "#include <myiostream.h>";
            } else if(textToReplace.contains("sstream")) {
                textToReplaceWith = "#include <myostream.h>";
            } else if(textToReplace.contains("bitset")) {
                textToReplaceWith = "#include <mybitset.h>";
            } else if(textToReplace.contains("set")) {
                textToReplaceWith = "#include <myset.h>";
            } else if(textToReplace.contains("algorithm")) {
                textToReplaceWith = "#include <algorithm.h>";
            } else if(textToReplace.contains("limits")) {
                textToReplaceWith = "#include <mylimits.h>";
            } else if(textToReplace.contains("fstream")) {
                textToReplaceWith = "#include <myfstream.h>";
            } else if(textToReplace.contains("WDllDefs")) {
                textToReplaceWith = "";
            } else if(textToReplace.contains("stdlib")) {
                textToReplaceWith = "";
            } else if(textToReplace.contains("exception")) {
                textToReplaceWith = "#include <myexception.h>";
            } else if(textToReplace.contains("stdexcept")) {
                textToReplaceWith = "#include <stdexcept.h>";
            } else if(textToReplace.contains("cmath")) {
                textToReplaceWith = "#include <cmath.h>";
            } else if(textToReplace.contains("cassert")) {
                textToReplaceWith = "#include <cassert.h>";
            } else if(textToReplace.contains("threadpool")) {
                textToReplaceWith = "";
            } else if(textToReplace.contains("ExtDllDefs")) {
                textToReplaceWith = "";
            } else if(textToReplace.contains("ostream")) {
                textToReplaceWith = "#include <myostream.h>";
            } else if(textToReplace.contains("cfloat")) {
                textToReplaceWith = "#include <cfloat.h>";
            }
            
            
            
            if(textToReplaceWith != null) {
                fileContent.replace(pos, endpos, textToReplaceWith);
                pos = fileContent.indexOf("#include", pos + textToReplaceWith.length());
            } else {
                pos = fileContent.indexOf("#include", endpos);
            }
        }
        
        return fileContent;
    }
    
    public int findMatchingBracket(StringBuffer content, int index) {
        int dotComma = content.indexOf(";", index);
        int openingBracket = content.indexOf("{", index);
        if(openingBracket>dotComma)
            return -1;
        int endBracket = -1;
        int amountOfBrackets = 1;
        for(int i = openingBracket+1; i<content.length(); i++) {
            if(content.charAt(i)=='{') {
                amountOfBrackets++;
            } else if(content.charAt(i)=='}') {
                amountOfBrackets--;
            }
            if(amountOfBrackets==0) {
                endBracket = i;
                break;
            }
        }
        return endBracket;
    }
    
    public StringBuffer handleUsings(StringBuffer fileContent) {
        String [] toReplace = {"using std::exit;", "using WAbstractItemModel::setData;", "using WAbstractItemModel::data;"};
        String [] toReplaceWith = {"#include <myexit.h>", "//using WAbstractItemModel::setData;", "//using WAbstractItemModel::data;"};
    
        replaceStrings(toReplace, toReplaceWith, fileContent);
        
        return fileContent;
    }
    
    public void replaceStrings(String [] toReplace, String [] toReplaceWith, StringBuffer fileContent) {
        for(int i = 0; i<toReplace.length; i++) {
            int pos = fileContent.indexOf(toReplace[i], 0);
            while(pos != -1) {
                fileContent.replace(pos, pos+toReplace[i].length(), toReplaceWith[i]);
                pos = fileContent.indexOf(toReplace[i], pos+toReplace[i].length());
            }
        }
    }

    public void replaceStrings(List<String> toReplace, List<String> toReplaceWith, StringBuffer fileContent) {
        for(int i = 0; i < toReplace.size(); ++i) {
            String p = toReplace.get(i);
            String r = toReplaceWith.get(i);
            
            int pos = fileContent.indexOf(p, 0);
            while(pos != -1) {
                fileContent.replace(pos, pos+p.length(), r);
                pos = fileContent.indexOf(p, pos+p.length());
            }
        }
    }

    void setTemplateClasses(Map<String,Integer> templateClasses) {
        this.templateClasses = templateClasses;
    }

    Map<String,Integer> getTemplateClasses() {
        return templateClasses;
    }
    
    public void copyFile(File in, File out) {
    	StringBuffer sb = Utils.readFileAsString(in.getAbsolutePath());
    	writeFile(out, sb);
    }
}
