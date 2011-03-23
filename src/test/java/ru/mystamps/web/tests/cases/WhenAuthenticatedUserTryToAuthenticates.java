package ru.mystamps.web.tests.cases;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.mystamps.web.tests.TranslationUtils.tr;

import org.junit.Test;

import ru.mystamps.web.tests.page.AuthAccountPage;

/**
 * TODO: use @BeforeClass/@AfterClass annotations from TestNG (#92)
 * (For now we can't use them because JUnit required than them should be static.)
 *
 * TODO: split test to parts
 * (For now we can't do this because order of tests not defined and logout
 * may happens before some of test.)
 **/
public class WhenAuthenticatedUserTryToAuthenticates extends WhenUserAtAnyPageWithForm<AuthAccountPage> {
	
	public WhenAuthenticatedUserTryToAuthenticates() {
		super(AuthAccountPage.class);
		hasTitle(tr("t_auth_title"));
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void messageShouldBeShownAndFormWithLegendAreAbsent() {
		page.login();
		
		assertTrue(page.textPresent(tr("t_already_authenticated")));
		assertFalse(page.authenticationFormExists());
		assertTrue(page.getFormHints().isEmpty());
		
		page.logout();
	}
	
}
