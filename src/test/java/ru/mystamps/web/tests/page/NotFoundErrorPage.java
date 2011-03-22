package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

public class NotFoundErrorPage extends AbstractPage {
	
	public NotFoundErrorPage(final WebDriver driver) {
		super(driver, "/tests/page-does-not-exists.htm");
	}
	
	public String getErrorMessage() {
		return getTextOfElementById("error-msg");
	}
	
	public String getErrorCode() {
		return getTextOfElementById("error-code");
	}
	
}
