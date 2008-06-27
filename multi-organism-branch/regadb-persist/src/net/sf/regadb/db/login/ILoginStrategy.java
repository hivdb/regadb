package net.sf.regadb.db.login;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.session.Login;


public interface ILoginStrategy
{
	public SettingsUser authenticate(String uid, String password, Login login) throws WrongUidException, WrongPasswordException;
}
