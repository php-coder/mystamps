*** Settings ***
Documentation    Verify series creation scenarios from user
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Test Setup       Before Test
Force Tags       series  logic

*** Test Cases ***
Create series by filling only required fields
	Select From List By Label  id=category  Sport
	Input Text                 id=quantity  2
	Choose File                id=image     ${MAIN_RESOURCE_DIR}${/}test.png
	Submit Form                id=add-series-form
	Element Text Should Be     id=category_name  Sport
	Element Text Should Be     id=quantity       2
	Element Text Should Be     id=perforated     Yes
	Page Should Contain Image  id=series-image-1

Create series by filling all fields
	Select From List By Label  id=category  Sport
	Select Country             Italy
	Input Text                 id=quantity  3
	Unselect Checkbox          id=perforated
	Choose File                id=image  ${MAIN_RESOURCE_DIR}${/}test.png
	Click Element              id=specify-issue-date-link
	Select From List By Value  id=day    8
	Select From List By Value  id=month  9
	Select From List By Value  id=year   1999
	Click Element              id=add-catalog-numbers-link
	Input Text                 id=michelNumbers    1, 2, 3
	Input Text                 id=michelPrice      10.5
	Input Text                 id=scottNumbers     10, 11, 12
	Input Text                 id=scottPrice       1000
	Input Text                 id=yvertNumbers     20, 21, 22
	Input Text                 id=yvertPrice       8.11
	Input Text                 id=gibbonsNumbers   30, 31, 32
	Input Text                 id=gibbonsPrice     400.335
	Input Text                 id=solovyovNumbers  40, 41, 42
	Input Text                 id=solovyovPrice    140.2
	Input Text                 id=zagorskiNumbers  50, 51, 52
	Input Text                 id=zagorskiPrice    150.2
	Submit Form                id=add-series-form
	Element Text Should Be     id=category_name  Sport
	Element Text Should Be     id=country_name   Italy
	Element Text Should Be     id=issue_date     08.09.1999
	Element Text Should Be     id=quantity       3
	Element Text Should Be     id=perforated     No
	Element Text Should Be     id=michel_catalog_info    \#1-3 (10.5 EUR)
	Element Text Should Be     id=scott_catalog_info     \#10-12 (1000 USD)
	Element Text Should Be     id=yvert_catalog_info     \#20-22 (8.11 EUR)
	# FIXME: disable rounding mode
	Element Text Should Be     id=gibbons_catalog_info   \#30-32 (400.34 GBP)
	Element Text Should Be     id=solovyov_catalog_info  \#40-42 (140.2 RUB)
	Element Text Should Be     id=zagorski_catalog_info  \#50-52 (150.2 RUB)
	Page Should Contain Image  id=series-image-1

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test

Before Test
	Go To  ${SITE_URL}/series/add

