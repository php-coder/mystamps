package ru.mystamps.web.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public final class WebDriverFactory {
	
	private static WebDriver driver;
	
	public static WebDriver getDriver() {
		if (driver == null) {
			driver = new HtmlUnitDriver();
		}
		
		return driver;
	}
	
	private WebDriverFactory() {
	}
	
}
