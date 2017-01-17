*** Settings ***
Documentation    Verify country creation scenarios
Library          Selenium2Library
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       country  logic

*** Test Cases ***
Create country with name in English
	[Documentation]          Verify creation of country by filling only mandatory fields
	Input Text               id=name  Germany
	Submit Form              id=add-country-form
	Location Should Be       ${SITE_URL}/country/germany
	Element Text Should Be   id=page-header  Stamps of Germany
	Go To                    ${SITE_URL}/series/add
	Country Field Should Be  Germany

Create country with name in English and Russian
	[Documentation]         Verify creation of country by specifying names in 2 languages
	Input Text              id=name  Russia
	Input Text              id=nameRu  Россия
	Submit Form             id=add-country-form
	Location Should Be      ${SITE_URL}/country/russia
	Element Text Should Be  id=page-header  Stamps of Russia

*** Keywords ***
Before Test Suite
	[Documentation]                     Open browsers, register fail hook and login as admin
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test

Before Test
	[Documentation]  Open create country page
	Go To            ${SITE_URL}/country/add

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser

Log In As
	[Documentation]  Log in as a user
	[Arguments]      ${login}  ${password}
	Go To            ${SITE_URL}/account/auth
	Input Text       id=login  ${login}
	Input Password   id=password  ${password}
	Submit Form      id=auth-account-form

Log Out
	[Documentation]  Log out current user
	Submit Form      id=logout-form

Country Field Should Be
	[Documentation]                   Verify the selection of the select list that is using selectize.js
	[Arguments]                       ${value}
	# We can't use "List Selection Should Be" because
	# 1) it doesn't work with invisible elements (and selectize.js makes field invisible)
	# 2) selectize.js dynamically creates list of countries only when we're clicking on the field
	Click Element                     id=country-selectized
	${dropdownXpath}=                 Set Variable  //*[contains(@class, "selectize-dropdown-content")]
	Wait Until Page Contains Element  xpath=${dropdownXpath}/*[contains(@class, "option")]
	Xpath Should Match X Times        xpath=${dropdownXpath}/*[text() = "${value}"]  expectedXpathCount=1
