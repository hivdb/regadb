package com.pharmadm.custom.rega.queryeditor;

import java.beans.Encoder;
import java.beans.Expression;

public class SuggestedValuesPersistenceDelegate extends java.beans.DefaultPersistenceDelegate{
    protected Expression instantiate(Object oldInstance, Encoder out) {
    	SuggestedValues suggestedValues = (SuggestedValues) oldInstance;
        return new Expression(suggestedValues, suggestedValues.getClass(), "new", new Object[]{suggestedValues.getSuggestedValuesWithoutQuery(), suggestedValues.getQuery(), suggestedValues.isMandatory()});
    }

}
