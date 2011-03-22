package ru.mystamps.web.tests.cases;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ru.mystamps.web.tests.page.IndexSitePage;

import static ru.mystamps.web.SiteMap.ADD_COUNTRY_PAGE_URL;
import static ru.mystamps.web.SiteMap.ADD_STAMPS_PAGE_URL;
import static ru.mystamps.web.SiteMap.RESTORE_PASSWORD_PAGE_URL;
import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenUserAtIndexPage extends WhenUserAtAnyPage<IndexSitePage> {
	
	public WhenUserAtIndexPage() {
		super(IndexSitePage.class);
		hasTitle(tr("t_index_title"));
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void shouldExistsWelcomeText() {
		assertTrue(page.textPresent(tr("t_you_may")));
	}
	
	@Test
	public void shouldExistsLinkForPasswordRecovery() {
		assertTrue(
			"should exists link to password restoration page",
			page.linkHasLabelAndPointsTo(tr("t_recover_forget_password"), RESTORE_PASSWORD_PAGE_URL)
		);
	}
	
	@Test
	public void shouldExistsLinkForAddingStamps() {
		assertTrue(
			"should exists link to page for adding stamps",
			page.linkHasLabelAndPointsTo(tr("t_add_series"), ADD_STAMPS_PAGE_URL)
		);
	}
	
	@Test
	public void shouldExistsLinkForAddingCountries() {
		assertTrue(
			"should exists link to page for adding countries",
			page.linkHasLabelAndPointsTo(tr("t_add_country"), ADD_COUNTRY_PAGE_URL)
		);
	}
	
}
