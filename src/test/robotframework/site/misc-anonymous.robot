*** Settings ***
Documentation    Verify required elements appearance on the main page from anonymous user
Library          SeleniumLibrary
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       misc  main-page

*** Test Cases ***
Anonymous should see a link for listing categories
	[Tags]                    category
	Page Should Contain Link  link=show list of categories

Anonymous should see a link for listing countries
	[Tags]                    country
	Page Should Contain Link  link=show list of countries

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Go To                               ${SITE_URL}/

After Test Suite
	Close Browser
