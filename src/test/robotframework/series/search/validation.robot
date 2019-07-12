*** Settings ***
Documentation   Verify series search validation scenarios
Library         SeleniumLibrary
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  search  validation

*** Test Cases ***
Search the series with empty required field
	Submit Form             id=search-series-form
	Element Text Should Be  id=catalogNumber.errors  Value must not be empty

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
