*** Settings ***
Documentation    Verify validation scenarios for adding series sales
Library          Selenium2Library
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  sales  validation

*** Test Cases ***
Create series sales with too long url
	[Documentation]         Verify validation of too long url
	${letter}=              Set Variable  j
	Input Text              id=url  http://${letter * 255}
	Submit Form             id=add-series-sales-form
	Element Text Should Be  id=url.errors  Value is greater than allowable maximum of 255 characters

*** Keywords ***
Before Test Suite
	[Documentation]                     Login as admin and open a page with series
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/1

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser
