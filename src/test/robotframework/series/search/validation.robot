*** Settings ***
Documentation   Verify series search validation scenarios
Library         SeleniumLibrary
Resource        ../../selenium.utils.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  search  validation

*** Test Cases ***
Search the series with empty required field
	[Setup]                 Disable Client Validation
	Submit Form             id=search-series-form
	Element Text Should Be  id=catalogNumber.errors  Value must not be empty

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

Disable Client Validation
	Remove Element Attribute  catalogNumber  required
