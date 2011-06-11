package ru.mystamps.web.tests.cases;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.mystamps.web.tests.TranslationUtils.tr;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.mystamps.web.tests.page.RegisterAccountPage;

/*
 * TODO: use @BeforeClass/@AfterClass annotations from TestNG (#92)
 * (For now we can't use them because JUnit required than them should be static.)
 *
 * TODO: split test to parts
 * (For now we can't do this because order of tests not defined and logout
 * may happens before some of test.)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/TestContext.xml")
public class WhenAuthenticatedUserRegisterAccount
	extends WhenUserAtAnyPageWithForm<RegisterAccountPage> {
	
	@Value("#{test.valid_user_login}")
	private String VALID_USER_LOGIN;
	
	@Value("#{test.valid_user_password}")
	private String VALID_USER_PASSWORD;
	
	public WhenAuthenticatedUserRegisterAccount() {
		super(RegisterAccountPage.class);
		hasTitle(tr("t_registration_title"));
		
		page.open();
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void messageShouldBeShownAndFormWithLegendAreAbsent() {
		page.login(VALID_USER_LOGIN, VALID_USER_PASSWORD);
		
		assertTrue(page.textPresent(tr("t_already_registered")));
		assertFalse(page.registrationFormExists());
		assertTrue(page.getFormHints().isEmpty());
		
		page.logout();
	}
	
}
