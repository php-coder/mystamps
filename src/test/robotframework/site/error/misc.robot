*** Settings ***
Documentation    Verify error pages
Library          SeleniumLibrary
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       error-page  misc

*** Test Cases ***
Client should see a custom page for Bad Request error
	Title Should Be         400: bad request
	Element Text Should Be  id:error-code  400
	Element Text Should Be  id:error-msg   Bad request

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/series/info  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

