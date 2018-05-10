package cz.kulicka.service.impl;

import cz.kulicka.PropertyPlaceholder;
import cz.kulicka.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {

	@Autowired
	PropertyPlaceholder propertyPlaceholder;

	@Override
	public boolean sendMail(String subject, Date errorTime) {

		Session session = getSession();

		if (!propertyPlaceholder.isNotificationOnErrorEnabled()) {
			return false;
		}

		for (String mail : propertyPlaceholder.getNotificationEmails()) {
			try {

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress("kulicka.bot@gmail.com"));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(mail));
				message.setSubject(subject);
				message.setText(buildMessage(subject, errorTime));

				Transport.send(message);

				System.out.println("Done");

			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		}


		return true;
	}

	private Session getSession() {

		final String username = "kulicka.bot@gmail.com";
		final String password = "Heslo0001!";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		return Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

	}

	private String buildMessage(String subject, Date errorTime){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ENV: " + propertyPlaceholder.getAppName());
		stringBuilder.append("\n");
		stringBuilder.append("TIME: " + errorTime);
		stringBuilder.append("\n");
		stringBuilder.append("ERROR MESSAGE: " + subject);
		stringBuilder.append("\n");

		return stringBuilder.toString();
	}
}
