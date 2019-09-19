*** Settings ***
Documentation   Verify scenarios of series search in user's collection
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Resource        ../../selenium.utils.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  search  logic

*** Test Cases ***
Search series by non-existing catalog number in a collection
	Input Text                      id:catalogNumber  888
	Select Random Option From List  id:catalogName
	Select Checkbox                 id:in-collection
	Submit Form                     id:search-series-form
	Element Text Should Be          id:no-series-found  No series found

Search series by existing catalog number in a collection
	[Template]  Search Series By Catalog Name And Number In Collection
	michel      99
	scott       99
	yvert       99
	gibbons     99
	solovyov    77
	zagorski    83

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=seriesowner  password=test
    Go To                               ${SITE_URL}

Search Series By Catalog Name And Number In Collection
	[Arguments]                  ${name}  ${number}
	Go To                        ${SITE_URL}
	Input Text                   id:catalogNumber  ${number}
	Select From List By Value    id:catalogName    ${name}
	Select Checkbox              id:in-collection
	Submit Form                  id:search-series-form
	Page Should Contain Element  css:#search-results [href="/series/1"]
