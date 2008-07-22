package com.pharmadm.custom.rega.queryeditor.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.regadb.util.string.StringTokenizer;

import com.pharmadm.custom.rega.awccomposition.PrimitiveConstantAdditionComposition;
import com.pharmadm.custom.rega.awccomposition.PrimitiveInputAdditionComposition;
import com.pharmadm.custom.rega.queryeditor.AWCWord;
import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.FixedString;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.NullComposition;
import com.pharmadm.custom.rega.queryeditor.OrderedAWCWordList;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.SimpleAtomicWhereClause;

public class AWCPrototypeBuilder {
	private AWCPrototypeCatalog catalog;
	
	public AWCPrototypeBuilder(AWCPrototypeCatalog catalog) {
		this.catalog = catalog;
	}
	
	
    public AtomicWhereClause getClause(String... expressions) {
        
    	AtomicWhereClause aClause = new SimpleAtomicWhereClause();
        aClause.setCompositionBehaviour(new NullComposition());
        
        HashMap<String, List<AWCWord>> exprs = new HashMap<String, List<AWCWord>>();
        
        for (String line:expressions) {
        	parseLine(line, exprs, aClause);
        }

        
        
        return aClause;
    }
    
    private void parseLine(String line, HashMap<String, List<AWCWord>> exprs, AtomicWhereClause clause) {
    	String name = getBeforeChar(line, '=');
    	assignResult(clause, exprs, name, parseExpression(getAfterChar(line, '='), exprs));
    }
    
    private List<AWCWord> parseExpression(String line, HashMap<String, List<AWCWord>> exprs) {
    	List<AWCWord> list = new ArrayList<AWCWord>();
    	
    	StringTokenizer tn = new StringTokenizer(line, "",":{}");
    	while (tn.hasMoreTokens()) {
    		String token  = tn.nextToken();
    		if (token.equals("{")) {
    			addWordsToList(list, parseVariable(tn, exprs));
    		}
    		else {
    			list.add(new FixedString(token));
    		}
    	}
    	
    	return list;
    }
    
    /**
     * parse the variable that just started on the tokenizer
     * and return its result as a List of AWCWords
     * @param tn
     */
    private List<AWCWord> parseVariable(StringTokenizer tn, HashMap<String, List<AWCWord>> exprs) {
		String varname = tn.nextToken();
		String s = tn.nextToken();
		if (s.equals("}")) {
			return exprs.get(varname);
		}
		else if (s.equals(":")) {
			String type = tn.nextToken();
			tn.nextToken();
			List<AWCWord> l = new ArrayList<AWCWord>();
			l.add(getWord(varname, type));
			return l;
		}
		return null;
    }

    private void assignResult(AtomicWhereClause clause, HashMap<String, List<AWCWord>> exprs, String variableName, List<AWCWord> expression) {
    	if (variableName.startsWith("ovar:")) {
    		exprs.put("ovar", assignOutputVariable(getAfterChar(variableName, ':'), expression));
    	}
    	else if (variableName.equals("visualisation")) {
    		addWordsToList(clause.getVisualizationClauseList(), expression);
    		exprs.put(variableName, expression);
    	}
    	else if (variableName.equals("sql")) {
    		addWordsToList(clause.getWhereClauseComposer(), expression);
    		exprs.put(variableName, expression);
    	}
    	else if (variableName.equals("group")) {
    		for (AWCWord word : expression) {
    			if (word instanceof FixedString) {
    				clause.addGroup(((FixedString) word).getString());
    			}
    			else if (word instanceof InputVariable) {
    				clause.addGroup(((InputVariable) word).getObject().getTableObject().getDescription());
    			}
    		}
    	}
    	else if (variableName.equals("composition")) {
    		if (expression.get(0) instanceof FixedString) {
    			String comp = ((FixedString)expression.get(0)).getString();
    			if (comp.equals("primitive constant addition")) {
    				clause.setCompositionBehaviour(new PrimitiveConstantAdditionComposition());
    			}
    			else if (comp.equals("primitive ivar addition")) {
    				clause.setCompositionBehaviour(new PrimitiveInputAdditionComposition());
    			}
    		}
    	}
    	else {
    		exprs.put(variableName, expression);
    	}    	
    }
    
    
    /**
     * create a new AWCWord 
     * @param name
     * @param type
     * @return
     */
    private AWCWord getWord(String wordName, String type) {
    	if (wordName.equals("ivar")) {
    		return new InputVariable(catalog.getObject(type));
    	}
    	else if (wordName.equals("constant")) {
    		return HibernateCatalogUtils.getConstant(catalog.getObject(type), null);
    	}
    	else if (wordName.equals("operator")) {
    		if (type.endsWith("Calc")) {
    			return HibernateCatalogUtils.getCalculationOperator(catalog.getObject(type.substring(0, type.length()-4)));
    		}
    		else if (type.endsWith("Comparison")) {
    			return HibernateCatalogUtils.getComparisonOperator(catalog.getObject(type.substring(0, type.length()-10)), false);
    		}
    		else if (type.equals("null")) {
    			return HibernateCatalogUtils.getNullComparisonOperator();
    		}
    		else if (type.equals("interval")) {
    			return HibernateCatalogUtils.getIntervalComparisonOperator();
    		}
    		else {
    			return null;
    		}
    	}
    	else {
    		return null;
    	}
    }	
    
    /**
     * assign the given expression to an outputvariable of the given type
     * @param variableType the type of the outputvariable
     * @param expression  the list of AWCWords that must become the outpvariables expression
     * @return an AWCWord list containing the outputvariable
     */
    private List<AWCWord> assignOutputVariable(String variableType, List<AWCWord> expression) {
		OutputVariable ovar = new OutputVariable(catalog.getObject(variableType));
		addWordsToList(ovar.getExpression(), expression);
		List<AWCWord> l =  new ArrayList<AWCWord>();
		l.add(ovar);
		return l;
    }
    
    /**
     * return the part of the given string after the given character
     * @param str
     * @param c
     * @return
     */
    private String getAfterChar(String str, char c) {
    	return str.substring(str.indexOf(c)+1).trim();
    }
    
    /**
     * return the part of the given string before the given character
     * @param str
     * @param c
     * @return
     */
    private String getBeforeChar(String str, char c) {
    	return str.substring(0, str.indexOf(c)).trim();
    }    
    
    /**
     * add the given list of AWCWords to the given wordlist
     * @param wordList
     * @param expression
     */
    private void addWordsToList(OrderedAWCWordList wordList, List<AWCWord> expression) {
		for (AWCWord word : expression) {
			wordList.addWord(word);
		}
    }
    
    /**
     * 
     * @param addTo list to add words to
     * @param toAdd
     */
    private void addWordsToList(List<AWCWord> addTo, List<AWCWord> toAdd) {
    	if (toAdd != null) {
        	addTo.addAll(toAdd);
    	}
    }    
}
