/*
 * Created on Jan 3, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db;

import java.util.List;
import java.util.Properties;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;

public class RegadbReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {

    public RegadbReverseEngineeringStrategy(ReverseEngineeringStrategy arg0) {
        super(arg0);
    }

    @Override
    public Properties getTableIdentifierProperties(TableIdentifier id) {
        Properties p = new Properties();
        p.put("sequence", id.getName() + "_" + id.getName() + "_ii_seq");
        return p;
    }

    @Override
    public String getTableIdentifierStrategyName(TableIdentifier id) {
        return "sequence";
    }

    @Override
    public boolean excludeForeignKeyAsCollection(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns) {
        if (referencedTable.getName().equals("drug_class")
            || fromTable.getName().equals("attribute_nominal_value")
            || fromTable.getName().equals("test_nominal_value")
            || fromTable.getName().equals("commercial_generic")
            || fromTable.getName().equals("dataset_access")
            || referencedTable.getName().equals("patient")
            || referencedTable.getName().equals("viral_isolate")
            || referencedTable.getName().equals("nt_sequence")
            || referencedTable.getName().equals("aa_sequence")
            || referencedTable.getName().equals("therapy"))
            return false;
        else
            return true;
        //return super.excludeForeignKeyAsCollection(arg0, arg1, arg2, arg3, arg4);
    }
}
