*** Settings ***
Documentation    Verify country creation validation scenarios
Library          Selenium2Library
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       Country  Validation

*** Test Cases ***
Create country with too short name
	[Documentation]         Verify validation of too short name
	Input Text              id=name  jj
	Input Text              id=nameRu  яя
	Submit Form             id=add-country-form
	Element Text Should Be  id=name.errors  Value is less than allowable minimum of 3 characters
	Element Text Should Be  id=nameRu.errors  Value is less than allowable minimum of 3 characters

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
