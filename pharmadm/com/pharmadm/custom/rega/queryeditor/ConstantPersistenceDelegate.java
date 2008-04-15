package com.pharmadm.custom.rega.queryeditor;

import java.beans.Encoder;
import java.beans.Expression;

import com.pharmadm.custom.rega.queryeditor.constant.Constant;

public class ConstantPersistenceDelegate extends java.beans.DefaultPersistenceDelegate{
    protected Expression instantiate(Object oldInstance, Encoder out) {
        Constant constant = (Constant) oldInstance;
        return new Expression(constant, oldInstance.getClass(), "new", new Object[]{constant.getSuggestedValues()});
    }
}
