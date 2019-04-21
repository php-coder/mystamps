*** Settings ***
Documentation    Verify series creation validation scenarios from a user
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  validation

*** Test Cases ***
Create series with empty required fields
	Submit Form                      id=add-series-form
	Element Text Should Be           id=category.errors  Value must not be empty
	Element Text Should Be           id=quantity.errors  Value must not be empty
	Element Text Should Be           id=image.errors     Value must not be empty
	Page Should Not Contain Element  id=image-url.errors

Create series with non-numeric quantity
	Input Text              id=quantity  NaN
	Submit Form             id=add-series-form
	Element Text Should Be  id=quantity.errors  Invalid value

Create series with too small quantity
	Input Text              id=quantity  0
	Submit Form             id=add-series-form
	Element Text Should Be  id=quantity.errors  Value must be greater than or equal to 1

Create series with too large quantity
	Input Text              id=quantity  51
	Submit Form             id=add-series-form
	Element Text Should Be  id=quantity.errors  Value must be less than or equal to 50

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test
	Go To                               ${SITE_URL}/series/add

After Test Suite
	Log Out
	Close Browser
