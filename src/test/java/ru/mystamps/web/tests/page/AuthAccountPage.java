package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

import static ru.mystamps.web.SiteMap.AUTHENTICATION_PAGE_URL;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.passwordField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class AuthAccountPage extends AbstractPageWithForm {
	
	public AuthAccountPage(final WebDriver driver) {
		super(driver, AUTHENTICATION_PAGE_URL);
		
		hasForm(
			with(
				required(inputField("login"))
					.withLabel(tr("t_login"))
					.and()
					.invalidValue("x"),
				
				required(passwordField("password"))
					.withLabel(tr("t_password"))
					.and()
					.invalidValue("x")
			)
			.and()
			.with(submitButton(tr("t_enter")))
		);
	}
	
	public boolean authenticationFormExists() {
		// TODO: probably better to check for form tag presence?
		return elementWithIdExists("authAccountForm");
	}
	
}
