package exercisesolution;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static junit.framework.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A blackbox unit test for the {@link Email} class
 * 
 * @author Rutger Claes <rutger.claes@cs.kuleuven.be>
 * @see Email
 */
public class EmailTest {

	/**
	 * A Calendar required to create Date objects
	 */
	private Calendar calendar;

	/**
	 * Sets a new calendar before every test
	 * 
	 * @post there is a valid calendar this.getCalendar() instanceof Calendar
	 * 
	 */
	@Before
	public void setupCalendar() {
		this.setCalendar(new GregorianCalendar());
	}

	/**
	 * Unsets the calendar attribute
	 * 
	 * @post the previous calendar is removed this.getCalendar() == null
	 */
	@After
	public void destroyCalendar() {
		this.setCalendar(null);
	}

	/**
	 * A simple test for the long constructor
	 * 
	 * This test checks the initialization of all attributes passed to the
	 * constructor.
	 * 
	 * @see Email#Email(String, String, String, String, Date)
	 */
	@Test
	public void simpleLongConstructorTest() {
		String sender = "me@somehost.com";
		String receiver = "you@otherhost.com";
		String subject = "Some subject";
		String body = "Some content";
		Date currentDate = this.createDate();

		Email email = null;

		try {
			email = new Email(sender, receiver, body, subject, currentDate);
		} catch (Exception e) {
			fail("Exception caught: " + e.getMessage());
		}

		// Quick check for all class invariants
		this.checkInvariants(email);

		// Test the sender, receiver, body, subject and date
		assertEquals("The sender is initialized correctly", sender, email
				.getSender());
		assertEquals("The receiver is initialized correctly", receiver, email
				.getReceiver());
		assertEquals("The body is initialized correctly", body, email.getBody());
		assertEquals("The subject is initialized correctly", subject, email
				.getSubject());
		assertEquals("The date is initialized correctly", currentDate, email
				.getDate());
	}

	/**
	 * A simple test for the shorter constructor
	 * 
	 * This test checks the initialization of all attributes passed to the
	 * constructor.
	 * 
	 * @see Email#Email(String, String, String, String)
	 */
	@Test
	public void simpleShortConstructorTest() {
		String sender = "me@somehost.com";
		String receiver = "you@otherhost.com";
		String subject = "Some subject";
		String body = "Some content";

		Date beforeDate = this.createDate();
		Date afterDate = null;

		Email email = null;

		try {
			email = new Email(sender, receiver, body, subject);
			afterDate = this.createDate();
		} catch (Exception e) {
			fail("Exception caught: " + e.getMessage());
		}

		// Quick check for all class invariants
		this.checkInvariants(email);

		// Test the sender, receiver, body, subject and date
		assertEquals("The sender is initialized correctly", sender, email
				.getSender());
		assertEquals("The receiver is initialized correctly", receiver, email
				.getReceiver());
		assertEquals("The body is initialized correctly", body, email.getBody());
		assertEquals("The subject is initialized correctly", subject, email
				.getSubject());

		assertTrue("The date is initialized correctly", beforeDate
				.compareTo(email.getDate()) <= 0
				&& afterDate.compareTo(email.getDate()) >= 0);
	}

	/**
	 * A test to see how the constructor responds to an invalid sender address
	 * argument.
	 * 
	 * @see EmailTest#testInvalidSenderChange()
	 */
	@Test
	public void testLongConstructorWithInvalidSender() {
		// Pass an invalid email address to the constructor
		{
			String sender = "me@somehost@com";
			String receiver = "you@otherhost.com";
			String subject = "Some subject";
			String body = "Some content";
			Date currentDate = this.createDate();

			@SuppressWarnings("unused")
			Email email;

			try {
				email = new Email(sender, receiver, body, subject, currentDate);
				fail("No exception thrown when passing an invalid sender");
			} catch (InvalidEmailAddressException e) {
				assertEquals("The address causing the exception should be set",
						e.getEmailAddress(), sender);
			} catch (Exception e) {
				fail("Wrong exception caught: " + e.getMessage());
			}
		}

		// Pass a null reference as sender email address
		{
			String sender = null;
			String receiver = "you@otherhost.com";
			String subject = "Some subject";
			String body = "Some content";
			Date currentDate = this.createDate();

			@SuppressWarnings("unused")
			Email email;

			try {
				email = new Email(sender, receiver, body, subject, currentDate);
				fail("No exception thrown when passing an invalid sender");
			} catch (InvalidEmailAddressException e) {
				assertEquals("The address should be set to null", e
						.getEmailAddress(), null);
			} catch (Exception e) {
				fail("Wrong exception caught");
			}
		}
	}

	/**
	 * Check how the constructor responds to an invalid body
	 * 
	 * @see Email#canHaveAsBody(String)
	 * @see Email#Email(String, String, String, String, Date)
	 */
	@Test
	public void testLongConstructorWithInvalidBody() {
		String sender = "me@somehost.com";
		String receiver = "you@otherhost.com";
		String subject = "Some subject";
		String body = null;
		Date currentDate = this.createDate();

		Email email = null;

		try {
			email = new Email(sender, receiver, body, subject, currentDate);
		} catch (Exception e) {
			fail("Unexpected exception caught");
		}

		// Check the invariants
		this.checkInvariants(email);

		// Check the constructor contract
		assertTrue("Email has a valid body", email.canHaveAsBody(email
				.getBody()));
		assertTrue("Email has default body", email.getBody().equals(
				Email.getDefaultBody()));
	}

	/**
	 * Do some tests with the
	 * {@link Email#Email(String, String, String, String, Date)}-constructor
	 * and a number of invalid subject lines
	 * 
	 * @see Email#canHaveAsSubject(String)
	 * @see Email#Email(String, String, String, String, Date)
	 */
	@Test
	public void testLongConstructorWithInvalidSubject() {
		{
			// do the test and check the result
			String sender = "me@somehost.com";
			String receiver = "you@otherhost.net";
			String invalidSubject = "Some subject\nSpread over two lines";
			String body = "Some content";
			Date someDate = this.createDate();
			@SuppressWarnings("unused")
			Email email = null;

			try {
				email = new Email(sender, receiver, body, invalidSubject,
						someDate);
			} catch (Exception e) {
				fail("Exception caught: " + e.getMessage());
			}

			this.checkInvariants(email);

			System.out.println(invalidSubject);
			assertEquals("The email has the default subject line", email
					.getSubject(), Email.getDefaultSubject());
		}

		{
			// do the test and check the result
			String sender = "me@somehost.com";
			String receiver = "you@otherhost.net";
			String invalidSubject = "content";

			// Construct a very long and invalid subject
			while (invalidSubject.length() <= Email.MAX_SUBJECT_LENGTH) {
				invalidSubject = invalidSubject + invalidSubject;
			}

			String body = "Some content";
			Date someDate = this.createDate();
			@SuppressWarnings("unused")
			Email email = null;

			try {
				email = new Email(sender, receiver, body, invalidSubject,
						someDate);
			} catch (Exception e) {
				fail("Exception caught: " + e.getMessage());
			}

			this.checkInvariants(email);

			assertEquals("The email has the default subject line", email
					.getSubject(), Email.getDefaultSubject());
		}

		{
			// do the test and check the result
			String sender = "me@somehost.com";
			String receiver = "you@otherhost.net";
			String body = "Some content";
			Date someDate = this.createDate();
			@SuppressWarnings("unused")
			Email email = null;

			try {
				email = new Email(sender, receiver, body, null, someDate);
			} catch (Exception e) {
				fail("Exception caught: " + e.getMessage());
			}

			this.checkInvariants(email);

			assertEquals("The email has the default subject line", email
					.getSubject(), Email.getDefaultSubject());
		}
	}

	/**
	 * Check how the constructor responds to a null date
	 * 
	 * @see Email#Email(String, String, String, String, Date)
	 * @see Email#canHaveAsDate(Date)
	 */
	@Test
	public void testLongConstructorWithNullDate() {
		// do the test and check the result
		String sender = "me@somehost.com";
		String receiver = "you@otherhost.net";
		String subject = "Some subject";
		String body = "Some content";

		@SuppressWarnings("unused")
		Email email = null;

		try {
			email = new Email(sender, receiver, body, subject, null);
			fail("No exception was thrown");
		} catch (InvalidDateException e) {
			assertEquals("The date should be set to null in the exception", e
					.getDate(), null);
		} catch (Exception e) {
			fail("Exception caught: " + e.getMessage());
		}
	}

	/**
	 * A test to see whether the constructor rejects dates in the future.
	 * 
	 * The constructor should throw an IllegalArgumentException
	 * 
	 * @see Email#Email(String, String, String, String, Date)
	 * @see Email#canHaveAsDate(Date)
	 * @see Email#getDate()
	 */
	@Test
	public void testConstructorDateInFuture() {
		// do the test and check the result
		String sender = "me@somehost.com";
		String receiver = "you@otherhost.net";
		String subject = "Some subject";
		String body = "Some content";
		Date futureDate = this.createDate(2010, 1, 12);
		@SuppressWarnings("unused")
		Email email = null;

		try {
			email = new Email(sender, receiver, body, subject, futureDate);
			fail("No exception was thrown");
		} catch (InvalidDateException e) {
			assertEquals("The date should be set in the exception",
					e.getDate(), futureDate);
		} catch (Exception e) {
			fail("Exception caught: " + e.getMessage());
		}
	}

	/**
	 * A test to see whether the constructor rejects null dates
	 * 
	 * @see Email#Email(String, String, String, String, Date)
	 * @see Email#canHaveAsDate(Date)
	 */
	@Test
	public void testNullDateChange() {
		// do the test and check the result
		String sender = "me@somehost.com";
		String receiver = "you@otherhost.net";
		String subject = "Some subject";
		String body = "Some content";
		@SuppressWarnings("unused")
		Email email = null;

		try {
			email = new Email(sender, receiver, body, subject, null);
			fail("No exception was thrown");
		} catch (InvalidDateException e) {
			assertEquals("The date should be set to null", e.getDate(), null);
		} catch (Exception e) {
			fail("Exception caught: " + e.getMessage());
		}
	}

	/**
	 * A simple test to see whether encapsulation of the date attribute is
	 * correct
	 * 
	 * When we retrieve the Date object from an email using
	 * {@link Email#getDate()} and subsequently change something on the object
	 * returned by the email, the internal date attribute of email should not
	 * have changed.
	 * 
	 * @see Email#getDate()
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testDateEncapsulation() {
		// Start with a simple email
		Email email = this.generateSimpleEmail();

		Date emailDate = email.getDate();
		emailDate.setDate(emailDate.getDate() + 1);

		assertFalse("Encapsulation isn't broken", email.getDate().equals(
				emailDate));
	}

	/**
	 * A test to see whether the implementation of
	 * {@link Email#changeSender(String)} recognizes some valid email addresses
	 * and behaves correctly.
	 * 
	 * A number of email addresses is passed to the
	 * {@link Email#changeSender(String)}. The email addresses are valid
	 * according to the specification in {@link Email#changeSender(String)} and
	 * {@link Email#canHaveAsSender(String)}.
	 * 
	 * Encapsulation is not checked because String object are considered
	 * immutable.
	 * 
	 * @see Email#changeSender(String)
	 * @see Email#canHaveAsSender(String)
	 * @see Email#canHaveAsEmailAddress(String)
	 * @see Email#getSender()
	 */
	@Test
	public void testValidSenderChange() {
		List<String> validEmailAddresses = Arrays.asList(
				"rutger.claes@cs.kuleuven.be", "info@somehost.be",
				"postmaster@sub.domain.museum",
				"me_myself.and-I@host.with.numb3r.nl", "pooky256@email.name",
				"Capital_Letter-test.mail@someHost.com");

		for (String newValidSender : validEmailAddresses) {
			// Start with a simple email
			Email email = this.generateSimpleEmail();

			// Assert that all class invariants are ok
			this.checkInvariants(email);

			// do the test and check the result
			try {
				email.changeSender(newValidSender);
			} catch (Exception e) {
				fail("Exception caught when presenting " + newValidSender
						+ ": " + e.getMessage());
			}

			// Check whether all invariants still apply
			this.checkInvariants(email);

			// Check whether the change happened
			assertEquals(
					"Emails sender address changed to the new sender address",
					email.getSender(), newValidSender);
		}
	}

	/**
	 * A test to see whether the implementation of
	 * {@link Email#changeSender(String)} recognizes and refuses some invalid
	 * email addresses.
	 * 
	 * A number of email addresses is passed to the
	 * {@link Email#changeSender(String)}. The email addresses are invalid
	 * according to the specification in {@link Email#changeSender(String)} and
	 * {@link Email#canHaveAsSender(String)}.
	 * 
	 * The expected behavior of the {@link Email#changeSender(String)} is to
	 * throw an {@link IllegalArgumentException} when an invalid email address
	 * is passed. These exceptions do not require a catch statement, yet the
	 * correctness of the behavior is tested here.
	 * 
	 * @see Email#changeSender(String)
	 * @see Email#canHaveAsSender(String)
	 * @see Email#canHaveAsEmailAddress(String)
	 * @see Email#getSender()
	 */
	@Test
	public void testInvalidSenderChange() {
		List<String> invalidEmailAddresses = Arrays.asList("invalidemail",
				"my mail@cs.kuleuven.be", ".@host.com", ".mail@host.com",
				"mail.@host.com", "mail..address@host.com", "mail--@host.com",
				"mail--address@host.com", "-mail@host.com", "_mail@host.com",
				"mail__address@host.com", "mail_@host.com", "mail.host.com",
				"mail@host", "mail\n@host.com", "mail%address@host.com",
				"mail@@host.com", "mail@some@host.com", "mail@some..host.com",
				"mail@some.", "mail@host.longtld");

		for (String invalidSender : invalidEmailAddresses) {
			// Start with a simple email
			Email email = this.generateSimpleEmail();
			String oldSender = email.getSender();

			// Assert that all class invariants are ok
			this.checkInvariants(email);

			try {
				email.changeSender(invalidSender);
				fail("No exception thrown when presenting: " + invalidSender);
			} catch (InvalidEmailAddressException e) {
				assertEquals("The email address should be set", e
						.getEmailAddress(), invalidSender);
			} catch (Exception e) {
				fail("Wrong Exception caught: " + e.getMessage());
			}

			// Check whether all invariants still apply
			this.checkInvariants(email);

			// Check whether the change happened
			assertFalse(
					"Email address has changed into an invalid email address: "
							+ invalidSender, email.getSender().equals(
							invalidSender));
			assertEquals("Email still has old sender email address", oldSender,
					email.getSender());
		}
	}

	/**
	 * A test to see how the {@link Email#changeSender(String)} method responds
	 * to the null argument. The expected behavior is to throw a
	 * {@link IllegalArgumentException} and keep the old sender address.
	 * 
	 * @see Email#changeSender(String)
	 * @see Email#canHaveAsSender(String)
	 * @see Email#canHaveAsEmailAddress(String)
	 * @see Email#getSender()
	 */
	@Test
	public void testNullSenderChange() {
		// Start with a simple email
		Email email = this.generateSimpleEmail();
		String oldSender = email.getSender();

		// Assert that all class invariants are ok
		this.checkInvariants(email);

		try {
			email.changeSender(null);
			fail("No exception thrown when presenting NULL");
		} catch (InvalidEmailAddressException e) {
			assertEquals("The address should be set to null", e
					.getEmailAddress(), null);
		} catch (Exception e) {
			fail("Wrong Exception caught: " + e.getMessage());
		}

		// Check whether all invariants still apply
		this.checkInvariants(email);

		// Check whether the change happened
		assertNotNull("The email still has a sender address", email.getSender());
		assertEquals("Email still has old sender email address", oldSender,
				email.getSender());
	}

	/**
	 * This is the only test we can do considering the strict pre conditions of
	 * the #{@link Email#setReceiver(String)} method
	 */
	@Test
	public void testChangingReceiver() {
		// Start with a simple email
		Email email = this.generateSimpleEmail();

		// Assert that all class invariants are ok
		this.checkInvariants(email);

		// do the test and check the result
		String validReceiver = "rutger.claes@cs.kuleuven.be";
		try {
			email.setReceiver(validReceiver);
		} catch (Exception e) {
			fail("Exception caught: " + e.getMessage());
		}

		// Check whether all invariants still apply
		this.checkInvariants(email);

		// Check if the change happened
		assertEquals("Receiver address has changed to new address",
				validReceiver, email.getReceiver());
	}

	/**
	 * Test changing the body to a new value
	 * 
	 * @see Email#changeBody(String)
	 * @see Email#canHaveAsBody(String)
	 */
	@Test
	public void testValidBodyChange() {
		// do the test and check the result
		String body = "Some valid content\nSpread over multiple lines\nContaining strange characters like #%&^";
		Email email = this.generateSimpleEmail();

		email.changeBody(body);

		this.checkInvariants(email);

		assertEquals("The email has the new body", email.getBody(), body);
	}

	/**
	 * Test changing the body of an email message to an invalid value (null)
	 * 
	 * @see Email#changeBody(String)
	 * @see Email#canHaveAsBody(String)
	 */
	@Test
	public void testNullBodyChange() {
		// do the test and check the result
		Email email = this.generateSimpleEmail();

		email.changeBody(null);

		this.checkInvariants(email);

		assertEquals("The email has the new body", email.getBody(), Email
				.getDefaultBody());
	}

	/**
	 * Test changing the subject line of an email message
	 * 
	 * @see Email#changeSubject(String)
	 * @see Email#canHaveAsSubject(String)
	 */
	@Test
	public void testValidSubjectChange() {
		// do the test and check the result
		String subject = "Some valid subject containing strange but valid characters like *$%^}";
		Email email = this.generateSimpleEmail();

		email.changeSubject(subject);

		this.checkInvariants(email);

		assertEquals("The email has the new subject line", email.getSubject(),
				subject);
	}

	/**
	 * Test changing the subject line of an email message to a subject line
	 * containing multiple lines.
	 * 
	 * @see Email#changeSubject(String)
	 * @see Email#canHaveAsSubject(String)
	 */
	@Test
	public void testInvalidCharactersInSubjectChange() {
		String invalidSubject = "Some subject\nSpread over two lines";

		Email email = this.generateSimpleEmail();

		try {
			email.changeSubject(invalidSubject);
		} catch (Exception e) {
			fail("Exception caught: " + e.getMessage());
		}

		this.checkInvariants(email);

		assertEquals("The email has the default subject line", email
				.getSubject(), Email.getDefaultSubject());
	}

	/**
	 * Test changing the subject line of an email message to a subject line that
	 * is longer than ${@link Email#MAX_SUBJECT_LENGTH}
	 * 
	 * @see Email#changeSubject(String)
	 * @see Email#canHaveAsSubject(String)
	 * @see Email#MAX_SUBJECT_LENGTH
	 */
	@Test
	public void testLongSubjectChange() {
		// do the test and check the result
		String invalidSubject = "content";

		// Construct a very long and invalid subject
		while (invalidSubject.length() <= Email.MAX_SUBJECT_LENGTH) {
			invalidSubject = invalidSubject + invalidSubject;
		}

		Email email = this.generateSimpleEmail();

		try {
			email.changeSubject(invalidSubject);
		} catch (Exception e) {
			fail("Exception caught: " + e.getMessage());
		}

		this.checkInvariants(email);

		assertEquals("The email has the default subject line", email
				.getSubject(), Email.getDefaultSubject());
	}

	/**
	 * Test changing the subject line of a message to null
	 * 
	 * @see Email#changeSubject(String)
	 * @see Email#canHaveAsSubject(String)
	 */
	@Test
	public void testNullSubjectChange() {
		// do the test and check the result

		Email email = this.generateSimpleEmail();

		try {
			email.changeSubject(null);
		} catch (Exception e) {
			fail("Exception caught: " + e.getMessage());
		}

		this.checkInvariants(email);

		assertEquals("The email has the default subject line", email
				.getSubject(), Email.getDefaultSubject());
	}

	/**
	 * Helper function
	 * 
	 * This method will create a simple email with an empty body, empty subject,
	 * default date and some simple email addresses
	 * 
	 * @return an email with a valid sender, receiver, subject, body and date
	 *         attribute | result instanceof Email | && result.canHaveAsSender(
	 *         result.getSender() ) | && result.canHaveAsReceiver(
	 *         result.getReceiver() ) | && result.canHaveAsSubject(
	 *         result.getSubject() ) | && result.canHaveAsBody( result.getBody() ) | &&
	 *         result.canHaveAsDate( result.getDate() )
	 */
	private Email generateSimpleEmail() {
		String senderAddress = "sender@domain.com";
		String receiverAddress = "receiver@otherdomain.be";

		try {
			return new Email(senderAddress, receiverAddress, "", "");
		} catch (Exception e) {
			fail("Creating a simple email message failed due to an exception: "
					+ e.getMessage());
			return null;
		}
	}

	/**
	 * This method checks some of the simple class invariants of the
	 * {@link Email} class.
	 * 
	 * The test in this method assume the implementation of
	 * {@link Email#canHaveAsSender(String)},
	 * {@link Email#canHaveAsReceiver(String)},
	 * {@link Email#canHaveAsSubject(String)},
	 * {@link Email#canHaveAsBody(String)} and {@link Email#canHaveAsDate(Date)}
	 * are correct and according to their specifications.
	 */
	private void checkInvariants(final Email email) {
		assertNotNull("The object is not null", email);

		// Checking the sender attribute
		assertNotNull("The object has a sender", email.getSender());
		assertTrue("The object has a valid sender", email.canHaveAsSender(email
				.getSender()));

		// Checking the receiver attribute
		assertNotNull("The object has a receiver", email.getReceiver());
		assertTrue("The object has a valid receiver", email
				.canHaveAsReceiver(email.getReceiver()));

		// Checking the subject attribute
		assertNotNull("The object has a subject", email.getSubject());
		assertTrue("The object has a valid subject", email
				.canHaveAsSubject(email.getSubject()));

		// Checking the body attribute
		assertNotNull("The object has a body", email.getBody());
		assertTrue("The object has a valid body", email.canHaveAsBody(email
				.getBody()));

		// Checking the date attribute
		assertNotNull("The object has a date", email.getDate());
		assertTrue("The object has a valid date", email.canHaveAsDate(email
				.getDate()));
	}

	private void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	private Calendar getCalendar() {
		return this.calendar;
	}

	/**
	 * Creates a new date object using the current calendar
	 * 
	 * @param year
	 *            the year
	 * @param month
	 *            the month
	 * @param day
	 *            the day
	 * 
	 * @return a new date with the year, month and day set to <year>, <month>
	 *         and <day> respectively result instanceof Date && result.getDay() ==
	 *         <day> && result.getMonth() == <month> && result.getYear() ==
	 *         <year>
	 */
	private Date createDate(int year, int month, int day) {
		this.getCalendar().set(year, month, day);
		return this.getCalendar().getTime();
	}

	/**
	 * Creates a new date object using the current calendar
	 * 
	 * @return a new date representing some time between the moment this method
	 *         is called and the moment the method returns result instanceof
	 *         Date && result.getTime() = System.currentTimeMillis() +/- delta
	 */
	private Date createDate() {
		this.getCalendar().setTimeInMillis(System.currentTimeMillis());
		return this.getCalendar().getTime();
	}
}