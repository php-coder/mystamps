package ru.mystamps.web.tests.cases;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ru.mystamps.web.tests.page.LogoutAccountPage;

import static ru.mystamps.web.SiteMap.AUTHENTICATION_PAGE_URL;
import static ru.mystamps.web.SiteMap.INDEX_PAGE_URL;
import static ru.mystamps.web.SiteMap.REGISTRATION_PAGE_URL;
import static ru.mystamps.web.tests.TranslationUtils.tr;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/TestContext.xml")
public class WhenUserLogsOut extends WhenUserAtAnyPage<LogoutAccountPage> {
	
	@Value("#{test.valid_user_login}")
	private String VALID_USER_LOGIN;
	
	@Value("#{test.valid_user_password}")
	private String VALID_USER_PASSWORD;
	
	public WhenUserLogsOut() {
		super(LogoutAccountPage.class);
	}
	
	@Test
	public void shouldRedirectAndClearSession() {
		page.login(VALID_USER_LOGIN, VALID_USER_PASSWORD);
		page.open();
		
		assertEquals(
			"after logout we should be redirected to main page",
			INDEX_PAGE_URL,
			page.getCurrentUrl()
		);
		
		assertTrue(
			"should exists link to authentication page",
			page.linkHasLabelAndPointsTo(tr("t_enter"), AUTHENTICATION_PAGE_URL)
		);
		
		assertTrue(
			"should exists link to registration page",
			page.linkHasLabelAndPointsTo(tr("t_register"), REGISTRATION_PAGE_URL)
		);
	}
	
}
