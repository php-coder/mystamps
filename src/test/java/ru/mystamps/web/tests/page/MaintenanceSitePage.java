package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

import static ru.mystamps.web.SiteMap.MAINTENANCE_PAGE_URL;

public class MaintenanceSitePage extends AbstractPage {
	
	public MaintenanceSitePage(final WebDriver driver) {
		super(driver, MAINTENANCE_PAGE_URL);
	}
	
	public String getErrorMessage() {
		return getTextOfElementById("error-msg");
	}
	
}
