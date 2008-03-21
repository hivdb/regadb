
/** Java class "DatabaseTableCatalog.java" generated from Poseidon for UML.
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
 * A Set that contains all Tables that are mentioned in the WhereClauses of
 * the Query. (Relevant as well as non-relevant tables.)
 * </p>
 * 
 */
public class DatabaseTableCatalog {

   ///////////////////////////////////////
   // associations

/**
 * <p>
 * 
 * </p>
 */
    
    public Collection<Table> tables = new TreeSet<Table>(); // of type Table

    public DatabaseTableCatalog() {
    }
    
        
    
   ///////////////////////////////////////
   // access methods for associations

    public Collection<Table> getTables() {
        return tables;
    }
    public Table getTable(String name) {
        if (name.startsWith("TMP_")) {
            return null;
        }
        Iterator<Table> iter = tables.iterator();
        while (iter.hasNext()) {
            Table table = (Table)iter.next();
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }
        return null;
    }
    public Table doGetTable(String name) {
//        if (name.startsWith("TMP_")) {
//            return new TempMoleculeIDSet();
//        }
        Table table = getTable(name);
        if (table == null) {
            table = new Table(name);
            tables.add(table);
        }
        return table;
    }
    public void addTable(Table table) {
        if (! tables.contains(table)) tables.add(table);
    }
    public void removeTable(Table table) {
        this.tables.remove(table);
    }
    public int size() {
        return tables.size();
    }

} // end DatabaseTableCatalog
