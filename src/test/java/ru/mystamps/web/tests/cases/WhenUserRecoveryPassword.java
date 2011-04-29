package ru.mystamps.web.tests.cases;

import org.junit.Test;

import ru.mystamps.web.tests.page.RestorePasswordPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenUserRecoveryPassword extends WhenUserAtAnyPage<RestorePasswordPage> {
	
	public WhenUserRecoveryPassword() {
		super(RestorePasswordPage.class);
		hasTitle(tr("t_restore_password_title"));
		
		page.open();
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
}
