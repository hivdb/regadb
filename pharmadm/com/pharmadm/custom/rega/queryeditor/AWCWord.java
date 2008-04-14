
/** Java interface "AWCWord.java" generated from Poseidon for UML.
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

import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

/**
 * <p>
 * An AWCWord can produce an unambiguous String representation of it's current
 * configuration. The string representation can be concatenated (with
 * appropriate whitespace added) resulting in a clause. A Word may (or may
 * not) be reused in several different clauses (for now: query
 * definitions and user intelligible sentences).
 * </p>
 * 
 */
public interface AWCWord extends ConfigurableWord {

   ///////////////////////////////////////
  // associations



  ///////////////////////////////////////
  // operations

/**
 * <p>
 * Produces a String representation of the current value of the Word
 * </p>
 * <p>
 * 
 * @return a String that shows the current value of the Word
 * </p>
 */
    public abstract String acceptWhereClause(QueryVisitor visitor);

} // end AWCWord





