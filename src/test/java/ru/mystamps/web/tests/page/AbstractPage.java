/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.tests.page;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import org.apache.commons.lang3.Validate;

import ru.mystamps.web.Url;
import ru.mystamps.web.tests.WebElementUtils;

import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@RequiredArgsConstructor(access = PROTECTED)
public abstract class AbstractPage {
	
	// CheckStyle: ignore LineLength for next 2 lines
	private static final String LOGOUT_BUTTON_LOCATOR = "//form[@id=\"logout-form\"]/input[@type=\"submit\"]";
	private static final String USER_BAR_ENTRIES_LOCATOR = "//*[@id=\"user_bar\"]//li/a | //*[@id=\"user_bar\"]//li//input[not(@type=\"hidden\")]";
	private static final String A_HREF_LOCATOR = "//a[@href=\"%s\"]";
	
	protected final WebDriver driver;
	private final String pageUrl;
	
	@FindBy(tagName = "body")
	private WebElement body;
	
	public void open() {
		driver.get(getFullUrl());
	}
	
	public void open(String pageUrl) {
		driver.get(Url.SITE + pageUrl);
	}
	
	private String getFullUrl() {
		return Url.SITE + pageUrl;
	}
	
	/**
	 * Get short URL (without host name) of page.
	 **/
	public String getUrl() {
		return pageUrl;
	}
	
	/**
	 * Get short URL (without host name) of current page.
	 **/
	public String getCurrentUrl() {
		return driver.getCurrentUrl().replace(Url.SITE, "");
	}
	
	/**
	 * Verifies that current url differs from page's url.
	 **/
	protected boolean pageUrlChanged() {
		return !driver.getCurrentUrl().equals(getFullUrl());
	}
	
	public String getTextAtLogo() {
		return getTextOfElementById("logo");
	}
	
	public List<String> getUserBarEntries() {
		List<WebElement> entries = getElementsByXPath(USER_BAR_ENTRIES_LOCATOR);
		return WebElementUtils.convertToListWithText(entries);
	}
	
	public boolean contentAreaExists() {
		return elementWithIdExists("content");
	}
	
	public String getHeader() {
		return getTextOfElementByTagName("h3");
	}
	
	public boolean footerExists() {
		return elementWithTagNameExists("footer");
	}
	
	public boolean linkWithLabelExists(String label) {
		return getLinkByText(label) != null;
	}
	
	public boolean linkWithLabelAndTitleExists(String label, String title) {
		WebElement link = getLinkByText(label);
		if (link == null) {
			return false;
		}
		
		return link.getAttribute("title").equals(title);
	}
	
	public boolean existsLinkTo(String relativeUrl) {
		Validate.isTrue(!"".equals(relativeUrl));
		return elementWithXPathExists(String.format(A_HREF_LOCATOR, relativeUrl));
	}
	
	public boolean textPresent(String text) {
		return body.getText().contains(text);
	}
	
	
	//
	// Helpers for find elements
	//
	
	protected WebElement getElementById(String elementId) {
		return driver.findElement(By.id(elementId));
	}
	
	protected WebElement getElementByName(String name) {
		return driver.findElement(By.name(name));
	}
	
	protected WebElement getElementByXPath(String xpath) {
		return driver.findElement(By.xpath(xpath));
	}
	
	protected WebElement getElementByTagName(String tagName) {
		return driver.findElement(By.tagName(tagName));
	}
	
	protected List<WebElement> getElementsByClassName(String className) {
		return driver.findElements(By.className(className));
	}
	
	protected List<WebElement> getElementsByXPath(String xpath) {
		return driver.findElements(By.xpath(xpath));
	}
	
	//
	// Helpers for getting element's value
	//
	
	protected String getTextOfElementById(String elementId) {
		return getElementById(elementId).getText();
	}
	
	protected String getTextOfElementByXPath(String xpath) {
		return getElementByXPath(xpath).getText();
	}
	
	protected String getTextOfElementByTagName(String tagName) {
		return getElementByTagName(tagName).getText();
	}
	
	
	//
	// Other helpers
	//
	
	protected boolean elementWithIdExists(String elementId) {
		return getElement(By.id(elementId)) != null;
	}
	
	protected boolean elementWithXPathExists(String xpath) {
		return getElement(By.xpath(xpath)) != null;
	}
	
	protected boolean elementWithTagNameExists(String tagName) {
		return getElement(By.tagName(tagName)) != null;
	}
	
	protected WebElement getLinkByText(String linkText) {
		return getElement(By.linkText(linkText));
	}
	
	private WebElement getElement(By byMethod) {
		try {
			return driver.findElement(byMethod);
		} catch (NoSuchElementException ex) {
			return null;
		}
	}
	
	public void login(String login, String password) {
		Validate.isTrue(!"".equals(login), "login must be not null and not empty");
		Validate.isTrue(!"".equals(password), "password must be not null and not empty");
		
		// TODO: check than we already authenticated and do nothing
		AuthAccountPage authPage = new AuthAccountPage(driver);
		authPage.open();
		authPage.authorizeUser(login, password);
		
		// return to current page
		open();
		// TODO: test for presence link with text "Sign out" to ensure than all right?
	}
	
	public void logout() {
		// TODO: check than we not authenticated and do nothing
		
		WebElement logoutButton = getElementByXPath(LOGOUT_BUTTON_LOCATOR);
		logoutButton.submit();
		
		// return to current page
		open();
		// TODO: test for presence link with text "Sign in" to ensure than all right?
	}
	
}
