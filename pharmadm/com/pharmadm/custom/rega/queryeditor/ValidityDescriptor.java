
/** Java class "ValidityDescriptor.java" generated from Poseidon for UML.
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
import java.util.Collection;

/**
 * <p>
 * 
 * </p>
 */
public class ValidityDescriptor {



  ///////////////////////////////////////
  // operations


/**
 * <p>
 * Returns a Collection with all AtomicWhereClauses with one or more
 * unbound input variables.
 * </p>
 * <p>
 * 
 * @return a Collection with all atomic clauses with one or more unbound
 * input variables
 * </p>
 */
    public Collection getAWCsWithUnboundInputVariables() {        
        // your code here
        return null;
    } // end getAWCsWithUnboundInputVariables        

/**
 * <p>
 * Returns a Collection with all AtomicWhereClauses with one or more unset
 * parameters or parameters with an invalid value.
 * </p>
 * <p>
 * 
 * @return a Collection with all atomic clauses with unset or invalid
 * parameters
 * </p>
 */
    public Collection getAWCsWithUnsetParameters() {        
        // your code here
        return null;
    } // end getAWCsWithUnsetParameters        

/**
 * <p>
 * Returns a Collection with all invalid AtomicWhereClauses, which is the
 * union of the clauses with unbound input variables and the clauses with
 * unset or invalid parameters.
 * </p>
 * <p>
 * 
 * @return a Collection with all invalid atomic clauses.
 * </p>
 */
    public Collection getInvalidAtomicWhereClauses() {        
        // your code here
        return null;
    } // end getInvalidAtomicWhereClauses        

} // end ValidityDescriptor



