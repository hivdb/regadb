
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
import java.util.Set;
//import com.pharmadm.custom.rega.chem.search.*;

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
    private HashMap singularNameMap = new HashMap();
    
    public Collection tables = new TreeSet(); // of type Table

    public DatabaseTableCatalog() {
        initNameMap();
    }
    
    private void initNameMap() {
        singularNameMap.put("MOLECULES", "molecule");
        singularNameMap.put("CALC_RESULT", "calculated result");
        singularNameMap.put("VIRUS_APPEARANCE", "virus");
        singularNameMap.put("VIRUS_APP_SELECTED", "selected virus");
        singularNameMap.put("VIRUS_APP_CHIMERIC", "chimeric virus");
        singularNameMap.put("RESISTANCE", "resistance experiment");
        singularNameMap.put("PATIENT_MEDICATION", "medicatie");
        singularNameMap.put("COD_MEDICATION", "drug");
        singularNameMap.put("COD_ALGORITHM", "resistance algorithm");
        singularNameMap.put("AA_SEQUENCE", "amino acid sequence");
        singularNameMap.put("NT_SEQUENCE", "nucleotide sequence");
        singularNameMap.put("SEQUENCE_NT_MUTATION", "nucleotide mutation");
        singularNameMap.put("SEQUENCE_NT_INSERTION", "nucleotide insertion");
        singularNameMap.put("SEQUENCE_NT_DELETION", "nucleotide deletion");
        singularNameMap.put("SEQUENCE_AA_MUTATION", "amino acid mutation");
        singularNameMap.put("SEQUENCE_AA_INSERTION", "amino acid insertion");
        singularNameMap.put("SEQUENCE_AA_DELETION", "amino acid deletion");
        singularNameMap.put("ALGO_RESULT", "resistance algorithm result");
    }
        
    
   ///////////////////////////////////////
   // access methods for associations

    public Collection getTables() {
        return tables;
    }
    public Table getTable(String name) {
        if (name.startsWith("TMP_")) {
            return null;
        }
        Iterator iter = tables.iterator();
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
            String singularName = (String)singularNameMap.get(name.toUpperCase());
            if (singularName != null) {
                table.setSingularName(singularName);
            }
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
