package oopexamination.exceptions;

import oopexamination.Person;

/**
 * Handle meeting set with pesrson without agendas
 * @author rsanged0
 *
 */
@SuppressWarnings("serial")
public class SecondPersonHasNoAgendaException extends Exception 
{

	private Person thePersonWithoutAgenda;

	/**
	 * @param thePersonWithoutAgenda
	 */
	public SecondPersonHasNoAgendaException(Person thePersonWithoutAgenda) 
	{
		this.thePersonWithoutAgenda = thePersonWithoutAgenda;
	}

	/**
	 * @return the thePersonWithoutAgenda
	 */
	public Person getThePersonWithoutAgenda()
	{
		return thePersonWithoutAgenda;
	}

	/**
	 * @param thePersonWithoutAgenda the thePersonWithoutAgenda to set
	 */
	public void setThePersonWithoutAgenda(Person thePersonWithoutAgenda) 
	{
		this.thePersonWithoutAgenda = thePersonWithoutAgenda;
	}

}
