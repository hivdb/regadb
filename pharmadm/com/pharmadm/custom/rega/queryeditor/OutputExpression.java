
/** Java class "OutputExpression.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.util.*;

/**
 * <p>
 * An OutputExpression's responsibility is to build the expression String
 * that is inlined while building a query String, when there is need to
 * refer to an InputVariable that is associated with the OutputVariable
 * that owns the OutputExpression. The expression is built from
 * FixedStrings, inputVariables and FromVariables.
 * </p>
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p> 
 * An OutputExpression is an OrderedAWCWordList that is constrained to contain only
 * FixedStrings, FromVariables and InputVariables, but no OutputVariables.
 *
 * An outputexpression does not have an owner, thus no single AtomicWhereClause is 
 * guaranteed to have all (non-fixedstring-)words of this.
 */
public class OutputExpression extends OrderedAWCWordList {
    
    public OutputExpression() {
    }
    
    /**
     * <p>
     * Builds the expression String from FixedStrings, inputVariables and
     * FromVariables.
     * </p>
     * <p>
     *
     * @return a String that contains the expression of the OutputVariable.
     * </p>
     */
    public String generateExpressionString() {
        return acceptWhereClause(JDBCManager.getInstance().getQueryVisitor());
    }
}



