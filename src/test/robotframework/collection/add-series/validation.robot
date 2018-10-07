*** Settings ***
Documentation    Verify validation of adding a series to collection
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       collection  validation

*** Test Cases ***
Add a series without required field
	Remove Element Attribute  number-of-stamps  required
	Input Text                id=number-of-stamps  ${EMPTY}
	Submit Form               id=add-series-form
	Element Text Should Be    id=number-of-stamps.errors  Value must not be empty

Add a series with too few number of stamps
	Input Text              id=number-of-stamps  0
	Submit Form             id=add-series-form
	Element Text Should Be  id=number-of-stamps.errors  Value must be greater than or equal to 1

Add a series with too many number of stamps
	Input Text              id=number-of-stamps  5
	Submit Form             id=add-series-form
	${msg}=                 Set Variable  Number of stamps must be less than or equal to a stamps quantity in the series
	Element Text Should Be  id=number-of-stamps.errors  ${msg}

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test
	# We need a series with more than 1 stamp, so the number-of-stamps field won't be hidden.
	# We also need a series with no more than 4 stamps, so the 5 stamps will lead to an error.
	Go To                               ${SITE_URL}/series/2

After Test Suite
	Log Out
	Close Browser
