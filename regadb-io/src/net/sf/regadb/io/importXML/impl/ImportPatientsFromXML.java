package net.sf.regadb.io.importXML.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ImportPatientsFromXML 
{
    public static void main(String[] args) throws SAXException, IOException, WrongUidException, WrongPasswordException, DisabledUserException 
    {
        if(args.length<3)
        {
            System.err.println("Usage: <patients.xml> <regadb user> <regadb password> [dataset]");
        }
        else
        {
            ImportXML instance = new ImportXML(args[1], args[2]);
            instance.importPatients(new InputSource(new FileReader(new File(args[0]))), (args.length > 3 ? args[3] : null));
            instance.login.closeSession();
        }
    }
}
