package ovid.preproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreprocCpp {
    private Map<String,Integer> templateClasses = new HashMap<String,Integer>();
    
    public static void main(String [] args) {
        PreprocCpp preproc = new PreprocCpp();
        
        preproc.performChangesOnFilesInDir(args[0] + File.separatorChar + "wt");
        preproc.performChangesOnFilesInDir(args[0] + File.separatorChar + "web");
        preproc.performChangesOnFilesInDir(args[0] + File.separatorChar + "Chart");
        
        preproc.removeExterns(new File(args[0] + File.separatorChar + "wt" + File.separatorChar + "WString"));
        
        preproc.removeMethodContent(new File(args[0] + File.separatorChar + "wt" + File.separatorChar + "WCalendar.C"),
        		"WCalendar::dateForCell",
        		"{return date();}");
        preproc.removeMethodContent(new File(args[0] + File.separatorChar + "wt" + File.separatorChar + "WCalendar.C"),
        		"WCalendar::renderMonth",
        		"{}");
        preproc.removeMethodContent(new File(args[0] + File.separatorChar + "wt" + File.separatorChar + "WEnvironment.C"),
        		"WEnvironment::libraryVersion",
        		"{return std::string();}");
        preproc.removeMethodContent(new File(args[0] + File.separatorChar + "wt" + File.separatorChar + "WEnvironment.C"),
        		"WEnvironment::libraryVersion(int",
        		"{}");
    }
    
    public PreprocCpp(){
        getTemplateClasses().put("WSignalMapper", 2);
    }
    
    public void performChangesOnFilesInDir(String dir) {
    	File dirF = new File(dir);
    	for(File f : dirF.listFiles()) {
    	    if(f.isFile())
    	        performChangesOnFile(f);
    	}
    }
    
    public void removeExterns(File f) {
    	System.err.println("Removing externs from file: " + f.getAbsolutePath());
    	StringBuffer fileContent = readFileAsString(f.getAbsolutePath());
    	
        int pos = fileContent.indexOf("extern", 0);
        int endpos;
        
        while(pos != -1) {
            endpos = fileContent.indexOf("\n", pos);
            String textToReplace = fileContent.substring(pos, endpos);
            String textToReplaceWith = "";
            
            if(textToReplaceWith != null) {
                fileContent.replace(pos, endpos, textToReplaceWith);
                pos = fileContent.indexOf("extern", pos + textToReplaceWith.length());
            } else {
                pos = fileContent.indexOf("extern", endpos);
            }
        }
        
        writeFile(f, fileContent);
    }
    
    public void performChangesOnFile(File f) {
        System.err.println("Preprocess file: " + f.getAbsolutePath());
        
        StringBuffer sb = readFileAsString(f.getAbsolutePath());
        
        System.err.println("\t handle includes");
        sb = handleIncludes(sb);
        System.err.println("\t handle usings");
        sb = handleUsings(sb);
        System.err.println("\t handle Void Template Arg");
        handleVoidTemplateArg(sb);
        System.err.println("\t handle Default Template Arg");
        handleDefaultTemplateArg(sb);
        
        writeFile(f, sb);
    }
    
    public void removeMethodContent(File f, String methodName, String replacement) {
    	StringBuffer fileContent = readFileAsString(f.getAbsolutePath());
    	
    	int methodNamePos = fileContent.indexOf(methodName);
    	int firstBracket = fileContent.indexOf("{", methodNamePos);
    	int matchBracket = findMatchingBracket(fileContent, firstBracket);
    	
    	fileContent.replace(firstBracket, matchBracket+1, replacement);
    	
    	writeFile(f, fileContent);
    }
    
    private void handleVoidTemplateArg(StringBuffer sb) {
        String [] toReplace = {"<void>"};
        String [] toReplaceWith = {"<DummyClass>"};
    
        replaceStrings(toReplace, toReplaceWith, sb);
    }
    
    private void handleSizeInBytes(StringBuffer sb) {
        String [] toReplace = {"public:", "public :"};
        String [] toReplaceWith = {"public: \n int sizeInBytes;\n", "public: \n int sizeInBytes;\n"};
    
        replaceStrings(toReplace, toReplaceWith, sb);    
    }

    private void handleSlots(StringBuffer sb) {
        String [] toReplace = {"public slots:", "private slots:"};
        String [] toReplaceWith = {"public:", "private:"};
    
        replaceStrings(toReplace, toReplaceWith, sb);
    }
    
    private StringBuffer handleJSignal(StringBuffer sb) {
        String temp = sb.toString();
        temp = Pattern.compile("JSignal<([^>]|[\r\n])*>").matcher(temp).replaceAll(Matcher.quoteReplacement("JSignal"));
        temp = Pattern.compile("template <([^>]|[\r\n])*> class JSignal;").matcher(temp).replaceAll(Matcher.quoteReplacement(""));
        
        return new StringBuffer(temp);
    }
    
    private StringBuffer handleBitsetDeclaration(StringBuffer sb) {
        String temp = sb.toString();
        temp = Pattern.compile("bitset<([^>]|[\r\n])*>").matcher(temp).replaceAll(Matcher.quoteReplacement("bitset"));
        
        return new StringBuffer(temp);
    }

    private void handleWStringWChar(StringBuffer sb) {
        String [] toReplace = {"std::wstring", "wchar_t"};
        String [] toReplaceWith = {"std::string", "char"};
    
        replaceStrings(toReplace, toReplaceWith, sb);
    }
    
    public void handleDefaultTemplateArg(StringBuffer sb){
        //TODO make it work with nested templates: WClass<WClass2<T,S>>
        
        for(Map.Entry<String, Integer> e : getTemplateClasses().entrySet()){
            int a = sb.indexOf(e.getKey()+"<");
            int b = sb.indexOf(">",a);
            
            String templates = sb.substring(a + e.getKey().length()+1, b);
            String [] t = templates.split(",");
            
            for(int i = t.length; i < e.getValue(); ++i){
                templates += ",NoClass";
            }
            
            sb.replace(a + e.getKey().length()+1, b, templates);
        }
    }
    
    public void removeComments(File f) {
        //Run external program
        //#!/bin/sh
        //#http://www.bdc.cx/software/stripcmt/
        //stripcmt $1 > $2
        String[] cmd = { "/usr/bin/stripcomment.sh", f.getAbsolutePath() , f.getAbsolutePath() + ".commentLess"};
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            File commentLess = new File(f.getAbsolutePath() + ".commentLess");
            commentLess.renameTo(new File(f.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public StringBuffer handleEnumsAndStructs(StringBuffer sb, File f) {
        ArrayList<Integer> enumStartIndices = new ArrayList<Integer>();
        ArrayList<Integer> enumEndIndices = new ArrayList<Integer>();
        sb = locateStructure("enum", sb, enumStartIndices, enumEndIndices);
        
        ArrayList<Integer> structStartIndices = new ArrayList<Integer>();
        ArrayList<Integer> structEndIndices = new ArrayList<Integer>();
        sb = locateStructure("struct", sb, structStartIndices, structEndIndices);
        
        String structsAndEnums = "";
        ArrayList<String> structs = new ArrayList<String>(); 
        ArrayList<String> toDelete = new ArrayList<String>(); 
        for(int i = 0; i<enumStartIndices.size(); i++) {
            toDelete.add(sb.substring(enumStartIndices.get(i), enumEndIndices.get(i)+1));
            structsAndEnums += toDelete.get(toDelete.size()-1) + '\n';
        }
        for(int i = 0; i<structStartIndices.size(); i++) {
            toDelete.add(sb.substring(structStartIndices.get(i), structEndIndices.get(i)+1));
            structsAndEnums += toDelete.get(toDelete.size()-1) + '\n';
            structs.add(sb.substring(structStartIndices.get(i), structEndIndices.get(i)+1));
        }
        String temp = sb.toString();
        for(String toDel : toDelete) {
            temp = temp.replace(toDel, "");
        }
        sb = new StringBuffer(temp);
        
        int nsPos = sb.indexOf("namespace Wt");
        if(nsPos==-1)
            System.err.println("\t could not find \"namespace Wt\"");
        int insertPos = sb.indexOf("{", nsPos);
        if(insertPos==-1)
            System.err.println("\t could not find \"namespace Wt\"");
        else 
            sb.insert(insertPos+1, "\n" + structsAndEnums);
        
        if(!f.getAbsolutePath().endsWith(".C")) {
        ArrayList<String> structNames = new ArrayList<String>();
        for(String struct : structs) {
            int indexOfBracket = struct.indexOf('{');
            String [] structWords = struct.substring(0, indexOfBracket).split(" ");
            structNames.add(structWords[structWords.length-1]);
        }
        
        File cFile;
        if(f.getAbsolutePath().endsWith(".h")) {
            cFile = new File(f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf('.'))+".C");
        } else {
            cFile = new File(f.getAbsolutePath() + ".C");
        }
        String className = cFile.getAbsolutePath().substring(cFile.getAbsolutePath().lastIndexOf(File.separatorChar)+1, cFile.getAbsolutePath().lastIndexOf("."));
        StringBuffer sbCFile = readFileAsString(cFile.getAbsolutePath());
        for(String structName : structNames) {
            sbCFile = new StringBuffer(sbCFile.toString().replaceAll(className.trim() + "::" + structName.trim(), structName.trim()));
        }
        writeFile(cFile, sbCFile);
        }
        
        return sb;
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
    
    public StringBuffer handleIterators(StringBuffer fileContent) {
        String [] toReplace = {"const_iterator"};
        String [] toReplaceWith = {"const_iterator*"};
    
        replaceStrings(toReplace, toReplaceWith, fileContent);
        
        return fileContent;
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
    
    public StringBuffer locateStructure(String name, StringBuffer content, ArrayList<Integer> enumSI, ArrayList<Integer> enumEI) {
        int index = content.indexOf(name);
        int endIndex;
        //System.err.println(content.toString());
        while(index!=-1) {
            endIndex = findMatchingBracket(content, index);
            if(endIndex==-1) {
                index = content.indexOf(name, content.indexOf(";", index));
            } else {
            endIndex = content.indexOf(";", endIndex);
            enumSI.add(index);
            enumEI.add(endIndex);
            index = content.indexOf(name, endIndex);
            }
        }
        
        return content;
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
    
    public StringBuffer locateStringLiterals(StringBuffer content, IStringLiteral stringLiteral) {
        boolean backslashState = false;
        
        int startIndex = -1;
        int endIndex;
        for(int i = 0; i<content.length(); i++) {
            if(content.charAt(i)=='\\' && !backslashState) {
                backslashState = true;
                continue;
            }
            if(backslashState) {
                backslashState = false;
                continue;
            } else {
                if(content.charAt(i) == '"') {
                    if(startIndex==-1 && content.charAt(i-1) != '\'') {
                        startIndex = i;
                    } else if(startIndex!=-1){
                        endIndex = i;
                        i = stringLiteral.locateStringAction(content, startIndex, endIndex);
                        startIndex = -1; 
                    }
                }
            }
        }
        
        return content;
    }
    
        
    public StringBuffer handleUsings(StringBuffer fileContent) {
        String [] toReplace = {"using std::exit;", "using WAbstractItemModel::setData;", "using WAbstractItemModel::data;"};
        String [] toReplaceWith = {"#include <myexit.h>", "//using WAbstractItemModel::setData;", "//using WAbstractItemModel::data;"};
    
        replaceStrings(toReplace, toReplaceWith, fileContent);
        
        return fileContent;
    }
    
    public StringBuffer handleBoost(StringBuffer fileContent) {
        String [] toReplace = {"boost::lexical_cast<std::string>"};
        String [] toReplaceWith = {"std::string"};
        
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
    
    private static StringBuffer readFileAsString(String filePath) {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new FileReader(filePath));
            char[] buf = new char[1024];
            int numRead=0;
            while((numRead=reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
            return fileData;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void setTemplateClasses(Map<String,Integer> templateClasses) {
        this.templateClasses = templateClasses;
    }

    Map<String,Integer> getTemplateClasses() {
        return templateClasses;
    }
}
