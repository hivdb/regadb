package net.sf.regadb.db.login;

import net.sf.regadb.db.session.Login;


public interface ILoginStrategy
{
	public boolean authenticate(String uid, String password, Login login);
}
