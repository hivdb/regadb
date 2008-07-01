package net.sf.regadb.ui.transaction;

public class CannotCreateTransactionException extends Exception
{
	public CannotCreateTransactionException()
	{
		super("Cannot create a transaction: an existing transaction is still running");
	}
}
