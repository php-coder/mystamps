*** Settings ***
Documentation    Verify required elements appearance on the main page from a user
Library          SeleniumLibrary
Resource         ../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       misc  main-page

*** Test Cases ***
User should not see a link to a page for importing a series
	[Tags]                        import-series
	Page Should Not Contain Link  link=import a series

User should not see a link to a list of import requests
	[Tags]                        import-series
	Page Should Not Contain Link  link=show list of import requests

User should see a link for adding series
	[Tags]                        series
	Page Should Contain Link      link=add a stamp series

User should see a link for adding countries
	[Tags]                        country
	Page Should Contain Link      link=add a country

User should see a link for listing countries
	[Tags]                        country
	Page Should Contain Link      link=show list of countries

User should see a link for adding categories
	[Tags]                        category
	Page Should Contain Link      link=add a category

User should see a link for listing categories
	[Tags]                        category
	Page Should Contain Link      link=show list of categories

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test
	Go To                               ${SITE_URL}/

After Test Suite
	Close Browser
