package exercisesolution;

/*
 * In this file the @category Javadoc tag is used.
 * 
 * The category tag is a proposed Javadoc tag.  It is not
 * an official tag yet.  Eclipse 3.3 (and maybe even older versions)
 * already support this tag.
 * 
 * You can use the @category tag to group several methods and/or attributes
 * together.  Eclipse offers support for these tags in the Outline and Members view.
 * You can choose the categories you want to hide/display in these views by clicking on 
 * the small right triangle.
 * 
 * For more information check out:
 *    - http://java.sun.com/j2se/javadoc/proposed-tags.html
 *    - http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4085608
 */
import java.util.Date;

/**
 * Class representing an Email
 * 
 * @invar an email must have a valid sender address at all times |
 *        this.canHaveAsSender( this.getSender() )
 * @invar an email must have a valid receiver address at all times |
 *        this.canHaveAsReceiver( this.getReceiver() )
 * @invar an email must have a valid subject line at all times |
 *        this.canHaveAsSubject( this.getSubject() )
 * @invar an email must have a valid body at all times | this.canHaveAsBody(
 *        this.getBody() )
 * @invar an email must have a valid date at all times | this.canHaveAsDate(
 *        this.getDate() )
 * 
 * @author Rutger Claes
 */
public class Email {

	/*
	 * A regular expression that can be used to check whether email addresses
	 * are obeying the rules specified in the assignment
	 */
	public static final String VALID_EMAIL_ADDRESS_REGEX = "^[a-zA-Z0-9]+([_\\.-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*\\.[A-Za-z]{2,6}";

	/*
	 * The maximum length of an email subject line
	 */
	public static final int MAX_SUBJECT_LENGTH = 256;

	/*
	 * A regular expression used to check the content of the subject line
	 */
	public static final String INVALID_SUBJECT_REGEX = "(.*[\\n\\r].*)+";

	/**
	 * A constructor to create a new email message with the current date
	 * 
	 * @effect Email( sender, receiver, body, subject, new Date() )
	 * 
	 * @param sender
	 *            the sender of the email message
	 * @param receiver
	 *            the receiver of the email message
	 * @param body
	 *            the body of the email message
	 * @param subject
	 *            the subject line of the email message
	 * 
	 * @throws InvalidEmailAddressException
	 *             when the sender specified is an invalid sender address |
	 *             !new.canHaveAsSender( sender )
	 * 
	 * @category Constructor
	 */
	public Email(String sender, String receiver, String body, String subject)
			throws InvalidEmailAddressException {
		this.changeSender(sender);
		this.setReceiver(receiver);
		this.changeBody(body);
		this.changeSubject(subject);
		this.date = new Date();
	}

	/**
	 * A constructor to create a new email message
	 * 
	 * @effect changeSender( sender )
	 * @effect setReceiver( receiver )
	 * @effect changeBody( body )
	 * @effect changeSubject( subject )
	 * 
	 * @post The date of the email will equal the specified <date> value |
	 *       new.getDate().equals( date )
	 * 
	 * @param sender
	 *            the sender of the email message
	 * @param receiver
	 *            the receiver of the email message
	 * @param body
	 *            the body of the email message
	 * @param subject
	 *            the subject line of the email message
	 * @param date
	 *            the date of the email message
	 * 
	 * @throws InvalidDateException
	 *             when the date specified is an invalid date |
	 *             !new.canHaveAsDate( date )
	 * @throws InvalidEmailAddressException
	 *             when the sender specified is an invalid sender address |
	 *             !new.canHaveAsSender( sender )
	 * 
	 * @category Constructor
	 */
	public Email(String sender, String receiver, String body, String subject,
			Date date) throws InvalidDateException,
			InvalidEmailAddressException {
		this.changeSender(sender);
		this.setReceiver(receiver);
		this.changeBody(body);
		this.changeSubject(subject);

		if (!this.canHaveAsDate(date)) {
			throw new InvalidDateException(date);
		}
		this.date = (Date) date.clone();
	}

	/**
	 * A method to change the sender address of this email message
	 * 
	 * @post the sender of this email will be the specified newSender |
	 *       new.getSender().equals( newSender )
	 * 
	 * @param newSender
	 *            the sender of the email message
	 * 
	 * @throws InvalidEmailAddressException
	 *             when the sender specified is an invalid sender address |
	 *             !this.canHaveAsSender( newSender )
	 * 
	 * @category Sender
	 * 
	 * @see Email#canHaveAsSender(String)
	 * @see Email#getSender()
	 */
	public void changeSender(String newSender)
			throws InvalidEmailAddressException {
		if (!this.canHaveAsSender(newSender)) {
			throw new InvalidEmailAddressException(newSender);
		}

		this.setSender(newSender);
	}

	/*
	 * Implemented in a defensive way
	 */
	private String sender;

	/**
	 * @return the sender of this email message
	 * 
	 * @category Sender
	 * @see Email#changeSender(String)
	 */
	public String getSender() {
		return this.sender;
	}

	/**
	 * A method to check whether this email can accept <sender> as its new value
	 * for sender
	 * 
	 * @param sender
	 *            sender address to check
	 * 
	 * @return false if the sender is an invalid email address false if the
	 *         sender is null | if( sender == null || new.canHaveAsEmailAddress(
	 *         sender ) ) { | result = false | } | else { | result = true |
	 * 
	 * @see Email#canHaveAsEmailAddress(String)
	 * @category Sender
	 */
	public boolean canHaveAsSender(String sender) {
		return Email.canHaveAsEmailAddress(sender);
	}

	/**
	 * @param newSender
	 *            new sender
	 * @category Sender
	 */
	private void setSender(String newSender) {
		this.sender = newSender;
	}

	/*
	 * Implemented in a nominal way
	 */
	private String receiver;

	/**
	 * Change the receiver address of the email message
	 * 
	 * @pre the newReceiver address passed to this method must be a valid one |
	 *      this.canHaveAsReceiver( newReceiver )
	 * 
	 * @post the receiver of this email will be the specified newReceiver |
	 *       new.getReceiver().equals( newReceiver )
	 * 
	 * @param newReceiver
	 *            the new receiver address
	 * 
	 * @see Email#canHaveAsReceiver(String)
	 * 
	 * @category Receiver
	 */
	public void setReceiver(String newReceiver) {
		this.receiver = newReceiver;
	}

	/**
	 * @return the receiver address of this message
	 * @category Receiver
	 */
	public String getReceiver() {
		return this.receiver;
	}

	/**
	 * A method to check whether this email can accept <receiver> as its new
	 * value for receiver
	 * 
	 * @param receiver
	 *            receiver address to check
	 * 
	 * @return false if the receiver is an invalid email address false if the
	 *         receiver is null | if( receiver == null ||
	 *         this.canHaveAsEmailAddress( receiver ) ) { | result = false | } |
	 *         else { | result = true | }
	 * 
	 * @category Receiver
	 */
	public boolean canHaveAsReceiver(final String receiver) {
		return Email.canHaveAsEmailAddress(receiver);
	}

	/**
	 * Check whether this message can accept <emailAddress> as its emailAddress
	 * 
	 * @param 	emailAddress	email address to check
	 * 
	 * @return	false if the email address is a null reference
	 * 			false if the email doesn't match the VALID_EMAIL_ADDRESS_REGEX
	 * 			| if( emailAddress == null ) {
	 * 			|	result = false
	 * 			| }
	 * 			| if( !emailAddress.matches( VALID_EMAIL_ADDRESS_REGEX ) ) {
	 * 			|	result = false
	 * 			| }
	 * 			| else {
	 * 			|	result = true
	 * 			| }
	 * 
	 * @category	Sender
	 * @category	Receiver
	 */
	public static boolean canHaveAsEmailAddress(final String emailAddress) {
		return emailAddress != null
				&& emailAddress.matches(VALID_EMAIL_ADDRESS_REGEX);
	}

	/*
	 * Implemented in a total way
	 */
	private String body = "";

	/**
	 * Change the body of an email message
	 * 
	 * @post if a valid newBody is passed, the body of this email will be the
	 *       specified newBody. Otherwise, the body will have a default body |
	 *       if( this.canHaveAsBody( newBody ) ) { | new.getBody().equals(
	 *       newBody ) | } else { | new.canHaveAsBody( new.getBody() ) |
	 *       new.getBody().equals( new.getDefaultBody() ) | }
	 * 
	 * @param newBody
	 *            new body for the email message
	 * 
	 * @category Body
	 * 
	 * @see Email#canHaveAsBody(String)
	 * @see Email#getBody()
	 * @see Email#getDefaultBody()
	 */
	public void changeBody(final String newBody) {
		if (this.canHaveAsBody(newBody)) {
			this.setBody(newBody);
		} else {
			this.setBody(Email.getDefaultBody());
		}
	}

	/**
	 * Check whether this email message can have <body> as its body
	 * 
	 * @param body
	 *            body to check
	 * 
	 * @return false if the body passed is a null reference | if( body == null ) { |
	 *         result = false | } | else { | result = true | }
	 * 
	 * @category Body
	 */
	public boolean canHaveAsBody(final String body) {
		return body != null;
	}

	/**
	 * @param newBody
	 *            new body for this email
	 * 
	 * @category Body
	 */
	private void setBody(final String newBody) {
		this.body = newBody;
	}

	/**
	 * @return body of this email message
	 * 
	 * @category Body
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 * Returns a default body value
	 * 
	 * @return the result of this method will always be a valid body for this
	 *         email message | for every email instanceof Email { |
	 *         email.canHaveAsBody( result ) | }
	 * 
	 * @category Body
	 */
	public static String getDefaultBody() {
		return "";
	}

	/*
	 * Implemented in a total way
	 */
	private String subject = "";

	/**
	 * Change the subject of this message
	 * 
	 * @param subject
	 *            the new subject
	 * 
	 * @post if a valid subject is passed, the subject of this email will be the
	 *       specified subject. Otherwise, the email will have a default subject |
	 *       if( this.canHaveAsSubject( subject ) ) { | new.getSubject().equals(
	 *       subject ) | } else { | new.canHaveAsSubject( new.getSubject() ) |
	 *       new.getSubject().equals( new.getDefaultSubject() ) | }
	 * 
	 * @category Subject
	 * 
	 * @see Email#canHaveAsSubject(String)
	 * @see Email#getDefaultSubject()
	 * @see Email#getSubject()
	 */
	public void changeSubject(final String subject) {
		if (this.canHaveAsSubject(subject)) {
			this.setSubject(subject);
		} else {
			this.setSubject(Email.getDefaultSubject());
		}
	}

	/**
	 * Check whether this email message can have <subject> as its subject line
	 * 
	 * @param subject
	 *            subject to check
	 * 
	 * @return false if the subject is a null reference false if the subject has
	 *         a length > MAX_SUBJECT_LENGTH false if the subject contains
	 *         illegal characters | if( subject == null ) | result = false | if(
	 *         subject.length() > Email.MAX_SUBJECT_LENGTH ) | result = false |
	 *         if( subject.matches( INVALID_SUBJECT_REGEX ) ) | result = false |
	 *         else | result = true
	 * 
	 * @see Email#MAX_SUBJECT_LENGTH
	 * @see Email#INVALID_SUBJECT_REGEX
	 * 
	 * @category Subject
	 */
	public boolean canHaveAsSubject(final String subject) {
		return subject != null && subject.length() <= MAX_SUBJECT_LENGTH
				&& !subject.matches(INVALID_SUBJECT_REGEX);
	}

	/**
	 * @return subject line of this message
	 * @category Subject
	 * @see Email#changeSubject(String)
	 */
	public final String getSubject() {
		return this.subject;
	}

	/**
	 * Returns a default, valid subject for email messages
	 * 
	 * @return a default subject line | for every email instanceof Email { |
	 *         email.canHaveAsSubject( result ) | }
	 * 
	 * @category Subject
	 */
	public static String getDefaultSubject() {
		return "";
	}

	/**
	 * @param newSubject
	 *            the new subject line
	 * @category Subject
	 */
	private final void setSubject(final String newSubject) {
		this.subject = newSubject;
	}

	/*
	 * Implemented in a defensive way
	 */
	private final Date date;

	/**
	 * Check whether this message can have <date> as its date
	 * 
	 * @param date
	 *            date to check
	 * 
	 * @return false if the date is a null reference false if the date is in the
	 *         future | if( date == null ) | result = false | if( date.getTime() <=
	 *         System.currentMillis() ) | result = false | else | result = true
	 * 
	 * @see Email#getDate()
	 * @category Date
	 */
	public boolean canHaveAsDate(final Date date) {
		return date != null && date.getTime() <= System.currentTimeMillis();
	}

	/**
	 * @return the date of this message
	 * @category Date
	 */
	public final Date getDate() {
		return (Date) this.date.clone();
	}
}