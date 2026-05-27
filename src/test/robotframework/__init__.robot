*** Settings ***
Documentation  Global Test Suites Configuration
Library        SeleniumLibrary
Resource       selenium.utils.robot
Suite Setup    Before All Test Suites

*** Keywords ***
Before All Test Suites
	Register Keyword To Run On Failure  Save Screenshot and Log Source
