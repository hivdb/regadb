package net.sf.regadb.db.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

public class HQLTest 
{
    public static void main(String [] args) throws IOException
    {
    	Arguments as = new Arguments();
    	ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
    	PositionalArgument user = as.addPositionalArgument("user", true);
    	PositionalArgument pass = as.addPositionalArgument("pass", true);
    	PositionalArgument hql = as.addPositionalArgument("query.hql", true);
    	
    	if(!as.handle(args))
    		return;
    	
    	if(confDir.isSet())
    		RegaDBSettings.createInstance(confDir.getValue());
    	else
    		RegaDBSettings.createInstance();
    	
        Login login = null;
        try
        {
            login = Login.authenticate(user.getValue(), pass.getValue());
        }
        catch (WrongUidException e)
        {
            e.printStackTrace();
        }
        catch (WrongPasswordException e)
        {
            e.printStackTrace();
        } 
        catch (DisabledUserException e) 
        {
            e.printStackTrace();
        }
        
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(
        		new File(hql.getValue())));
        String line;
        while((line = br.readLine()) != null)
        	if(!line.startsWith("//"))
        		sb.append(line).append(' ');
        br.close();
        
        Transaction t = login.createTransaction();
        Query q = t.createQuery(sb.toString());
        System.out.println(q.list().size() +" result(s)");
        t.commit();
        login.closeSession();
    }
}
