package ru.mystamps.web.tests.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.HttpURLConnection;

import org.apache.commons.lang.RandomStringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.entity.SuspiciousActivity;
import ru.mystamps.web.tests.page.NotFoundErrorPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/DispatcherServletContext.xml"})
@TransactionConfiguration(defaultRollback = false)
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
	@Transactional(readOnly = true)
	public void incidentShouldBeLoggedToDatabase() {
		SuspiciousActivity activity =
			suspiciousActivities.findByPage(currentUrl);
		
		System.out.println("current = " + currentUrl);
		assertNotNull(activity);
		System.out.println("page = " + activity.getPage());
		assertEquals("PageNotFound", activity.getType().getName());
	}
	
}
