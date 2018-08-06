/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import ru.mystamps.web.service.exception.EmailSendingException;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// CheckStyle: ignore LineLength for next 4 lines
/**
 * Sending e-mails with Mailgun service (via SMTP).
 *
 * @see <a href="https://documentation.mailgun.com/en/latest/user_manual.html#sending-via-smtp">Sending via SMTP</a>
 */
@RequiredArgsConstructor
public class SmtpMailgunEmailSendingStrategy implements MailgunEmailSendingStrategy {

	private final JavaMailSender mailer;
	
	@Override
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	public void send(
		String toEmail,
		String fromEmail,
		String fromName,
		String subject,
		String text,
		String tag,
		boolean testMode) {
		
		try {
			// We're using MimeMessagePreparator only because of its capability of adding headers.
			// Otherwise we would use SimpleMailMessage class.
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				@Override
				public void prepare(MimeMessage mimeMessage) throws Exception {
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
					message.setValidateAddresses(true);
					message.setTo(toEmail);
					message.setFrom(new InternetAddress(fromEmail, fromName, "UTF-8"));
					message.setSubject(subject);
					message.setText(text);
					
					message.getMimeMessage().addHeader("X-Mailgun-Tag", tag);
					if (testMode) {
						message.getMimeMessage().addHeader("X-Mailgun-Drop-Message", "yes");
					}
				}
			};
			
			mailer.send(preparator);
			
		} catch (MailException ex) {
			throw new EmailSendingException("Can't send mail to e-mail " + toEmail, ex);
		}
	}
	
}
