package ru.mystamps.web.tests.cases;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;

import org.junit.Test;

import ru.mystamps.web.tests.page.NotFoundErrorPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenUserOpenNotExistingPage extends WhenUserAtAnyPage<NotFoundErrorPage> {
	
	public WhenUserOpenNotExistingPage() {
		super(NotFoundErrorPage.class);
		hasTitleWithoutStandardPrefix(tr("t_404_title"));
		hasResponseServerCode(HttpURLConnection.HTTP_NOT_FOUND);
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void shouldExistsErrorMessage() {
		assertEquals(tr("t_404_description", "\n"), page.getErrorMessage());
	}
	
	@Test
	public void shouldExistsErrorCode() {
		assertEquals("404", page.getErrorCode());
	}
	
	@Test
	public void incidentShouldBeLoggedToDatabase() {
		// TODO: check suspicious_events table (#99)
	}
	
}
