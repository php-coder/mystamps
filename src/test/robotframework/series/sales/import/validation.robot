*** Settings ***
Documentation   Verify validation scenarios for importing a series sale
Library         SeleniumLibrary
Resource        ../../../auth.steps.robot
Resource        ../../../selenium.utils.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  sales  import-sales  validation  htmx

*** Test Cases ***
Import a series sale with empty required field
	Disable Client Validation      import-series-sale-form
	Submit Form                    id:import-series-sale-form
	Wait Until Element Is Visible  id:series-sale-url.errors
	Element Text Should Be         id:series-sale-url.errors  Value must not be empty

Import a series sale with invalid url
	Input Text                     id:series-sale-url  invalid-url
	Submit Form                    id:import-series-sale-form
	Wait Until Element Is Visible  id:series-sale-url.errors
	Element Text Should Be         id:series-sale-url.errors  Value must be a valid URL

Import a series sale with too long url
	${letter}=                     Set Variable  j
	Input Text                     id:series-sale-url  http://${letter * 767}
	Submit Form                    id:import-series-sale-form
	Wait Until Element Is Visible  id:series-sale-url.errors
	Element Text Should Be         id:series-sale-url.errors  Value is greater than allowable maximum of 767 characters

Import a series sale from an unsupported site
	Input Text                     id:series-sale-url  http://example.com
	Submit Form                    id:import-series-sale-form
	Wait Until Element Is Visible  id:series-sale-url.errors
	Element Text Should Be         id:series-sale-url.errors  Import from this site isn't supported

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To  ${SITE_URL}/series/1
