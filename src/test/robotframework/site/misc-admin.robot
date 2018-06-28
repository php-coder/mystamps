*** Settings ***
Documentation    Verify required elements appearance on the main page from an admin
Library          Selenium2Library
Resource         ../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       misc  main-page

*** Test Cases ***
Admin should see a link to a page for importing a series
	[Tags]                    import-series
	Page Should Contain Link  link=import a series

Admin should see a link to a list of import requests
	[Tags]                    import-series
	Page Should Contain Link  link=show list of import requests

Admin should see a link for adding series
	[Tags]                    series
	Page Should Contain Link  link=add stamp series

Admin should see a link for adding countries
	[Tags]                    country
	Page Should Contain Link  link=add country

Admin should see a link for listing countries
	[Tags]                    country
	Page Should Contain Link  link=show list of countries

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/

After Test Suite
	Log Out
	Close Browser
