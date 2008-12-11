package net.sf.regadb.install.ddl;

import java.io.File;

import net.sf.regadb.util.reflection.PackageUtils;

public class MsSqlDdlGenerator extends DdlGenerator
{
    public static void main(String [] args) 
    {
        String fileName = PackageUtils.getDirectoryPath("net.sf.regadb.install.ddl.schema", "regadb-install");
        MsSqlDdlGenerator gen = new MsSqlDdlGenerator();
        gen.generate(fileName+File.separatorChar+"mssqlSchema.sql");
    }
    
    public MsSqlDdlGenerator(){
    	super("org.hibernate.dialect.SQLServerDialect", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

	@Override
	protected String processLine(String line) {
		return line.replaceAll(" unique,",",")
					.replaceAll("varchar\\(255\\)","text")
					.replaceAll("varbinary\\(255\\)","varbinary(MAX)");
	}
}
