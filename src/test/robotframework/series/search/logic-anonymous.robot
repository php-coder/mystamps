*** Settings ***
Documentation   Verify series search scenarios
Library         SeleniumLibrary
Resource        ../../selenium.utils.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  search  logic

*** Test Cases ***
Search series by non-existing catalog number 
	Input Text                      id:catalogNumber  888
	Select Random Option From List  id:catalogName
	Submit Form                     id:search-series-form
	Element Text Should Be          id:no-series-found  No series found

Search series by existing catalog number
	[Template]  Search Series By Catalog Name And Number
	michel      99
	scott       99
	yvert       99
	gibbons     99
	solovyov    77
	zagorski    83

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

Search Series By Catalog Name And Number
	[Arguments]                  ${name}  ${number}
	Go To                        ${SITE_URL}
	Input Text                   id:catalogNumber  ${number}
	Select From List By Value    id:catalogName    ${name}
	Submit Form                  id:search-series-form
	Page Should Contain Element  css:#search-results [href="/series/1"]
