package net.sf.regadb.db.login;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;

public class RegaDBLoginStrategy implements ILoginStrategy
{	
	public boolean authenticate(String uid, String password, Login login)
	{
        Transaction t = login.createTransaction();
        
        SettingsUser settings
            = t.getSettingsUser(uid, password);
        
        t.commit();
        
        return settings != null ;//&& Encrypt.encryptMD5(password).equals(password);
	}
}