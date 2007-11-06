package ovid.preproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PreprocCpp {
    public static void main(String [] args) {
        PreprocCpp preproc = new PreprocCpp();
        
        preproc.performChangesOnFile(new File("/home/plibin0/tmp/wt/java_tag/wt/src/web/EscapeOStream.h"));
        preproc.performChangesOnFile(new File("/home/plibin0/tmp/wt/java_tag/wt/src/web/DomElement.C"));
        preproc.performChangesOnFile(new File("/home/plibin0/tmp/wt/java_tag/wt/src/web/DomElement.h"));
    }
    
    public void performChangesOnFile(File f) {
        System.err.println("Preprocess file: " + f.getAbsolutePath());
        
        StringBuffer sb = readFileAsString(f.getAbsolutePath());
        
        //remove all comments
        sb = new StringBuffer(sb.toString().replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)",""));
        
        sb = handleIncludes(sb);
        sb = handleUsings(sb);
        sb = handleBoost(sb);
        sb = handleIterators(sb);
        
        String [] operators = {"return", "=", "<<"};
        sb = locateStringLiterals(sb, new StringLiteralReplaceStdString(operators));
        
        ArrayList<Integer> stringStartIndices = new ArrayList<Integer>();
        ArrayList<Integer> stringEndIndices = new ArrayList<Integer>();
        sb = locateStringLiterals(sb, new StringLiteralPositionRecorder(stringStartIndices, stringEndIndices));
        
        ArrayList<Integer> enumStartIndices = new ArrayList<Integer>();
        ArrayList<Integer> enumEndIndices = new ArrayList<Integer>();
        sb = locateStructure("enum", sb, enumStartIndices, enumEndIndices);
        
        ArrayList<Integer> structStartIndices = new ArrayList<Integer>();
        ArrayList<Integer> structEndIndices = new ArrayList<Integer>();
        sb = locateStructure("struct", sb, structStartIndices, structEndIndices);
        
        String structsAndEnums = "";
        ArrayList<String> toDelete = new ArrayList<String>(); 
        for(int i = 0; i<enumStartIndices.size(); i++) {
            toDelete.add(sb.substring(enumStartIndices.get(i), enumEndIndices.get(i)+1));
            structsAndEnums += toDelete.get(toDelete.size()-1) + '\n';
        }
        for(int i = 0; i<structStartIndices.size(); i++) {
            toDelete.add(sb.substring(structStartIndices.get(i), structEndIndices.get(i)+1));
            structsAndEnums += toDelete.get(toDelete.size()-1) + '\n';
        }
        String temp = sb.toString();
        for(String toDel : toDelete) {
            temp = temp.replace(toDel, "");
        }
        sb = new StringBuffer(temp);
        
        int pos = sb.indexOf("class", 0);
        int lastIndexOf=-1;
        int endpos;
        
        while(pos != -1) {
            endpos = sb.indexOf("\n", pos);
            if(sb.substring(pos, endpos+1).contains(";")) {
                lastIndexOf = endpos;
            } else {
                break;
            }
            pos = sb.indexOf("class", endpos);
        }
        if(lastIndexOf!=-1)
            sb.insert(lastIndexOf+1, structsAndEnums);
        
        writeFile(f, sb);
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
            } else if(textToReplace.contains("WDllDefs")) {
                textToReplaceWith = "";
            } else if(textToReplace.contains("boost")) {
                textToReplaceWith = "";
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
            endIndex = content.indexOf(";", endIndex);
            enumSI.add(index);
            enumEI.add(endIndex);
            index = content.indexOf(name, endIndex);
        }
        
        return content;
    }
    
    public int findMatchingBracket(StringBuffer content, int index) {
        int openingBracket = content.indexOf("{", index);
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
        String [] toReplace = {"using std::exit;"};
        String [] toReplaceWith = {"#include <myexit.h>"};
    
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
}
