*** Settings ***
Documentation    Verify miscellaneous aspects of series creation
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  misc

*** Test Cases ***
Comment should be stripped from leading and trailing spaces
	Click Element             id=add-comment-link
	Input Text                id=comment  ${SPACE * 2}example comment${SPACE * 2}
	Submit Form               id=add-series-form
	Textarea Value Should Be  id=comment  example comment

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/add

After Test Suite
	Log Out
	Close Browser
