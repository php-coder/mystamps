*** Settings ***
Documentation    Verify access to Togglz console from user
Library          SeleniumLibrary
Resource         ../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       togglz  access

*** Test Cases ***
User don't have access to Togglz console
	Go To                   ${SITE_URL}/togglz
	Element Text Should Be  id:error-code  403
	Element Text Should Be  id:error-msg   Forbidden

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test

