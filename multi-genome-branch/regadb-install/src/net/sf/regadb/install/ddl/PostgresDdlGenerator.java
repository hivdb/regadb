package net.sf.regadb.install.ddl;

import java.io.File;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.util.reflection.PackageUtils;

public class PostgresDdlGenerator extends DdlGenerator
{
    private String schema = "regadbschema";
    private int maxLength = 63;
    
    private Set<String> foreignKeys = new HashSet<String>();
    
    public PostgresDdlGenerator() {
		super("org.hibernate.dialect.PostgreSQLDialect", "org.postgresql.Driver");
	}

	public static void main(String [] args) 
    {
        String fileName = PackageUtils.getDirectoryPath("net.sf.regadb.install.ddl.schema", "regadb-install");
        PostgresDdlGenerator gen = new PostgresDdlGenerator();
        gen.generate(fileName+File.separatorChar+"postgresSchema.sql");
    }

    protected String processLine(String str) 
    {
    	str = str.replaceAll(" int4", " integer ")
    			 .replaceAll("varchar\\(255\\)", "text");
    	
        if(str.startsWith("create table"))
        {
            return processCreateTable(str);
        }
        else if(str.startsWith("alter table"))
        {
            return processAlterTable(str);
        }
        
        return str;
    }
    
    private String processCreateTable(String str)
    {
        String [] words = str.split(" ");
        String s = schema+".";
        String tableName = words[2];

        StringBuffer lineBuffer = new StringBuffer(str);

        //for m-n relations such as commercial_generic, the schema name is omitted
        if(tableName.indexOf(s) == -1){
            //lineBuffer.insert(str.indexOf(tableName), s);
        }
        tableName = tableName.substring(tableName.lastIndexOf('.')+1);

        int indexOfPrimaryKey = lineBuffer.indexOf("primary key");
        if(indexOfPrimaryKey!=-1)
        {
            String primaryKeyArgs = lineBuffer.substring(indexOfPrimaryKey, lineBuffer.indexOf(")",indexOfPrimaryKey));
                
            if(!primaryKeyArgs.contains(",") && primaryKeyArgs.contains("_ii"))
            {
                int endOfPrimKeyName = str.indexOf(' ', str.indexOf('('));
                int endOfPrimKeyArgs = str.indexOf(',', endOfPrimKeyName);

                lineBuffer.delete(endOfPrimKeyName, endOfPrimKeyArgs);
                lineBuffer.insert(endOfPrimKeyName, " integer default nextval('" + tableName + "_" + tableName + "_ii_seq')");
            }
        }
        
        return lineBuffer.toString();
    }
    
    private String processAlterTable(String str)
    {
        String strBackup = str;
        String s = schema+'.';
        
        String [] words = str.split(" ");
        String tableName = words[2];
        if(tableName.indexOf(s) == -1){
            //strBackup = strBackup.replace("alter table ", "alter table "+s);
        }else
            tableName = tableName.substring(tableName.lastIndexOf('.')+1);
            
        

        int indexOfForeignKey = strBackup.indexOf("foreign key");
        if(indexOfForeignKey!=-1)
        {
            int firstBracket = strBackup.indexOf('(', indexOfForeignKey);
            String key = strBackup.substring(firstBracket+1, strBackup.indexOf(')', firstBracket));
            strBackup += "("+key+") ON UPDATE CASCADE";
            
            String alterTablePublic = "alter table "+ s;
            String alterTable = "alter table ";
            if(strBackup.indexOf(alterTablePublic)!=-1)
            {
                alterTable = alterTablePublic;
            }
            
            if(strBackup.indexOf("add constraint ")!=-1)
            {
                //tableName = strBackup.substring(strBackup.indexOf(alterTable)+alterTable.length(),strBackup.indexOf(" ", strBackup.indexOf(alterTable)+alterTable.length()));
                int referenceIndex = strBackup.indexOf("references")+"references".length();
                String referencingTable = strBackup.substring(referenceIndex, strBackup.indexOf('(', referenceIndex));
                referencingTable = referencingTable.trim();
                referencingTable = referencingTable.replaceAll(schema+".", "");
                String fk_name = '"'+ getForeignKey(tableName, referencingTable) +'"';
                StringBuffer strBuffer = new StringBuffer(strBackup);
                int indexOfAddConstraint = strBuffer.indexOf("add constraint ")+"add constraint ".length();
                strBuffer.delete(indexOfAddConstraint, strBackup.indexOf(" ", indexOfAddConstraint));
                strBuffer.insert(indexOfAddConstraint, fk_name);
                strBackup = strBuffer.toString();
            }
        }
        
        return strBackup;
    }
    
    private String getForeignKey(String table, String refTable){
        String fk = "FK_"+table+"_"+refTable;
        if(fk.length() > maxLength){
            System.err.print("truncated '"+ fk +"'("+fk.length()+") to '");
            
            fk = "FK_"+ truncate(maxLength - 3, "_", table, refTable);
            
            System.err.println(fk +"'("+fk.length()+")");
        }
        
        if(!foreignKeys.add(fk))
            System.out.println("duplicate foreign key: "+ fk);
        return fk;
    }
    
    protected void write(PrintStream out, List<Query> qs){
    	out.println("create schema "+ schema +';');
    	out.println("set search_path to "+ schema +';');
    	
    	super.write(out, qs);
    }
}
