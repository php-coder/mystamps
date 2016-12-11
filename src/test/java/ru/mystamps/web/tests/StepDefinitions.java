package ru.mystamps.web.tests;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class StepDefinitions {

	WebDriver driver = new FirefoxDriver();

	@Given("^as a user$")
	public void as_a_user() throws Exception {
		driver.get("http://127.0.0.1:8080/account/auth");
		driver.findElement(By.id("login")).click();
		driver.findElement(By.id("login")).clear();
		driver.findElement(By.id("login")).sendKeys("coder");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("test");
		driver.findElement(By.cssSelector("input.btn.btn-primary")).click();
	}

	@When("^I add series to my collection$")
	public void iAddSeriesToMyCollection() throws Exception {
		driver.get("http://127.0.0.1:8080/");
		driver.findElement(By.linkText("1 item(s)")).click();
		if (driver.findElements(By.cssSelector("input.btn.btn-danger")).size() != 0) {
			System.out.println("Series is part of your collection");
		}else {
			driver.findElement(By.cssSelector("input.btn.btn-success")).click();
		}
	}
	
	@Then("^I am on the page with my collection$")
	public void i_am_on_the_page_with_my_collection() throws Exception {
		driver.get("http://127.0.0.1:8080/collection/coder");
	}
		
	@Then("^I see that this series have been added to the collection$")
	public void i_see_that_this_series_have_been_added_to_the_collection() throws Exception {
		driver.get("http://127.0.0.1:8080/series/1");
		Assert.assertTrue(driver.findElement(By.xpath(".//*[@class='btn btn-danger']//ancestor::form/p[1]")).getText().equals("Series is part of your collection"));
	}
}
