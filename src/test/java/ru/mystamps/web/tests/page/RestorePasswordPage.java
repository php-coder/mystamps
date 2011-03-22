package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

import static ru.mystamps.web.SiteMap.RESTORE_PASSWORD_PAGE_URL;

public class RestorePasswordPage extends AbstractPage {
	
	public RestorePasswordPage(final WebDriver driver) {
		super(driver, RESTORE_PASSWORD_PAGE_URL);
	}
	
}
