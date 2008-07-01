/*
 * Created on Dec 15, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db;

public enum QueryDefinitionRunStatus 
{
    Running (0),
    Finished (1),
    Failed (2);
    
    private final int value;
    
    QueryDefinitionRunStatus(int value) 
    {
        this.value = value;
    }
    
    public int getValue() 
    {
        return this.value;
    }
    
    public static QueryDefinitionRunStatus getQueryDefinitionRunStatus(QueryDefinitionRun run)
    {
        switch (run.getStatus()) 
        {
	        case 0:
	            return QueryDefinitionRunStatus.Running;
	        case 1:
	            return QueryDefinitionRunStatus.Finished;
	        case 2:
	            return QueryDefinitionRunStatus.Failed;
        }
        
        return null;
    }
}
