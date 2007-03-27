package net.sf.regadb.db.login;

public class WrongUidException extends Exception
{
	public WrongUidException()
	{
		super("The user id is invalid");
	}
}
