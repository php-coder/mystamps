/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.StrSubstitutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.entity.UsersActivation;
import ru.mystamps.web.service.exception.EmailSendingException;

@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
	private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);
	
	private final JavaMailSender mailer;
	private final MessageSource messageSource;
	private final String robotEmail;
	private final boolean testMode;
	
	@Override
	public void sendActivationKeyToUser(UsersActivation activation) {
		Validate.isTrue(activation != null, "Activation must be non null");
		Validate.isTrue(activation.getEmail() != null, "E-mail must be non null");
		Validate.isTrue(activation.getLang() != null, "Language must be non null");
		Validate.isTrue(activation.getActivationKey() != null, "Activation key must be non null");
		
		sendMail(
			activation.getEmail(),
			getSubjectOfActivationMail(activation),
			getTextOfActivationMail(activation),
			"activation_key"
		);
		
		LOG.info(
			"E-mail with activation code has been sent to {} (lang: {})",
			activation.getEmail(),
			activation.getLang()
		);
	}
	
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	private void sendMail(
		final String email,
		final String subject,
		final String text,
		final String tag) {
		
		try {
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				@Override
				@SuppressWarnings("PMD.SignatureDeclareThrowsException")
				public void prepare(MimeMessage mimeMessage) throws Exception {
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
					message.setTo(email);
					message.setFrom(robotEmail);
					message.setSubject(subject);
					message.setText(text);
					
					// see: http://documentation.mailgun.com/user_manual.html#sending-via-smtp
					message.getMimeMessage().addHeader("X-Mailgun-Tag", tag);
					if (testMode) {
						message.getMimeMessage().addHeader("X-Mailgun-Drop-Message", "yes");
					}
				}
			};
			
			mailer.send(preparator);
			
		} catch (MailException ex) {
			throw new EmailSendingException("Can't send mail to e-mail " + email, ex);
		}
	}
	
	private String getTextOfActivationMail(UsersActivation activation) {
		String template = messageSource.getMessage("activation.text", null, activation.getLocale());
		
		String activationUrl =
			Url.ACTIVATE_ACCOUNT_PAGE_WITH_KEY.replace("{key}", activation.getActivationKey());
		
		Map<String, String> ctx = new HashMap<>();
		ctx.put("site_url", testMode ? Url.SITE : Url.PUBLIC_URL); // NOCHECKSTYLE: AvoidInlineConditionalsCheck
		ctx.put("activation_url_with_key", activationUrl);
		ctx.put("expire_after_days", String.valueOf(CronService.PURGE_AFTER_DAYS));
		
		StrSubstitutor substitutor = new StrSubstitutor(ctx);
		return substitutor.replace(template);
	}
	
	private String getSubjectOfActivationMail(UsersActivation activation) {
		return messageSource.getMessage("activation.subject", null, activation.getLocale());
	}
	
}
