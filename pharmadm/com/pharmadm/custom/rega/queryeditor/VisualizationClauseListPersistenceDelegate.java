package com.pharmadm.custom.rega.queryeditor;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;

public class VisualizationClauseListPersistenceDelegate extends
		DefaultPersistenceDelegate {
    protected Expression instantiate(Object oldInstance, Encoder out) {
    	VisualizationClauseList visList = (VisualizationClauseList)oldInstance;
    	visList.setOwner(null);
        return new Expression(visList, visList.getClass(), "new", new Object[]{visList.getWords()});
    }

}
