*** Settings ***
Documentation    Verify validation of adding a series to collection
Library          Selenium2Library
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       collection  validation

*** Test Cases ***
Add a series without required field
	[Documentation]         Verify validation of required fields
	Input Text              id=number-of-stamps  ${EMPTY}
	Submit Form             id=add-series-form
	Element Text Should Be  id=quantity.errors   Value must not be empty

Add a series with too few number of stamps
	[Documentation]         Verify validation of the minimal number of stamps
	Input Text              id=number-of-stamps  0
	Submit Form             id=add-series-form
	Element Text Should Be  id=quantity.errors   Value must be greater than or equal to 1

*** Keywords ***
Before Test Suite
	[Documentation]                     Login as user and open series info page
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test
	# We need a series with more than 1 stamp, so the number-of-stamps field won't be hidden
	Go To                               ${SITE_URL}/series/2

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser
