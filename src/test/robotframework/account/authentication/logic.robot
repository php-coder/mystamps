*** Settings ***
Documentation   Verify account authentication scenarios
Library         SeleniumLibrary
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      account  authentication  logic

*** Test Cases ***
Successful authentication
	Input Text                  id:login     coder
	Input Text                  id:password  test
	Submit Form                 id:auth-account-form
	Location Should Be          ${SITE_URL}/
	Page Should Contain Link    link:Test Suite
	Page Should Contain Button  value:Sign out

Log out
	Go To                     ${SITE_URL}/account/auth
	Submit Form               id:logout-form
	Location Should Be        ${SITE_URL}/
	Page Should Contain Link  link:Sign in
	Page Should Contain Link  link:Register

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

