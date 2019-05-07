*** Settings ***
Documentation    Verify series creation scenarios from user
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
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

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test

Before Test
	Go To  ${SITE_URL}/series/add

After Test Suite
	Log Out
	Close Browser
