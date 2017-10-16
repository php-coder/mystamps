*** Settings ***
Documentation    Verify scenarios of adding additional image to a series
Library          Selenium2Library
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       series  add-image  logic

*** Test Cases ***
Add additional image by providing a file
	[Documentation]                Verify uploading an additional image to a series
	Page Should Not Contain Image  id=series-image-2
	Choose File                    id=image  ${MAIN_RESOURCE_DIR}${/}test.png
	Submit Form                    id=add-image-form
	Location Should Be             ${SITE_URL}/series/1
	Page Should Contain Image      id=series-image-2

Add additional image by providing a URL to image
	[Documentation]                Verify downloading an additional image to a series
	Page Should Not Contain Image  id=series-image-3
	Input Text                     id=image-url  ${SITE_URL}/image/1
	Submit Form                    id=add-image-form
	Location Should Be             ${SITE_URL}/series/1
	Page Should Contain Image      id=series-image-3

*** Keywords ***
Before Test Suite
	[Documentation]                     Open browser, register fail hook and login as admin
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test

Before Test
	[Documentation]  Open a page with a series info
	Go To            ${SITE_URL}/series/1

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser
