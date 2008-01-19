package oopexamination;

import java.util.HashSet;
import java.util.Set;
/** 
 * Person class to deal with owners of meetings or holders of agenda
 * @author rsanged0
 *@pre The name can not be set once initialized
 */

public class Person 
{
	private Set<Agenda> agendas = new HashSet<Agenda>();

	private final String name;

	/**
	 * Default constructor to initialize new person
	 * Sets the name to a blank string but thia means person has no name?
	 */
	public Person()
	{
		this.name="" ;

	}

	/**
	 * Restricted constructor
	 * Sets the name
	 * @param name 
	 * -the name of the person
	 */
	public Person(String name) 
	{

		this.name = name;
	}


	/**
	 * Arguemented constructor 
	 * Sets name and agendas owned by this person
	 * @param name
	 * @param agendas
	 * @pre The given name must be valid
	 * @pre The agenda supplied must not be null
	 * @post The name is set to the given name
	 * @post agenda is set to the supplied agenda
	 * @note Validation of name and agenda is not implemented
	 */
	public Person(final String name, Set<Agenda> agendas) 
	{
		this.name = name;
		this.agendas = agendas;
	}

	/**
	 * Add one more agenda to the agenda collections of this person
	 * @param agenda
	 * @post agenda added to list of this person
	 */
	public void addAgenda(Agenda agenda)
	{
		agendas.add(agenda);
	}

	/**
	 * Return all agendas owned by this person
	 * @return the agendas
	 */
	public Set<Agenda> getAgendas() {
		return agendas;
	}

	/**
	 * Return name of person
	 * @return name
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * Set agendas owned by this person from the given collection of agendas
	 * @param agendas the agendas to set
	 */
	public void setAgendas(Set<Agenda> agendas) 
	{
		this.agendas = agendas;
	}

}
