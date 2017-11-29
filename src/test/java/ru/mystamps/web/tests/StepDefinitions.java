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

package ru.mystamps.web.tests;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import ru.mystamps.web.Url;

import static org.junit.Assert.assertEquals;

/**
 * @author Anna Osipova
 */
public class StepDefinitions  {
	private final WebDriver driver = new HtmlUnitDriver();

	@Given("^as a user$")
	public void loginAsUser() {
		driver.get(Url.SITE + Url.AUTHENTICATION_PAGE);
		driver.findElement(By.id("login")).click();
		driver.findElement(By.id("login")).clear();
		driver.findElement(By.id("login")).sendKeys("coder");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("test");
		driver.findElement(By.id("auth-submit")).click();
	}

	@When("^I add series to my collection$")
	public void addSeriesToCollection() {
		driver.get("http://127.0.0.1:8080/series/1");
		if (driver.findElements(By.id("series-danger")).size() != 0) {
			driver.findElement(By.id("series-danger")).click();
			driver.get(Url.SITE);
			driver.findElement(By.linkText("1 item(s)")).click();
		}
		driver.findElement(By.id("series-success")).click();
	}
	
	@Then("^I am on the page with my collection$")
	public void onPageMyCollection() {
		String actualTitle = driver.getTitle();
		assertEquals(actualTitle, "My stamps: Test Suite's collection");
	}

	@And("^I see that this series has been added to the collection$")
	public void seriesHasBeenAddedToCollection() {
		driver.get("http://127.0.0.1:8080/series/1");
		String actual = driver.findElement(
			By.xpath("//*[@id='series-danger']//ancestor::form/p[1]")
		).getText();
		String expected = "Series is part of your collection";
		assertEquals(expected, actual);
	}
}
