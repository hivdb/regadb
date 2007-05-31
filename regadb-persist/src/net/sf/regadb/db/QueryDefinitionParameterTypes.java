/*
 * Created on Dec 15, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db;

public enum QueryDefinitionParameterTypes {
    STRING (0),
    INTEGER (1),
    DOUBLE (2),
    DATE (3),
    GENERICDRUG (4),
    COMMERCIALDRUG (5),
    TEST (6),
    TESTTYPE (7),
    PROTEIN (8),
    ATTRIBUTE (9),
    ATTRIBUTEGROUP (10);
    
    private final int value;
    
    QueryDefinitionParameterTypes(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public static QueryDefinitionParameterTypes getQueryDefinitionParameterType(QueryDefinitionParameterType type)
    {
        switch (type.getId()) 
        {
	        case 0:
	            return QueryDefinitionParameterTypes.STRING;
	        case 1:
	            return QueryDefinitionParameterTypes.INTEGER;
	        case 2:
	            return QueryDefinitionParameterTypes.DOUBLE;
	        case 3:
	            return QueryDefinitionParameterTypes.DATE;
	        case 4:
	            return QueryDefinitionParameterTypes.GENERICDRUG;
	        case 5:
	            return QueryDefinitionParameterTypes.COMMERCIALDRUG;
	        case 6:
	            return QueryDefinitionParameterTypes.TEST;
	        case 7:
	            return QueryDefinitionParameterTypes.TESTTYPE;
	        case 8:
	            return QueryDefinitionParameterTypes.PROTEIN;
	        case 9:
	            return QueryDefinitionParameterTypes.ATTRIBUTE;
	        case 10:
	            return QueryDefinitionParameterTypes.ATTRIBUTEGROUP;
        }
        
        return null;
    }
}
