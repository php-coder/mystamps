package ru.mystamps.web.tests.cases;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ru.mystamps.web.tests.page.MaintenanceSitePage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenSiteOnMaintenance extends WhenUserAtAnyPage<MaintenanceSitePage> {
	
	public WhenSiteOnMaintenance() {
		super(MaintenanceSitePage.class);
		hasTitle(tr("t_maintenance_title"));
		
		// TODO:
		//hasResponseServerCode(HttpURLConnection.HTTP_UNAVAILABLE);
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void shouldExistsMessage() {
		assertEquals(tr("t_maintenance_on_site", "\n"), page.getErrorMessage());
	}
	
}
