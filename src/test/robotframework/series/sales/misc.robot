*** Settings ***
Documentation    Verify miscellaneous aspects of adding series sales
Library          Selenium2Library
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  sales  misc

*** Test Cases ***
Url should be stripped from leading and trailing spaces
	Input Text                id=url  ${SPACE * 2}bad-value${SPACE * 2}
	Submit Form               id=add-series-sales-form
	Urlfield Value Should Be  id=url  bad-value

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/1

After Test Suite
	Log Out
	Close Browser
