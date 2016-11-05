package ru.mystamps.web.tests;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

/**
 * Created by Goblik on 30.10.2016.
 */

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features",
	plugin = {"pretty",
		"html:target/cucumber-html-report"},
	tags = {"@in1"})

public class RunStampsTests {

}
 
