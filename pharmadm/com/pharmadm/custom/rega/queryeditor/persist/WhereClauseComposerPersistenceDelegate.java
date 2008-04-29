package com.pharmadm.custom.rega.queryeditor.persist;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;

import com.pharmadm.custom.rega.queryeditor.WhereClauseComposer;

public class WhereClauseComposerPersistenceDelegate extends
		DefaultPersistenceDelegate {

    protected Expression instantiate(Object oldInstance, Encoder out) {
        WhereClauseComposer composer = (WhereClauseComposer)oldInstance;
        composer.setOwner(null);
        return new Expression(composer, composer.getClass(), "new", new Object[]{composer.getWords()});
    }
	
}
