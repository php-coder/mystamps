*** Settings ***
Documentation   Verify scenarios of adding a catalog price to a series
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  add-price  logic  htmx

*** Test Cases ***
Add a price by a catalog
	[Tags]      unstable
	[Template]  Add a price
	michel      10  EUR
	scott       20  USD
	yvert       30  EUR
	gibbons     40  GBP
	solovyov    50  RUB
	zagorski    60  RUB

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/4

Add a price
	[Arguments]                       ${catalog}  ${price}  ${currency}
	Select From List By Value         id:price-catalog-name  ${catalog}
	Input Text                        id:catalog-price  ${price}
	Submit Form                       id:add-catalog-price-form
	Wait Until Page Does Not Contain  id:add-catalog-price-form
	Wait Until Page Contains Element  id:${catalog}_catalog_info
	Element Text Should Be            id:${catalog}_catalog_info  ${price} ${currency}
