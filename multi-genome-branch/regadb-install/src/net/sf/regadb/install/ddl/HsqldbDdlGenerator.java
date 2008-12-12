package net.sf.regadb.install.ddl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.util.reflection.PackageUtils;

import org.apache.commons.io.FileUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HsqldbDdlGenerator extends DdlGenerator
{
	public HsqldbDdlGenerator() {
        super("org.hibernate.dialect.HSQLDialect", "org.hsqldb.jdbcDriver");
    }

    public static void main(String [] args) 
	{
		String fileName = PackageUtils.getDirectoryPath("net.sf.regadb.install.ddl.schema", "regadb-install");
		HsqldbDdlGenerator gen = new HsqldbDdlGenerator();
		gen.generate(fileName+File.separatorChar+"hsqldbSchema.sql");
	}

    @Override
    protected String processLine(String line) {
        return line.replaceAll("varchar\\(255\\)", "longvarchar");
    }
}
