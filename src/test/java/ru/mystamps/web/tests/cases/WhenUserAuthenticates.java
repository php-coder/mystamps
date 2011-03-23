package ru.mystamps.web.tests.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

import org.junit.Test;

import ru.mystamps.web.tests.page.AuthAccountPage;

import static ru.mystamps.web.SiteMap.INDEX_PAGE_URL;
import static ru.mystamps.web.SiteMap.RESTORE_PASSWORD_PAGE_URL;
import static ru.mystamps.web.tests.TranslationUtils.stripHtmlTags;
import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_MIN_LENGTH;

public class WhenUserAuthenticates extends WhenUserAtAnyPageWithForm<AuthAccountPage> {
	
	public WhenUserAuthenticates() {
		super(AuthAccountPage.class);
		hasTitle(tr("t_auth_title"));
		hasHeader(tr("t_authentication_on_site"));
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void shouldExistsMessageWithLinkAboutPasswordRecovery() {
		assertThat(
			page.getFormHints(),
			hasItem(stripHtmlTags(tr("t_if_you_forget_password")))
		);
		
		assertTrue(
			"should exists link to password restoration page",
			page.linkHasLabelAndPointsTo("remind", RESTORE_PASSWORD_PAGE_URL)
		);
	}
	
	@Test
	public void loginShouldNotBeTooShort() {
		page.fillField("login", "a");
		page.submit();
		
		assertEquals(tr("value.too-short", LOGIN_MIN_LENGTH), page.getFieldError("login"));
	}
	
	@Test
	public void loginShouldNotBeTooLong() {
		page.fillField("login", "abcde12345fghkl6");
		page.submit();
		
		assertEquals(tr("value.too-long", LOGIN_MAX_LENGTH), page.getFieldError("login"));
	}
	
	@Test
	public void loginWithForbiddenCharactersShouldBeRejected() {
		page.fillField("login", "'t@$t'");
		page.submit();
		
		assertEquals(tr("login.invalid"), page.getFieldError("login"));
	}
	
	@Test
	public void passwordShouldNotBeTooShort() {
		page.fillField("password", "123");
		page.submit();
		
		assertEquals(tr("value.too-short", PASSWORD_MIN_LENGTH), page.getFieldError("password"));
	}
	
	@Test
	public void passwordWithForbiddenCharacterShouldBeRejected() {
		page.fillField("password", "'t@$t'");
		page.submit();
		
		assertEquals(tr("password.invalid"), page.getFieldError("password"));
	}
	
	@Test
	public void invalidCredentialsShouldBeRejected() {
		// TODO: inject from config (#95)
		final String INVALID_TEST_LOGIN    = "test";
		final String INVALID_TEST_PASSWORD = "test";
		
		page.fillField("login", INVALID_TEST_LOGIN);
		page.fillField("password", INVALID_TEST_PASSWORD);
		page.submit();
		
		assertEquals(tr("login.password.invalid"), page.getFormError());
	}
	
	@Test
	public void validCredentialsShouldAuthenticateUserOnSite() {
		// TODO: inject from config (#95)
		final String VALID_TEST_LOGIN    = "coder";
		final String VALID_TEST_PASSWORD = "test";
		final String VALID_TEST_NAME     = "Test Suite";
		
		page.fillField("login", VALID_TEST_LOGIN);
		page.fillField("password", VALID_TEST_PASSWORD);
		page.submit();
		
		assertEquals(
			"after login we should be redirected to main page",
			INDEX_PAGE_URL,
			page.getCurrentUrl()
		);
		
		assertEquals(
			"after login user name should be in user bar",
			VALID_TEST_NAME,
			page.getUserBarEntries().get(0)
		);
		
		assertEquals(
			"after login link for logout should be in user bar",
			tr("t_logout"),
			page.getUserBarEntries().get(1)
		);
		
		page.logout();
	}
	
}
