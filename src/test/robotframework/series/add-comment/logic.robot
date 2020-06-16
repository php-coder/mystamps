*** Settings ***
Documentation   Verify scenarios of adding a comment to a series
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  add-comment  logic

*** Test Cases ***
Add a comment
	Input Text                        id:comment  A comment
	Submit Form                       id:add-comment-form
	Wait Until Page Does Not Contain  id:add-comment-form
	Wait Until Element Value Is       comment  A comment

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/4
