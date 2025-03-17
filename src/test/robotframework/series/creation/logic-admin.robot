*** Settings ***
Documentation    Verify series creation scenarios from admin
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Test Setup       Before Test
Force Tags       series  logic  htmx

*** Test Cases ***
Create series by filling only required fields and providing an image
	Select From List By Label  id:category  Sport
	Input Text                 id:quantity  2
	Choose File                id:image  ${MAIN_RESOURCE_DIR}${/}test.png
	Submit Form                id:add-series-form
	Element Text Should Be     id:category_name  Sport
	Element Text Should Be     id:quantity  2
	Element Text Should Be     id:perforated  Yes
	Page Should Contain Image  id:series-image-1

Create series by filling only required fields and providing a URL to image
	Select From List By Label  id:category  Sport
	Input Text                 id:quantity  1
	Input Text                 id:image-url  ${SITE_URL}/image/1
	Submit Form                id:add-series-form
	Element Text Should Be     id:category_name  Sport
	Element Text Should Be     id:quantity  1
	Element Text Should Be     id:perforated  Yes
	Page Should Contain Image  id:series-image-1

Create series by filling all fields
	Select From List By Label  id:category  Sport
	Selectize By Value         country  italy
	Input Text                 id:quantity  3
	Unselect Checkbox          id:perforated
	Choose File                id:image  ${MAIN_RESOURCE_DIR}${/}test.png
	Click Element              id:specify-issue-date-link
	Select From List By Value  id:day    4
	Select From List By Value  id:month  5
	Select From List By Value  id:year   1999
	Click Element              id:add-catalog-numbers-link
	Input Text                 id:michelNumbers    101, 102, 103
	Input Text                 id:michelPrice      10.5
	Input Text                 id:scottNumbers     110, 111, 112
	Input Text                 id:scottPrice       1000
	Input Text                 id:yvertNumbers     120, 121, 122
	Input Text                 id:yvertPrice       8.11
	Input Text                 id:gibbonsNumbers   130, 131, 132
	Input Text                 id:gibbonsPrice     400.335
	Input Text                 id:solovyovNumbers  140, 141, 142
	Input Text                 id:solovyovPrice    200.5
	Input Text                 id:zagorskiNumbers  150, 151, 152
	Input Text                 id:zagorskiPrice    300.55
	Submit Form                id:add-series-form
	Element Text Should Be     id:category_name  Sport
	Element Text Should Be     id:country_name   Italy
	Element Text Should Be     id:issue_date     04.05.1999
	Element Text Should Be     id:quantity       3
	Element Text Should Be     id:perforated     No
	Element Text Should Be     id:michel_catalog_info    \#101-103 (10.5 EUR)
	Element Text Should Be     id:scott_catalog_info     \#110-112 (1000 USD)
	Element Text Should Be     id:yvert_catalog_info     \#120-122 (8.11 EUR)
	# FIXME: disable rounding mode
	Element Text Should Be     id:gibbons_catalog_info   \#130-132 (400.34 GBP)
	Element Text Should Be     id:solovyov_catalog_info  \#140-142 (200.5 RUB)
	Element Text Should Be     id:zagorski_catalog_info  \#150-152 (300.55 RUB)
	Page Should Contain Image  id:series-image-1

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test

Before Test
	Go To  ${SITE_URL}/series/add

