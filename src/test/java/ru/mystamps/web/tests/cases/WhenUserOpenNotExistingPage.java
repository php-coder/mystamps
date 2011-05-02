package ru.mystamps.web.tests.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.HttpURLConnection;

import org.apache.commons.lang.RandomStringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.entity.SuspiciousActivity;
import ru.mystamps.web.tests.page.NotFoundErrorPage;

import static ru.mystamps.web.SiteMap.SITE_URL;
import static ru.mystamps.web.tests.TranslationUtils.tr;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:integrationTestsContext.xml"})
public class WhenUserOpenNotExistingPage extends WhenUserAtAnyPage<NotFoundErrorPage> {
	
	private final String currentUrl;
	
	@Autowired
	private SuspiciousActivityDao suspiciousActivities;
	
	private String generateRandomUrl() {
		return String.format(
			"/tests/page-does-not-exists-%s.htm",
			RandomStringUtils.randomNumeric(5)
		);
	}
	
	public WhenUserOpenNotExistingPage() {
		super(NotFoundErrorPage.class);
		hasTitleWithoutStandardPrefix(tr("t_404_title"));
		hasResponseServerCode(HttpURLConnection.HTTP_NOT_FOUND);
		
		currentUrl = generateRandomUrl();
		page.open(currentUrl);
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
		SuspiciousActivity activity =
			suspiciousActivities.findByPage(SITE_URL + currentUrl);
		
		assertNotNull(activity);
		assertEquals("PageNotFound", activity.getType().getName());
	}
	
}
