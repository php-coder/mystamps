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
		// go to authentication page
		// fill login - admin
		// fill password-test
		// press submit button
	}

	@When("^I add series to my collection$")
	public void i_add_series_to_my_collection() throws Exception {
		//assert open page- Add stamp series Page
		//select Category
		//select Country
		//select Quantity
		//select checkbox Perforated
		//Choose File -img
		//Press Specify date of release
		
		// select Day
		//select Month
		//selct Year
		
		//Press Add information from stamps catalogues
		
		//fill info
		//fill value
		
		//Press Add comment
		//fill Add comment
		
		//Press button Add
		
	}

	@Then("^I am on the page with my collection$")
	public void i_am_on_the_page_with_my_collection() throws Exception {
		
		//assert open page- Add to collection
		//Add info about selling/buying this series:
		//fill Date
		//select Seller 
		//fill URL

		//fill Price ammount
		//fill Price value
		//fill Alternative price ammount
		//fill Alternative price value
		//Select Buyer
	//or
		//Press button Add to collection
		
		

	}

	@Then("^I see that this series have been added to the collection$")
	public void i_see_that_this_series_have_been_added_to_the_collection() throws Exception {
		//assert open page-Site Admin's collection
		//assert Stamps in collection
	}
}
