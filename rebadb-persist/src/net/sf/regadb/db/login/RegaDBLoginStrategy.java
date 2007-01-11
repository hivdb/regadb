package net.sf.regadb.db.login;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.encrypt.Encrypt;

public class RegaDBLoginStrategy implements ILoginStrategy
{	
	public boolean authenticate(String uid, String password, Login login) throws WrongUidException, WrongPasswordException
	{
        Transaction t = login.createTransaction();
        
        SettingsUser settings
            = t.getSettingsUser(uid);
        
        t.commit();
        
        if(settings==null)
        	throw new WrongUidException();
        else if(!settings.getPassword().equals(Encrypt.encryptMD5(password)))
        	throw new WrongPasswordException();
       
        
        return settings != null && settings.getPassword().equals(Encrypt.encryptMD5(password));
	}
}