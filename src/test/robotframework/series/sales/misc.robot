*** Settings ***
Documentation    Verify miscellaneous aspects of adding series sales
Library          Selenium2Library
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  sales  misc

*** Test Cases ***
Url should be stripped from leading and trailing spaces
	Input Text       id=url  ${SPACE * 2}bad-value${SPACE * 2}
	Submit Form      id=add-series-sales-form
	# We can't use "Textfield Value Should Be" because it causes NPE:
	# https://github.com/MarkusBernhardt/robotframework-selenium2library-java/issues/92
	${value}=        Get Value  id=url
	Should Be Equal  ${value}   bad-value

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/1

After Test Suite
	Log Out
	Close Browser
