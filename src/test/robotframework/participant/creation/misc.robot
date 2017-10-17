*** Settings ***
Documentation    Verify miscellaneous aspects of participant creation
Library          Selenium2Library
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       participant  misc

*** Test Cases ***
Name and url should be stripped from leading and trailing spaces
	[Documentation]            Verify removing of leading and trailing spaces from name and url
	Input Text                 id=name  ${SPACE * 2}f${SPACE * 2}
	Input Text                 id=url   ${SPACE * 2}url${SPACE * 2}
	Submit Form                id=add-participant-form
	Textfield Value Should Be  id=name  f
	# We can't use "Textfield Value Should Be" because it causes NPE:
	# https://github.com/MarkusBernhardt/robotframework-selenium2library-java/issues/92
	${value}=                  Get Value  id=url
	Should Be Equal            ${value}   url

*** Keywords ***
Before Test Suite
	[Documentation]                     Login as admin and go to add participant page
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/participant/add

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser
