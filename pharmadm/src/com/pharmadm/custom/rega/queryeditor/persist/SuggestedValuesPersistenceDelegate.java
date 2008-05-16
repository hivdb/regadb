package com.pharmadm.custom.rega.queryeditor.persist;

import java.beans.Encoder;
import java.beans.Expression;

import com.pharmadm.custom.rega.queryeditor.constant.SuggestedValues;

public class SuggestedValuesPersistenceDelegate extends java.beans.DefaultPersistenceDelegate{
    protected Expression instantiate(Object oldInstance, Encoder out) {
    	SuggestedValues suggestedValues = (SuggestedValues) oldInstance;
        return new Expression(suggestedValues, suggestedValues.getClass(), "new", new Object[]{suggestedValues.getSuggestedValuesWithoutQuery(), suggestedValues.getQuery(), suggestedValues.isMandatory()});
    }

}
