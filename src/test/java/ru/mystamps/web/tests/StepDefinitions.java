package ru.mystamps.web.tests;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.BeforeClass;
import ru.mystamps.web.tests.cases.WhenAdminAddSeries;

/**
 * Created by Goblik on 30.10.2016.
 */
public class StepDefinitions  {

	
	WhenAdminAddSeries page = new WhenAdminAddSeries();

	@Given("^as a user$")
	public void as_a_user() throws Exception {

	}

	@When("^I add series to my collection$")
	public void i_add_series_to_my_collection() throws Exception {

	}

	@Then("^I am on the page with my collection$")
	public void i_am_on_the_page_with_my_collection() throws Exception {

	}

	@Then("^I see that this series have been added to the collection$")
	public void i_see_that_this_series_have_been_added_to_the_collection() throws Exception {

	}
}
