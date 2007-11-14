
/** Java class "InvalidWhereClauseException.java" generated from Poseidon for UML.
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
 * 
 * </p>
 */
public class InvalidWhereClauseException extends Exception {

   ///////////////////////////////////////
   // associations

/**
 * <p>
 * 
 * </p>
 */
    public ValidityDescriptor validityDescriptor; 


   ///////////////////////////////////////
   // access methods for associations

    public ValidityDescriptor getValidityDescriptor() {
        return validityDescriptor;
    }
    public void setValidityDescriptor(ValidityDescriptor validityDescriptor) {
        this.validityDescriptor = validityDescriptor;
    }

} // end InvalidWhereClauseException



