*** Settings ***
Documentation    Verify validation scenarios during adding additional image to a series
Library          Selenium2Library
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  add-image  validation

*** Test Cases ***
Add image but without filling a required field
	[Documentation]         Verify validation of a required field
	Submit Form             id=add-image-form
	Element Text Should Be  id=image.errors  Value must not be empty

*** Keywords ***
Before Test Suite
	[Documentation]                     Login as admin and open a page with a series info
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/1

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser
