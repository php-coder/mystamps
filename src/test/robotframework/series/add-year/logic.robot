*** Settings ***
Documentation   Verify scenarios of adding a release year to a series
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  add-year  logic  htmx

*** Test Cases ***
Add a release year
	Select From List By Value         id:release-year  1995
	Submit Form                       id:add-release-year-form
	Wait Until Page Does Not Contain  id:add-release-year-form
	Wait Until Page Contains Element  id:issue_date
	Element Text Should Be            id:issue_date  1995

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/4
