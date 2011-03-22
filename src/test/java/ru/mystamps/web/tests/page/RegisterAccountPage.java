package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

import static ru.mystamps.web.SiteMap.REGISTRATION_PAGE_URL;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class RegisterAccountPage extends AbstractPageWithForm {
	
	public RegisterAccountPage(final WebDriver driver) {
		super(driver, REGISTRATION_PAGE_URL);
		
		hasForm(
			with(
				required(inputField("email")).withLabel(tr("t_email")).and().invalidValue("xxx")
			)
			.and()
			.with(submitButton(tr("t_register")))
		);
	}
	
	public boolean registrationFormExists() {
		// TODO: probably better to check for form tag presence?
		return elementWithIdExists("registerAccountForm");
	}
	
}
