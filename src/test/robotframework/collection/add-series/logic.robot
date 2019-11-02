*** Settings ***
Documentation    Verify add series to user's collection
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       collection  series  logic

*** Test Cases ***
Add a series to user's collection
	Go To                     ${SITE_URL}/series/2
	Element Text Should Be    id:number-of-stamps-block  I have 2 out of 2 stamps
	Submit Form               id:add-series-form
	Page Should Contain Link  css:[href="/series/2"]

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=seriesowner  password=test
