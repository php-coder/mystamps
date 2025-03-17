*** Settings ***
Documentation   Verify scenarios of adding a comment to a series
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Resource        ../../selenium.utils.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  add-comment  logic  htmx

*** Test Cases ***
Add a comment
	Input Text                        id:new-comment  A comment
	Submit Form                       id:add-comment-form
	Wait Until Page Contains Element  id:comment
	Element Text Should Be            id:comment  A comment

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test
	Go To                               ${SITE_URL}/series/4
