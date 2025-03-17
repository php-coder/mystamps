*** Settings ***
Documentation    Verify validation of adding a series to collection for a paid user
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Test Setup       Disable Client Validation  add-series-form
Force Tags       collection  validation  htmx

*** Test Cases ***
Add a series with price but without currency
	Input Text                 id:paid-price            20
	Select From List By Value  id:paid-currency         ${EMPTY}
	Submit Form                id:add-series-form
	Element Text Should Be     id:paid-currency.errors  Price and currency must be specified or left empty, specifying only one of them makes no-sense

Add a series with currency but without price
	Input Text                 id:paid-price             ${EMPTY}
	Select From List By Label  id:paid-currency          CZK
	Submit Form                id:add-series-form
	Element Text Should Be     id:paid-currency.errors   Price and currency must be specified or left empty, specifying only one of them makes no-sense

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=paid  password=test
	Go To                               ${SITE_URL}/series/1
