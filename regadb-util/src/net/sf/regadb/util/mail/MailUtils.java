package net.sf.regadb.util.mail;

import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.EmailConfig;
import net.sf.regadb.util.settings.RegaDBSettings;

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
	
	public static void main(String args[]){
		Arguments as = new Arguments();
		ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
		ValueArgument aTo = as.addValueArgument("t", "recipient-csv-list", false);
		ValueArgument aFrom = as.addValueArgument("f", "sender", false);
		ValueArgument aHost = as.addValueArgument("h", "host", false);
		ValueArgument aSubj = as.addValueArgument("s", "subject", false).setValue("Test subject.");
		ValueArgument aMsg = as.addValueArgument("m", "message", false).setValue("Test message.");
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		String from = null, host = null;
		Set<String> to = null;
		
		EmailConfig ecfg = RegaDBSettings.getInstance().getInstituteConfig().getEmailConfig();
		if(ecfg != null){
			from = ecfg.getFrom();
			to = ecfg.getTo();
			host = ecfg.getHost();
		}
		
		if(aTo.isSet()){
			to = new TreeSet<String>();
			for(String s : aTo.getValue().split(","))
				to.add(s);
		}
		
		if(aFrom.isSet())
			from = aFrom.getValue();
		
		if(aHost.isSet())
			host = aHost.getValue();
		
		try {
			sendMail(host,from,to,aSubj.getValue(),aMsg.getValue());
			System.err.println("email succes.");
		} catch (Exception e) {
			System.err.println("email fail.");
			e.printStackTrace();
		}
	}
}
