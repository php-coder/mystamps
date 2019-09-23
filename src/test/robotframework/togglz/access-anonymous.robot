*** Settings ***
Documentation    Verify access to Togglz console from anonymous user
Library          SeleniumLibrary
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       togglz  access

*** Test Cases ***
Anonymous user don't have access to Togglz console
	Go To                   ${SITE_URL}/togglz
	Element Text Should Be  id:error-code  403
	Element Text Should Be  id:error-msg   Forbidden

*** Keywords ***
Before Test Suite
	Open Browser                        about:blank  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

