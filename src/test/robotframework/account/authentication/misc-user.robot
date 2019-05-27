*** Settings ***
Documentation   Verify account authentication scenarios
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Test Setup      Before Test
Force Tags      account  authentication  misc

*** Test Cases ***
User cannot authenticate again
	Page Should Contain              You have already authenticated
	Page Should Not Contain Element  id=auth-account-form

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test

Before Test
	Go To  ${SITE_URL}/account/auth

