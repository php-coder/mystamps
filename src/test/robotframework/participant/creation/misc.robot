*** Settings ***
Documentation    Verify miscellaneous aspects of participant creation
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       participant  misc

*** Test Cases ***
Name and url should be stripped from leading and trailing spaces
	Input Text                 id:name  ${SPACE * 2}f${SPACE * 2}
	Input Text                 id:url   ${SPACE * 2}url${SPACE * 2}
	Submit Form                id:add-participant-form
	Textfield Value Should Be  id:name  f
	Textfield Value Should Be  id:url   url

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/participant/add

