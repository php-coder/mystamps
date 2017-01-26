*** Settings ***
Documentation    Verify series creation scenarios
Library          Selenium2Library
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       series  logic

*** Test Cases ***
Create series by filling only required fields
	[Documentation]            Verify creation of series by filling only mandatory fields
	Select From List By Label  id=category  Sport
	Input Text                 id=quantity  2
	Choose File                id=image  ${RESOURCE_DIR}${/}test.png
	Submit Form                id=add-series-form
	${location}=               Get Location
	Should Match Regexp        ${location}  /series/\\d+
	Element Text Should Be     id=category_name  Sport
	Element Text Should Be     id=quantity  2
	Element Text Should Be     id=perforated  Yes
	Page Should Contain Image  id=series-image-1

*** Keywords ***
Before Test Suite
	[Documentation]                     Open browser, register fail hook and login as admin
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test

Before Test
	[Documentation]  Open create series page
	Go To            ${SITE_URL}/series/add

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
