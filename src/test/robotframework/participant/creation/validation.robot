*** Settings ***
Documentation    Verify participant creation validation scenarios
Library          Selenium2Library
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       participant  validation

*** Test Cases ***
Create participant with blank required fields
	[Documentation]         Verify validation of required fields
	Submit Form             id=add-participant-form
	Element Text Should Be  id=name.errors  Value must not be empty

Create participant with too short name
	[Documentation]         Verify validation of too short name
	Input Text              id=name  xx
	Submit Form             id=add-participant-form
	Element Text Should Be  id=name.errors  Value is less than allowable minimum of 3 characters

Create participant with too long name and url
	[Documentation]         Verify validation of too long name and url
	${letter}=              Set Variable  j
	Input Text              id=name  ${letter * 51}
	Input Text              id=url   http://${letter * 255}
	Submit Form             id=add-participant-form
	Element Text Should Be  id=name.errors  Value is greater than allowable maximum of 50 characters
	Element Text Should Be  id=url.errors   Value is greater than allowable maximum of 255 characters

Create participant with invalid url
	[Documentation]         Verify validation of invalid url
	Input Text              id=url  invalid-url
	Submit Form             id=add-participant-form
	Element Text Should Be  id=url.errors  Value must be a valid URL

*** Keywords ***
Before Test Suite
	[Documentation]                     Login as admin and go to add participant page
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/participant/add

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser
