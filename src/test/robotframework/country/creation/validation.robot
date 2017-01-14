*** Settings ***
Documentation    Verify country creation validation scenarios
Library          Selenium2Library
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       country  validation

*** Test Cases ***
Create country with too short name
	[Documentation]         Verify validation of too short name
	Input Text              id=name  jj
	Input Text              id=nameRu  яя
	Submit Form             id=add-country-form
	Element Text Should Be  id=name.errors  Value is less than allowable minimum of 3 characters
	Element Text Should Be  id=nameRu.errors  Value is less than allowable minimum of 3 characters

Create country with too long name
	[Documentation]         Verify validation of too long name
	${englishLetter}=       Set Variable  j
	${russianLetter}=       Set Variable  я
	Input Text              id=name  ${englishLetter * 51}
	Input Text              id=nameRu  ${russianLetter * 51}
	Submit Form             id=add-country-form
	Element Text Should Be  id=name.errors  Value is greater than allowable maximum of 50 characters
	Element Text Should Be  id=nameRu.errors  Value is greater than allowable maximum of 50 characters

Create country with forbidden characters in name
	[Documentation]         Verify validation of invalid name
	Input Text              id=name  S0m3+CountryN_ame
	Input Text              id=nameRu  Нек0торо3+наз_вание
	Submit Form             id=add-country-form
	Element Text Should Be  id=name.errors  Country name must consist only latin letters, hyphen or spaces
	Element Text Should Be  id=nameRu.errors  Country name must consist only Russian letters, hyphen or spaces

Create country with repeating hyphens in name
	[Documentation]         Verify validation of name with repeating hyphens
	Input Text              id=name  te--st
	Input Text              id=nameRu  те--ст
	Submit Form             id=add-country-form
	Element Text Should Be  id=name.errors  Value must not contain repetition of hyphen
	Element Text Should Be  id=nameRu.errors  Value must not contain repetition of hyphen

Create country with name that starts with hyphen
	[Documentation]         Verify validation of name with leading hyphen
	Input Text              id=name  -test
	Input Text              id=nameRu  -тест
	Submit Form             id=add-country-form
	Element Text Should Be  id=name.errors  Value must not start or end with hyphen
	Element Text Should Be  id=nameRu.errors  Value must not start or end with hyphen

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
