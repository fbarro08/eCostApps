package com.mall.util;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class EmailUtility {
	 private final static Logger logger = Logger.getLogger(EmailUtility.class);
	 public static final String SMTP_HOST = "mail.smtp.host";
	 public static final String SMTP_PORT = "mail.smtp.port";
	 public static final String ADMIN_BCC = "mail.bcc";
	 
	 public static final String MAIL_FROM = "mail.from";
	 public static final String SMPTP_SUBJECT = "mail.bcc";
	 
	 public static final String CONTENTTYPE_HTML = "text/html";
	 public static final String CONTENTTYPE_TEXT = "text/plain";
	 	 
	 private static String COMMA_DELIMETER = ";";
	 private Properties mailProps = new Properties();
	 
	 private final ResourceUtil resourceUtil = ResourceUtil.getInstance();
	 
	 private static EmailUtility instance;
	 
	 public EmailUtility() {
    	try {
    		mailProps.load(EmailUtility.class.getResourceAsStream("/email.properties"));    		
		} catch (IOException e) {
			logger.error(e);
		}
	 }
	 
    public static EmailUtility getInstance() {
        if (instance == null) {
            instance = new EmailUtility();
        }

        return instance;
    }
	    
	 public boolean sendHTMLMail(String recipients, String subject, String content) {
			return send(recipients, resourceUtil.getKeyValue(MAIL_FROM, mailProps), subject, content, CONTENTTYPE_HTML);
	 }
	 
	 public boolean sendTextMail(String recipients, String subject, String content) {
			return send(recipients, resourceUtil.getKeyValue(MAIL_FROM, mailProps), subject, content, CONTENTTYPE_TEXT);
	 }
	 
	 private boolean send(String recipients, String sender, String subject, String body, String contentType) {  
		boolean isSentMail = false; 
        try {        	        	
        	String[] recipientArray = parseRecipient(recipients);
        	
        	if (recipients == null) {
        		logger.error("There are no recipients for the message.");
        		return isSentMail;
        	}
        	
            Session mailSession = Session.getDefaultInstance(mailProps);
            Message simpleMessage = new MimeMessage(mailSession);
            
			simpleMessage.setFrom(new InternetAddress(sender));
            simpleMessage.setRecipients(Message.RecipientType.TO, extractValidRecipient(recipientArray));
            simpleMessage.setSubject(subject);
            simpleMessage.setContent(body, contentType);
            Transport.send(simpleMessage);

            logger.info("Email Successfully Sent to " + recipients + ", Subject=" + subject);
            isSentMail = true;
       
		} catch (AddressException aex) {
			logger.error(aex);
		} catch (MessagingException mex) {
			logger.error(mex);
		} catch (Exception ex) {
			logger.error(ex);
		}
		
		return isSentMail;
	 }
	 
	 private String[] parseRecipient(String unParseRecipients) {
		if (unParseRecipients != null && !unParseRecipients.trim().isEmpty()) {
            return unParseRecipients.split(COMMA_DELIMETER);
        }
        return null;
    }

    private Address[] extractValidRecipient(String[] recipients) throws AddressException {
        Address[] recipentsAddress = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            recipentsAddress[i] = new InternetAddress(recipients[i]);
        }
        return recipentsAddress;
    }
	
}
