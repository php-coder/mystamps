*** Settings ***
Documentation    Verify validation scenarios for adding series sales
Library          SeleniumLibrary
Resource         ../../../auth.steps.robot
Resource         ../../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Test Setup       Disable Client Validation  add-series-sales-form
Force Tags       series  sales  validation  htmx

*** Test Cases ***
Create series with empty required fields
	Select From List By Value  id:seller           ${EMPTY}
	Input Text                 id:price            ${EMPTY}
	Select From List By Value  id:currency         ${EMPTY}
	Submit Form                id:add-series-sales-form
	Element Text Should Be     id:seller.errors    Value must not be empty
	Element Text Should Be     id:price.errors     Value must not be empty
	Element Text Should Be     id:currency.errors  Value must not be empty

Create series sale with too long url
	${letter}=              Set Variable   j
	Input Text              id:url         http://${letter * 767}
	Submit Form             id:add-series-sales-form
	Element Text Should Be  id:url.errors  Value is greater than allowable maximum of 767 characters

Create series sale with invalid url
	Input Text              id:url         invalid-url
	Submit Form             id:add-series-sales-form
	Element Text Should Be  id:url.errors  Value must be a valid URL

Create series sale with the prices in the same currency
	Input Text                 id:price                100
	Select From List By Label  id:currency             USD
	Input Text                 id:alt-price            200
	Select From List By Label  id:alt-currency         USD
	Submit Form                id:add-series-sales-form
	Element Text Should Be     id:alt-currency.errors  Price and alternative price must be in a different currencies

Create series sale with alternative price but without currency
	Input Text                 id:alt-price            200
	Select From List By Value  id:alt-currency         ${EMPTY}
	Submit Form                id:add-series-sales-form
	Element Text Should Be     id:alt-currency.errors  Alternative price and currency must be specified or left empty, specifying only one of them makes no-sense

Create series sale with alternative currency but without price
	Input Text                 id:alt-price            ${EMPTY}
	Select From List By Label  id:alt-currency         GBP
	Submit Form                id:add-series-sales-form
	Element Text Should Be     id:alt-currency.errors  Alternative price and currency must be specified or left empty, specifying only one of them makes no-sense

Create series sale with the same seller and buyer
	Select From List By Label  id:seller        Tommy Lee Jones
	Select From List By Label  id:buyer         Tommy Lee Jones
	Submit Form                id:add-series-sales-form
	Element Text Should Be     id:buyer.errors  Seller and buyer must be different

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/1
