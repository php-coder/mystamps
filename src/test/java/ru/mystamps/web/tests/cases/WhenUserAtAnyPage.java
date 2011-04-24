package ru.mystamps.web.tests.cases;

import java.net.HttpURLConnection;

import org.openqa.selenium.support.PageFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ru.mystamps.web.tests.WebDriverFactory;
import ru.mystamps.web.tests.page.AbstractPage;

import static ru.mystamps.web.SiteMap.AUTHENTICATION_PAGE_URL;
import static ru.mystamps.web.SiteMap.REGISTRATION_PAGE_URL;
import static ru.mystamps.web.tests.TranslationUtils.tr;

public abstract class WhenUserAtAnyPage<T extends AbstractPage> {
	
	protected final T page;
	
	/**
	 * Prefix of page's title  which will be prepend by hasTitle().
	 */
	private static final String TITLE_PREFIX = "MyStamps: ";
	
	/**
	 * @see hasResponseServerCode()
	 */
	private int serverCode = HttpURLConnection.HTTP_OK;
	
	/**
	 * @see hasTitle()
	 * @see hasTitleWithoutStandardPrefix()
	 */
	private String title;
	
	/**
	 * @see hasHeader()
	 */
	private String header;
	
	public WhenUserAtAnyPage(final Class<T> pageClass) {
		page = PageFactory.initElements(WebDriverFactory.getDriver(), pageClass);
		page.open();
	}
	
	protected void hasResponseServerCode(final int serverCode) {
		this.serverCode = serverCode;
	}
	
	protected void hasTitle(final String title) {
		this.title = TITLE_PREFIX + title;
	}
	
	protected void hasTitleWithoutStandardPrefix(final String title) {
		this.title = title;
	}
	
	protected void hasHeader(final String header) {
		this.header = header;
	}
	
	protected void checkStandardStructure() {
		checkServerResponseCode();
		shouldHaveTitle();
		shouldHaveLogo();
		shouldHaveUserBar();
		shouldHaveContentArea();
		mayHaveHeader();
		shouldHaveFooter();
	}
	
	private void checkServerResponseCode() {
		assertEquals("Server response code", serverCode, page.getServerResponseCode());
	}
	
	private void shouldHaveTitle() {
		if (title == null) {
			throw new IllegalStateException(
				"Page title was not set! Did you call hasTitle() or hasTitleWithoutStandardPrefix() before?"
			);
		}
		
		assertEquals("title should be '" + title + "'", title, page.getTitle());
	}
	
	private void shouldHaveLogo() {
		assertEquals(
			"text at logo should be '" + tr("t_my_stamps") + "'",
			tr("t_my_stamps"),
			page.getTextAtLogo()
		);
	}
	
	private void shouldHaveUserBar() {
		assertTrue("user bar should exists", page.userBarExists());
		
		assertTrue(
			"should exists link to authentication page",
			page.linkHasLabelAndPointsTo(tr("t_enter"), AUTHENTICATION_PAGE_URL)
		);
		
		assertTrue(
			"should exists link to registration page",
			page.linkHasLabelAndPointsTo(tr("t_register"), REGISTRATION_PAGE_URL)
		);
	}
	
	private void shouldHaveContentArea() {
		assertTrue("should exists content area", page.contentAreaExists());
	}
	
	private void mayHaveHeader() {
		if (header != null) {
			assertEquals("header should exists", header, page.getHeader());
		}
	}
	
	private void shouldHaveFooter() {
		assertTrue("footer should exists", page.footerExists());
		assertTrue(
			"should exists link with author's email",
			page.linkHasLabelWithTitleAndPointsTo(
				tr("t_site_author_name"),
				tr("t_write_email"),
				"mailto:" + tr("t_site_author_email")
			)
		);
	}
	
}
