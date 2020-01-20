*** Settings ***
Documentation    Verify miscellaneous aspects of adding series sales
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       series  sales  misc

*** Test Cases ***
Url should be stripped from leading and trailing spaces
	[Setup]                    Disable Client Validation
	Input Text                 id:url  ${SPACE * 2}bad-value${SPACE * 2}
	Submit Form                id:add-series-sales-form
	Textfield Value Should Be  id:url  bad-value

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/1

Disable Client Validation
	Remove Element Attribute  price  required
