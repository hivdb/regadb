package net.sf.regadb.db.login;

public class DisabledUserException extends Exception
{
    public DisabledUserException()
    {
        super("The user account is not yet enabled");
    }
}
