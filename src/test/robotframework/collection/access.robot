*** Settings ***
Documentation    Verify access to collection related pages (including non-existing)
Library          SeleniumLibrary
Resource         ../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       collection  access

*** Test Cases ***
Opening a page of non-existing collection show an error
	Go To                             ${SITE_URL}/collection/collection-404-error-test
	Element Text Should Be            id:error-code  404
	Element Text Should Match Regexp  id:error-msg   Requested page[\\n\\r]+not found

*** Keywords ***
Before Test Suite
	Open Browser                        about:blank  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

