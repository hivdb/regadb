package com.pharmadm.custom.rega.queryeditor;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;

public class WhereClauseComposerPersistenceDelegate extends
		DefaultPersistenceDelegate {

    protected Expression instantiate(Object oldInstance, Encoder out) {
        WhereClauseComposer composer = (WhereClauseComposer)oldInstance;
        composer.setOwner(null);
        return new Expression(composer, composer.getClass(), "new", new Object[]{composer.getWords()});
    }
	
}
