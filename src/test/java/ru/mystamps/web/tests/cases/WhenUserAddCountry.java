package ru.mystamps.web.tests.cases;

import static ru.mystamps.web.tests.TranslationUtils.tr;

import org.junit.Test;

import ru.mystamps.web.tests.page.AddCountryPage;

public class WhenUserAddCountry extends WhenUserAtAnyPageWithForm<AddCountryPage> {
	
	public WhenUserAddCountry() {
		super(AddCountryPage.class);
		hasTitle(tr("t_add_country"));
		hasHeader(tr("t_add_country_ucfirst"));
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
}
