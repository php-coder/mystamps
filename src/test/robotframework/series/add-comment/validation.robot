*** Settings ***
Documentation   Verify validation scenarios for adding a comment to a series
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Resource        ../../selenium.utils.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  add-comment  validation  htmx

*** Test Cases ***
Add comment with empty required field
	[Setup]                           Disable Client Validation  add-comment-form
	Submit Form                       id:add-comment-form
	Wait Until Page Contains Element  id:new-comment.errors
	Element Text Should Be            id:new-comment.errors  must not be empty

Add a blank comment
	Input Text                        id:new-comment  ${SPACE}${SPACE}
	Submit Form                       id:add-comment-form
	Wait Until Page Contains Element  id:new-comment.errors
	Element Text Should Be            id:new-comment.errors  must not be empty

Add too long comment
	${letter}=                        Set Variable  x
	Input Text                        id:new-comment  ${letter * 1025}
	Submit Form                       id:add-comment-form
	Wait Until Page Contains Element  id:new-comment.errors
	Element Text Should Be            id:new-comment.errors  Value is greater than allowable maximum of 1024 characters

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test
	Go To                               ${SITE_URL}/series/5
