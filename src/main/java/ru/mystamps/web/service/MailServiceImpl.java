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

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DatePrinter;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import ru.mystamps.web.Url;
import ru.mystamps.web.feature.account.SendUsersActivationDto;
import ru.mystamps.web.feature.report.AdminDailyReport;
import ru.mystamps.web.feature.report.ReportService;
import ru.mystamps.web.service.exception.EmailSendingException;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MailServiceImpl implements MailService {
	private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);
	
	private final JavaMailSender mailer;
	private final MessageSource messageSource;
	private final String adminEmail;
	private final Locale adminLang;
	private final String robotEmail;
	private final boolean testMode;
	private final DatePrinter shortDatePrinter;
	private final ReportService reportService;
	
	public MailServiceImpl(
		ReportService reportService,
		JavaMailSender mailer,
		MessageSource messageSource,
		String adminEmail,
		Locale adminLang,
		String robotEmail,
		boolean testMode) {
		
		this.reportService = reportService;
		this.mailer = mailer;
		this.messageSource = messageSource;
		this.adminEmail = adminEmail;
		this.adminLang = adminLang;
		this.robotEmail = robotEmail;
		this.testMode = testMode;
		
		this.shortDatePrinter = FastDateFormat.getInstance("dd.MM.yyyy", adminLang);
	}
	
	@Override
	@Async
	public void sendActivationKeyToUser(SendUsersActivationDto activation) {
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
			"Email with activation code has been sent to {} (lang: {})",
			activation.getEmail(),
			activation.getLang()
		);
	}
	
	@Override
	@Async
	public void sendDailyStatisticsToAdmin(AdminDailyReport report) {
		sendMail(
			adminEmail,
			getSubjectOfDailyStatisticsMail(report),
			reportService.prepareDailyStatistics(report),
			"daily_statistics"
		);
		
		String date = shortDatePrinter.format(report.getStartDate());
		
		LOG.info(
			"E-mail with daily statistics for {} has been sent to {} (lang: {})",
			date,
			adminEmail,
			adminLang
		);
	}
	
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	private void sendMail(
		final String email,
		final String subject,
		final String text,
		final String tag) {
		
		try {
			// We're using MimeMessagePreparator only because of its capability of adding headers.
			// Otherwise we would use SimpleMailMessage class.
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				@Override
				@SuppressWarnings({
					"PMD.SignatureDeclareThrowsException", "PMD.AccessorMethodGeneration"
				})
				public void prepare(MimeMessage mimeMessage) throws Exception {
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
					message.setValidateAddresses(true);
					message.setTo(email);
					message.setFrom(new InternetAddress(robotEmail, "My Stamps", "UTF-8"));
					message.setSubject(subject);
					message.setText(text);
					
					// see: https://documentation.mailgun.com/en/latest/user_manual.html#sending-via-smtp
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
	
	private String getTextOfActivationMail(SendUsersActivationDto activation) {
		String template = messageSource.getMessage("activation.text", null, activation.getLocale());
		
		String activationUrl =
			String.format("%s?key=%s", Url.ACTIVATE_ACCOUNT_PAGE, activation.getActivationKey());
		
		Map<String, String> ctx = new HashMap<>();
		ctx.put("site_url", testMode ? Url.SITE : Url.PUBLIC_URL);
		ctx.put("activation_url_with_key", activationUrl);
		ctx.put("expire_after_days", String.valueOf(CronService.PURGE_AFTER_DAYS));
		
		StringSubstitutor substitutor = new StringSubstitutor(ctx);
		return substitutor.replace(template);
	}
	
	private String getSubjectOfActivationMail(SendUsersActivationDto activation) {
		return messageSource.getMessage("activation.subject", null, activation.getLocale());
	}
	
	private String getSubjectOfDailyStatisticsMail(AdminDailyReport report) {
		String template = messageSource.getMessage("daily_stat.subject", null, adminLang);
		
		String fromDate = shortDatePrinter.format(report.getStartDate());
		Map<String, String> ctx = new HashMap<>();
		ctx.put("date", fromDate);
		put(ctx, "total_changes", report.countTotalChanges());
		
		StringSubstitutor substitutor = new StringSubstitutor(ctx);
		return substitutor.replace(template);
	}
	
	private static void put(Map<String, String> ctx, String key, long value) {
		ctx.put(key, String.valueOf(value));
	}
	
}
