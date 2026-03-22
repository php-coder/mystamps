*** Settings ***
Documentation  Global Test Suites Configuration
Library        SeleniumLibrary
Suite Setup    Before All Test Suites

*** Keywords ***
Before All Test Suites
	Register Keyword To Run On Failure  Log Source
