*** Settings ***
Documentation   Verify account authentication scenarios
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Suite Setup     Before Test Suite
Suite Teardown  After Test Suite
Force Tags      account  authentication  logic

*** Test Cases ***
Successful authentication
	Input Text                  id=login     coder
	Input Text                  id=password  test
	Submit Form                 id=auth-account-form
	Location Should Be          ${SITE_URL}/
	Page Should Contain Link    Test Suite
	Page Should Contain Button  value=Sign out

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

After Test Suite
	Log Out
	Close Browser
