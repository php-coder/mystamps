*** Settings ***
Documentation    Verify validation scenarios for adding series sales
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  sales  validation

*** Test Cases ***
Create series sales with too long url
	[Tags]                      todo
	${letter}=                  Set Variable  j
	Input Text                  id=url  http://${letter * 255}
	Submit Form                 id=add-series-sales-form
	Wait Until Element Text Is  url.errors  Value is greater than allowable maximum of 255 characters

Create series sales with invalid url
	[Tags]                      todo
	Input Text                  id=url  invalid-url
	Submit Form                 id=add-series-sales-form
	Wait Until Element Text Is  url.errors  Value must be a valid URL

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/1

After Test Suite
	Log Out
	Close Browser
