package com.cloudfordev.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.cloudfordev.security.EnigmaMachine;

/**
 * Notification is a class used to record exceptions for future review.  <br>
 * <br>
 * This class can notify in the following ways:<br>
 * <br>
 * 1.  Page - Send an SMS text message to the oncall pager<br>
 * 2.  Email - Send an email to the ops email address<br>
 * 3.  Log - Write to standard out<br>
 * <br>
 * Note that an alert pages and logs.  
 * 
 * @author u1001
 * @version 1.2
 */
public class Notification {
	
	// TODO The smsEmail address should be provided by a configuration file or -Dparm
	// TODO Or perhaps passed in
	private String smsEmail = "9137497635@txt.att.net";
	private String msgBody = "";
	private Throwable thrown = null;
	private Object pitcher = null;
	
	/**
	 * Create a new Notification using the class that threw the message and the message itself.
	 * 
	 * @param pitcher
	 * @param msgBody
	 */
	public Notification(Object pitcher, String msgBody) {
		if (pitcher == null) {
			pitcher = new String("Unknown.class");
		}
		
		if (msgBody == null) {
			msgBody = "";
		}
		
		this.pitcher = pitcher;
		this.msgBody = msgBody;
	}
	
	/**
	 * Create a new Notification using the class that threw the message and an Exception thrown by that class.
	 * 
	 * @param pitcher
	 * @param thrown
	 */
	public Notification(Object pitcher, Throwable thrown) {
		if (pitcher == null) {
			pitcher = new String("Unknown.class");
		}
		
		if (thrown == null) {
			thrown = new Exception("UnknownException");
		}
		
		if (thrown.getLocalizedMessage() == null) {
			thrown = new Exception("NullPointerException");
		}
		
		this.pitcher = pitcher;
		this.msgBody = thrown.toString();
		this.thrown = thrown;
	}

	/**
	 * Alert, the highest severity notification.<br>
	 * <br>
	 * An alert sends an SMS text message and logs the message.
	 */
	public void alert() {
		log();	
		sendEmail(smsEmail); 
	}
	
	/**
	 * Email the message to the specified email address.
	 * 
	 * @param toAddr The address to email the message.
	 */
	public void email(String toAddr) {
		if (toAddr == null) {
			toAddr = smsEmail;
		}
		
		sendEmail(toAddr);
	}
	
	/**
	 * Write the message to standard out.
	 */
	public void log() {
		// Translate the pitcher class into a fully qualified name
		String logBody = pitcher.getClass().getCanonicalName();
		
		if (thrown == null) {
			// If a throwable was not passed in, assume it was a string message and append it
			logBody += " | " + msgBody;
		} else {
			// Else a throwable was passed in, so we get information out of it
			StringWriter errors = new StringWriter();
			thrown.printStackTrace(new PrintWriter(errors));
			logBody += " | " + thrown.getLocalizedMessage() + "\n" + errors.toString();
		}
		
		// Write a formatted message to standard out
		System.out.println(new Date().toString() + " [ N O T I F I C A T I O N ]\n" + logBody);
	}
	
	/**
	 * Send an email to the specified email address.
	 * 
	 * @param toAddr The address to email the message to
	 */
	private void sendEmail(String toAddr)  {
		Session session = null;
		// The from information is set
		String fromAddr = "notification@cloudfordev.com";	
		String fromName = "CFD Notifications";
		String emailBody;
		
		if (thrown == null) {
			// If a throwable was not passed in, assume it was a string message and append it
			emailBody = msgBody;
		} else {
			// Else a throwable was passed in, so we get information out of it
			emailBody = thrown.getLocalizedMessage();
		}
		
		try {
			// Our user name is webmaster at CFD
			final String username = "webmaster@cloudfordev.com";
			
			// Setup the EnigmaMachine
			String keyLocation = "";
			if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
				keyLocation = "C:\\Users\\jboss\\keys\\enigma";
			} else {
				keyLocation = "/home/jboss/keys/enigma";
			}
			EnigmaMachine em = new EnigmaMachine(new File(keyLocation));
			
			// Decrypt the mail relay password
			final String password = em.decrypt("pBbhgWGyzNnKmhrYtkIASw==","TOu9l9xDbKcHRF5+TKZVdg==");
	
			// These are Gmail's mail relay properties
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			
			// Get a session to the mail relay
			session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
		} catch (Exception e) {
			Notification n = new Notification(this,e);
			n.log();
		}
	
		// Build a MIME message based on the session
        Message msg = new MimeMessage(session);
        try {
        	// Set the MIME fields
	        msg.setFrom(new InternetAddress(fromAddr, fromName));
	        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));
	        msg.setSubject(pitcher.getClass().getCanonicalName());
	        msg.setText(emailBody);
	        // Send the message
	        Transport.send(msg);
        } catch (AddressException ae) { 
        	// Ironically, Notification uses itself to log this exception
        	Notification n = new Notification(this,ae);
        	n.log();
        } catch (MessagingException me) {
        	// Ironically, Notification uses itself to log this exception
        	Notification n = new Notification(this,me);
        	n.log();
        } catch (UnsupportedEncodingException e) {
        	// Ironically, Notification uses itself to log this exception
        	Notification n = new Notification(this,e);
        	n.log();
		}
	}
}
