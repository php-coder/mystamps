*** Settings ***
Documentation    Verify scenarios of adding additional image to a series
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Test Setup       Before Test
Force Tags       series  add-image  logic  htmx

*** Test Cases ***
Add additional image by uploading a file
	Page Should Not Contain Image  id:series-image-2
	Choose File                    id:image  ${MAIN_RESOURCE_DIR}${/}test.png
	Submit Form                    id:add-image-form
	Page Should Contain Image      id:series-image-2

Add additional image by downloading a file from URL
	Page Should Not Contain Image  id:series-image-3
	Input Text                     id:image-url  ${SITE_URL}/image/1
	Submit Form                    id:add-image-form
	Page Should Contain Image      id:series-image-3

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test

Before Test
	Go To  ${SITE_URL}/series/1

