package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

import static ru.mystamps.web.SiteMap.INDEX_PAGE_URL;

public class IndexSitePage extends AbstractPage {
	
	public IndexSitePage(final WebDriver driver) {
		super(driver, INDEX_PAGE_URL);
	}
	
}
