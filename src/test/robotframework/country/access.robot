*** Settings ***
Documentation    Verify access to country related pages (including non-existing)
Library          SeleniumLibrary
Resource         ../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       country  access

*** Test Cases ***
Anonymous user cannot create country
	Go To                   ${SITE_URL}/country/add
	Element Text Should Be  id:error-code  403
	Element Text Should Be  id:error-msg  Forbidden

Opening a page of non-existing country show an error
	Go To                             ${SITE_URL}/country/country-404-error-test
	Element Text Should Be            id:error-code  404
	Element Text Should Match Regexp  id:error-msg   Requested page[\\n\\r]+not found

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

