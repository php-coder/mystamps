package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

import static ru.mystamps.web.SiteMap.ADD_COUNTRY_PAGE_URL;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.required;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class AddCountryPage extends AbstractPageWithForm {
	
	public AddCountryPage(final WebDriver driver) {
		super(driver, ADD_COUNTRY_PAGE_URL);
		
		hasForm(
			with(
				required(inputField("country")).withLabel(tr("t_country"))
			)
			.and()
			.with(submitButton(tr("t_add")))
		);
	}
	
}
