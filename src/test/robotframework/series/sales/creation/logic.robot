*** Settings ***
Documentation    Verify adding a sale to a series
Library          SeleniumLibrary
Resource         ../../../auth.steps.robot
Resource         ../../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       series  sales  logic  htmx

*** Test Cases ***
Add a sale with only required fields
	Go To                      ${SITE_URL}/series/2
	Select From List By Label  id:seller              Tommy Lee Jones
	Input Text                 id:price               125
	Select From List By Value  id:currency            RUB
	Submit Form                id:add-series-sales-form
	Element Text Should Be     id:series-sale-1-info  Tommy Lee Jones was selling for 125.00 RUB

Add a sale with all fields
	Go To                      ${SITE_URL}/series/3
	Input Text                 id:date          04.01.2021
	Select From List By Label  id:seller        Eicca Toppinen
	Input Text                 id:url           http://example.com/series-sale
	Input Text                 id:price         7.5
	Select From List By Value  id:currency      EUR
	Input Text                 id:alt-price     10.1
	Select From List By Label  id:alt-currency  USD
	Select From List By Value  id:condition     MNH
	Select From List By Label  id:buyer         Tommy Lee Jones
	Submit Form                id:add-series-sales-form
	Element Text Should Be     id:series-sale-1-info         04.01.2021 Eicca Toppinen sold to Tommy Lee Jones for 7.50 EUR (10.10 USD) (MNH)
	Link Should Point To       id:series-sale-1-transaction  http://example.com/series-sale


*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
