package net.sf.regadb.util.mail;

import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * E-mail utils class.
 * 
 * @author pieter
 */
public class MailUtils {
	/**
	 * Send a mail.	
	 */
	public static void sendMail(String host, String from, Set<String> recipients, CharSequence subject, CharSequence message) throws AddressException, MessagingException {
		Properties props = System.getProperties();

		props.put("mail.smtp.host", host);

		Session session = Session.getDefaultInstance(props, null);

		MimeMessage mimeMessage = new MimeMessage(session);
		mimeMessage.setFrom(new InternetAddress(from));
		for (String recepient : recipients) 
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
		mimeMessage.setSubject(subject.toString());
		mimeMessage.setText(message.toString());

		Transport.send(mimeMessage);
	}
}
