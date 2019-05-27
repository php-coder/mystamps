*** Settings ***
Documentation    Verify required elements appearance on the main page from anonymous user
Library          SeleniumLibrary
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       misc  main-page

*** Test Cases ***
Anonymous should see a link for listing categories
	[Tags]                    category
	Page Should Contain Link  link=show list of categories

Anonymous should not see a link for adding categories
	[Tags]                        category
	Page Should Not Contain Link  link=add a category

Anonymous should see a link for listing countries
	[Tags]                    country
	Page Should Contain Link  link=show list of countries

Anonymous should not see a link for adding countries
	[Tags]                        country
	Page Should Not Contain Link  link=add a country

Anonymous should not see a link for adding series
	[Tags]                        series
	Page Should Not Contain Link  link=add a stamp series

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

