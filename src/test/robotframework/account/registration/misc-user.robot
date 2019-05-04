*** Settings ***
Documentation   Verify account registration scenarios
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Suite Setup     Before Test Suite
Suite Teardown  After Test Suite
Test Setup      Before Test
Force Tags      account  registration  misc

*** Test Cases ***
User cannot register account again
	Page Should Contain              You have already registered
	Page Should Not Contain Element  id=register-account-form

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test

Before Test
	Go To  ${SITE_URL}/account/register

After Test Suite
	Log Out
	Close Browser
