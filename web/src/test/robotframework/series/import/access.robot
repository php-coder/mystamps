*** Settings ***
Documentation    Verify access to import series related pages
Library          SeleniumLibrary
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  import-series  access

*** Test Cases ***
Anonymous user cannot request series import
	Go To                   ${SITE_URL}/series/import/request
	Element Text Should Be  id=error-code  403
	Element Text Should Be  id=error-msg   Forbidden

Anonymous user cannot access the status of the series import
	Go To                   ${SITE_URL}/series/import/request/1
	Element Text Should Be  id=error-code  403
	Element Text Should Be  id=error-msg   Forbidden

Anonymous user cannot see a list of import requests
	Go To                   ${SITE_URL}/series/import/requests
	Element Text Should Be  id=error-code  403
	Element Text Should Be  id=error-msg   Forbidden

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

After Test Suite
	Close Browser
