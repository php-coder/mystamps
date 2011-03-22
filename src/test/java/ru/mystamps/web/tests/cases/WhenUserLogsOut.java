package ru.mystamps.web.tests.cases;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ru.mystamps.web.tests.page.LogoutAccountPage;

import static ru.mystamps.web.SiteMap.AUTHENTICATION_PAGE_URL;
import static ru.mystamps.web.SiteMap.INDEX_PAGE_URL;
import static ru.mystamps.web.SiteMap.REGISTRATION_PAGE_URL;
import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenUserLogsOut extends WhenUserAtAnyPage<LogoutAccountPage> {
	
	public WhenUserLogsOut() {
		super(LogoutAccountPage.class);
	}
	
	@Test
	public void shouldRedirectAndClearSession() {
		page.login();
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
