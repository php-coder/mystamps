package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;
import ru.mystamps.web.Url;

/**
 * Created by shestakov.m on 23.08.2016.
 */
public class InfoCollectionPage extends AbstractPage {
	
	public InfoCollectionPage(WebDriver driver) {
		super(driver, Url.INFO_COLLECTION_PAGE);
	}
	
}
