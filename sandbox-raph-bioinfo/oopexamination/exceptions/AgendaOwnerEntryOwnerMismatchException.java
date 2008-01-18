/**
 * 
 */
package oopexamination.exceptions;

import oopexamination.Person;

/**
 * Class to handle entries whose owner is diffrent from agenda owner
 * @author rsanged0
 *
 */
@SuppressWarnings("serial")
public class AgendaOwnerEntryOwnerMismatchException extends Exception 
{
	
private  Person firstPerson,secondPerson;

/**
 * @param firstPerson
 * @param secondPerson
 */
public AgendaOwnerEntryOwnerMismatchException(Person firstPerson, Person secondPerson) 
{
	super("Could not add an entry because owner of entry of  "+firstPerson.getName()+" is not the same as owner of agenda "+secondPerson.getName());
	this.firstPerson = firstPerson;
	this.secondPerson = secondPerson;
}

/**
 * @return the firstPerson
 */
public Person getFirstPerson() {
	return firstPerson;
}

/**
 * @return the secondPerson
 */
public Person getSecondPerson() {
	return secondPerson;
}

/**
 * @param firstPerson the firstPerson to set
 */
public void setFirstPerson(Person firstPerson) {
	this.firstPerson = firstPerson;
}

/**
 * @param secondPerson the secondPerson to set
 */
public void setSecondPerson(Person secondPerson) {
	this.secondPerson = secondPerson;
}
}
