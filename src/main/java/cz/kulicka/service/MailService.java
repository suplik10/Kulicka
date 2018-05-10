package cz.kulicka.service;

import java.util.Date;

public interface MailService {
	boolean sendMail(String subject, Date errorTime);
}
