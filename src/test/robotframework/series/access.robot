*** Settings ***
Documentation    Verify access to series related pages (including non-existing)
Library          Selenium2Library
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  access

*** Test Cases ***
Anonymous user cannot create series
	Go To                   ${SITE_URL}/series/add
	Element Text Should Be  id=error-code  403
	Element Text Should Be  id=error-msg  Forbidden

Opening a page of non-existing series show an error
	Go To                   ${SITE_URL}/series/999
	Element Text Should Be  id=error-code  404
	Element Text Should Be  id=error-msg  Requested page${\n}not found

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

After Test Suite
	Close Browser
