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
package ru.mystamps.web.tests.cases;

import org.springframework.beans.factory.annotation.Value;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.RegisterAccountPage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.fest.assertions.api.Assertions.assertThat;
import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.PageWithFormAssert.assertThat;

public class WhenAnonymousUserRegisterAccount
	extends WhenAnyUserAtAnyPage<RegisterAccountPage> {
	
	private static final Pattern ACTIVATION_LINK_REGEXP =
		Pattern.compile(".*/account/activate\\?key=[0-9a-z]{10}.*", Pattern.DOTALL);
	
	private static final int MAX_TIME_TO_WAIT_EMAIL_IN_SECONDS = 15;
	
	@Value("${spring.mail.host}")
	private String mailHost;
	
	@Value("${spring.mail.port}")
	private Integer mailPort;
	
	@Value("${activation.subject}")
	private String subjectOfActivationMail;
	
	private Wiser mailServer;
	
	public WhenAnonymousUserRegisterAccount() {
		super(RegisterAccountPage.class);
	}
	
	@BeforeClass
	public void startMailServer() {
		mailServer = new Wiser();
		mailServer.setHostname(mailHost);
		mailServer.setPort(mailPort);
		mailServer.start();
	}
	
	@AfterClass(alwaysRun = true)
	public void stopMailServer() {
		mailServer.stop();
	}
	
	@BeforeMethod
	public void openPage() {
		page.open();
	}
	
	@Test(groups = "misc")
	public void emailShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.registerUser(" test ");
		
		assertThat(page).field("email").hasValue("test");
	}
	
	@Test(groups = "logic", dependsOnGroups = { "misc" })
	public void successfulMessageShouldBeShownAfterRegistration() {
		page.registerUser("coder@rock.home");
		
		assertThat(page.getCurrentUrl()).isEqualTo(Url.ACTIVATE_ACCOUNT_PAGE);
		
		assertThat(page.textPresent(tr("t_activation_sent_message"))).isTrue();
	}
	
	@Test(groups = "logic", dependsOnMethods = "successfulMessageShouldBeShownAfterRegistration")
	public void emailWithActivationKeyShouldBeSentAfterRegistration()
		throws MessagingException, IOException, InterruptedException {
		
		List<WiserMessage> messages = Collections.emptyList();
		
		for (int cnt = 0; cnt < MAX_TIME_TO_WAIT_EMAIL_IN_SECONDS; cnt++) {
			messages = mailServer.getMessages();
			if (!messages.isEmpty()) {
				break;
			}
			
			TimeUnit.SECONDS.sleep(1);
		}
		
		assertThat(messages)
			.overridingErrorMessage("No messages has been sent via mail server")
			.isNotEmpty();
		
		boolean activationMailFound = false;
		for (WiserMessage message : messages) {
			String subject = message.getMimeMessage().getSubject();
			if (!subjectOfActivationMail.equals(subject)) {
				continue;
			}
			
			activationMailFound = true;
			
			Object body = message.getMimeMessage().getContent();
			assertThat(body).isInstanceOf(String.class);
			
			String text = (String)body;
			assertThat(text)
				.overridingErrorMessage("Message doesn't contain link with activation key")
				.matches(ACTIVATION_LINK_REGEXP);
			break;
		}
		
		assertThat(activationMailFound)
			.overridingErrorMessage(
				"Messages with subject '" + subjectOfActivationMail + "' not found"
			)
			.isTrue();
	}
	
}
