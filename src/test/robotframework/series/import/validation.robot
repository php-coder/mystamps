*** Settings ***
Documentation    Verify import series validation scenarios
Library          Selenium2Library
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  import-series  validation

*** Test Cases ***
Submit request with blank required field
	[Documentation]         Verify validation of required field
	Submit Form             id=import-series-form
	Element Text Should Be  id=url.errors  Value must not be empty

Submit request with too long url
	[Documentation]         Verify validation of too long url
	${letter}=              Set Variable  j
	Input Text              id=url  http://${letter * 767}
	Submit Form             id=import-series-form
	Element Text Should Be  id=url.errors  Value is greater than allowable maximum of 767 characters

Submit request with invalid url
	[Documentation]         Verify validation of invalid url
	Input Text              id=url  invalid-url
	Submit Form             id=import-series-form
	Element Text Should Be  id=url.errors  Value must be a valid URL

*** Keywords ***
Before Test Suite
	[Documentation]                     Login as admin and open a page for requesting a series import
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/import/request

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser
