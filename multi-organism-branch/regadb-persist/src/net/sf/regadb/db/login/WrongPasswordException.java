package net.sf.regadb.db.login;

public class WrongPasswordException extends Exception
{
	public WrongPasswordException()
	{
		super("The user password is invalid");
	}
}
