package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

import static ru.mystamps.web.SiteMap.LOGOUT_PAGE_URL;

public class LogoutAccountPage extends AbstractPage {
	
	public LogoutAccountPage(final WebDriver driver) {
		super(driver, LOGOUT_PAGE_URL);
	}
	
}
