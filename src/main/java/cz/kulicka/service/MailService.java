package cz.kulicka.service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.util.Date;

public interface MailService {
	boolean sendMail(String subject, Date errorTime);
}
