*** Settings ***
Documentation   Verify series search scenarios
Library         SeleniumLibrary
Resource        ../../selenium.utils.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  search  logic

*** Test Cases ***
Search series by non-existing catalog number
	Input Text                      id=catalogNumber  888
	Select Random Option From List  id=catalogName
	Submit Form                     id=search-series-form
	Element Text Should Be          id=no-series-found  No series found

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
