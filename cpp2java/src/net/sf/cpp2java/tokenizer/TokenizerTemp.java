package net.sf.cpp2java.tokenizer;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.cppast.CPPParserTokenManager;
import net.sourceforge.pmd.cpd.cppast.SimpleCharStream;
import net.sourceforge.pmd.cpd.cppast.Token;
import net.sourceforge.pmd.cpd.cppast.TokenMgrError;

public class TokenizerTemp 
{
    public static SimpleCharStream charStream = null;
    
    public static ArrayList<Token> getTokens(StringBuffer sourceFile)
    {
        ArrayList<Token> tokenList = new ArrayList<Token>();
        
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(sourceFile.toString()));
        
        StringBuffer sb = sourceCode.getCodeBuffer();
        
        try {
            if (charStream == null) {
                charStream = new SimpleCharStream(new StringReader(sb.toString()));
            } else {
                charStream.ReInit(new StringReader(sb.toString()));
            }
            CPPParserTokenManager.ReInit(charStream);
            CPPParserTokenManager.setFileName(sourceCode.getFileName());
            
            Token currToken = CPPParserTokenManager.getNextToken();
            while (currToken.image.length() > 0) {
                //tokenEntries.add(new TokenEntry(currToken.image, sourceCode.getFileName(), currToken.beginLine));
                //System.err.println(currToken.image);
                //System.err.println(currToken.beginColumn + "-" + currToken.endColumn);
                tokenList.add(currToken);
                currToken = CPPParserTokenManager.getNextToken();
            }
        }
        catch (TokenMgrError err) {
            err.printStackTrace();
            System.out.println("Skipping " + sourceCode.getFileName() + " due to parse error");
            return null;
        }
        
        return tokenList;
    }
}
