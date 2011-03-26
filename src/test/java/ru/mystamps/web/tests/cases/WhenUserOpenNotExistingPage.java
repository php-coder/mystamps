package ru.mystamps.web.tests.cases;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.mystamps.db.SuspiciousActivities;
import ru.mystamps.db.SuspiciousActivitiesTypes;
import ru.mystamps.web.entity.SuspiciousActivity;
import ru.mystamps.web.tests.page.NotFoundErrorPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:integrationTestsContext.xml"})
public class WhenUserOpenNotExistingPage extends WhenUserAtAnyPage<NotFoundErrorPage> {
	
	@Autowired
	private SuspiciousActivities act;
	
	@Autowired
	private SuspiciousActivitiesTypes types;
	
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
		final Long typeId = types.findByName("PageNotFound").getId();
		final SuspiciousActivity activity = act.getLastRecord();
		
		assertEquals(typeId, activity.getTypeId());
		
		// TODO: generate random page
		assertEquals(page.getCurrentUrl(), activity.getPage());
	}
	
}
