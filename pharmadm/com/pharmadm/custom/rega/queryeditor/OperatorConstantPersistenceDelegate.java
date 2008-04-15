package com.pharmadm.custom.rega.queryeditor;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;

import com.pharmadm.custom.rega.queryeditor.constant.OperatorConstant;

public class OperatorConstantPersistenceDelegate extends DefaultPersistenceDelegate {
    protected Expression instantiate(Object oldInstance, Encoder out) {
    	OperatorConstant constant = (OperatorConstant) oldInstance;
        return new Expression(constant, oldInstance.getClass(), "new", new Object[]{constant.getSuggestedValues(), constant.getHumanStringValue()});
    }
}
