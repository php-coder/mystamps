*** Settings ***
Documentation    Verify series creation scenarios
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       series  logic

*** Test Cases ***
Create series by filling only required fields and providing an image
	Select From List By Label  id=category  Sport
	Input Text                 id=quantity  2
	Choose File                id=image  ${MAIN_RESOURCE_DIR}${/}test.png
	Submit Form                id=add-series-form
	${location}=               Get Location
	Should Match Regexp        ${location}  /series/\\d+
	Element Text Should Be     id=category_name  Sport
	Element Text Should Be     id=quantity  2
	Element Text Should Be     id=perforated  Yes
	Page Should Contain Image  id=series-image-1

Create series by filling only required fields and providing a URL to image
	Select From List By Label  id=category  Sport
	Input Text                 id=quantity  1
	Input Text                 id=image-url  ${SITE_URL}/image/1
	Submit Form                id=add-series-form
	${location}=               Get Location
	Should Match Regexp        ${location}  /series/\\d+
	Element Text Should Be     id=category_name  Sport
	Element Text Should Be     id=quantity  1
	Element Text Should Be     id=perforated  Yes
	Page Should Contain Image  id=series-image-1

Create series by filling all fields
	[Tags]                     unstable
	Select From List By Label  id=category  Sport
	Select Country             Italy
	Input Text                 id=quantity  3
	Unselect Checkbox          id=perforated
	Choose File                id=image  ${MAIN_RESOURCE_DIR}${/}test.png
	Click Element              id=specify-issue-date-link
	Select From List By Value  id=day  4
	Select From List By Value  id=month  5
	Select From List By Value  id=year  1999
	Click Element              id=add-catalog-numbers-link
	Input Text                 id=michelNumbers    101, 102, 103
	Input Text                 id=michelPrice      10.5
	Input Text                 id=scottNumbers     110, 111, 112
	Input Text                 id=scottPrice       1000
	Input Text                 id=yvertNumbers     120, 121, 122
	Input Text                 id=yvertPrice       8.11
	Input Text                 id=gibbonsNumbers   130, 131, 132
	Input Text                 id=gibbonsPrice     400.335
	Input Text                 id=solovyovNumbers  140, 141, 142
	Input Text                 id=solovyovPrice    200.5
	Input Text                 id=zagorskiNumbers  150, 151, 152
	Input Text                 id=zagorskiPrice    300.55
	Click Element              id=add-comment-link
	Input Text                 id=comment  Any text
	Submit Form                id=add-series-form
	${location}=               Get Location
	Should Match Regexp        ${location}  /series/\\d+
	Element Text Should Be     id=category_name  Sport
	Element Text Should Be     id=country_name  Italy
	Element Text Should Be     id=issue_date  04.05.1999
	Element Text Should Be     id=quantity  3
	Element Text Should Be     id=perforated  No
	Element Text Should Be     id=michel_catalog_info    \#101-103 (10.5 EUR)
	Element Text Should Be     id=scott_catalog_info     \#110-112 (1000 USD)
	Element Text Should Be     id=yvert_catalog_info     \#120-122 (8.11 EUR)
	# FIXME: disable rounding mode
	Element Text Should Be     id=gibbons_catalog_info   \#130-132 (400.34 GBP)
	Element Text Should Be     id=solovyov_catalog_info  \#140-142 (200.5 RUB)
	Element Text Should Be     id=zagorski_catalog_info  \#150-152 (300.55 RUB)
	Element Text Should Be     id=comment  Any text
	Page Should Contain Image  id=series-image-1

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test

Before Test
	Go To  ${SITE_URL}/series/add

After Test Suite
	Log Out
	Close Browser

Select Country
	[Documentation]                   Select the given value in a select list that is using selectize.js
	[Arguments]                       ${value}
	# We can't use "Select From List By Label" because
	# 1) it doesn't work with invisible elements (and selectize.js makes field invisible)
	# 2) selectize.js dynamically creates list of countries only when we're clicking on the field
	Click Element                     id=country-selectized
	${countryOption}=                 Catenate  SEPARATOR=/
	...                               //*[contains(@class, "selectize-control")]
	...                               *[contains(@class, "selectize-dropdown")]
	...                               *[contains(@class, "selectize-dropdown-content")]
	...                               *[contains(@class, "option") and text()="${value}"]
	Wait Until Page Contains Element  xpath=${countryOption}
	Click Element                     xpath=${countryOption}
