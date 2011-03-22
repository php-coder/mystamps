package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

import static ru.mystamps.web.SiteMap.ACTIVATE_ACCOUNT_PAGE_URL;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.passwordField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class ActivateAccountPage extends AbstractPageWithForm {
	
	public ActivateAccountPage(final WebDriver driver) {
		super(driver, ACTIVATE_ACCOUNT_PAGE_URL);
		
		hasForm(
			with(
				required(inputField("login")).withLabel(tr("t_login")).and().invalidValue("x"),
				inputField("name").withLabel(tr("t_name")).and().invalidValue("x"),
				required(passwordField("password")).withLabel(tr("t_password")).and().invalidValue("x"),
				required(passwordField("passwordConfirm")).withLabel(tr("t_password_again")).and().invalidValue("x"),
				required(inputField("activationKey")).withLabel(tr("t_activation_key")).and().invalidValue("x")
			)
			.and()
			.with(submitButton(tr("t_activate")))
		);
	}
	
}
