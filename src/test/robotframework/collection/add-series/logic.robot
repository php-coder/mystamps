*** Settings ***
Documentation    Verify add series to user's collection
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       collection  series  logic

*** Test Cases ***
Add a series to user's collection (all stamps)
	Go To                     ${SITE_URL}/series/2
	Element Text Should Be    id:number-of-stamps-block  I have 2 out of 2 stamps
	Submit Form               id:add-series-form
	Page Should Contain Link  css:[href="/series/2"]
	# See https://developer.mozilla.org/en-US/docs/Web/CSS/General_sibling_combinator
	Element Text Should Be    css:[href="/series/2"] ~ .label-success  New

Add a series to user's collection (incomplete series)
	Go To                     ${SITE_URL}/series/4
	Input Text                id:number-of-stamps  2
	Submit Form               id:add-series-form
	Page Should Contain Link  css:[href="/series/4"]
	Element Text Should Be    css:[href="/series/4"] ~ .label-success  New
	# See https://developer.mozilla.org/en-US/docs/Web/CSS/General_sibling_combinator
	Element Text Should Be    css:[href="/series/4"] ~ .label-default  2 out of 4

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=seriesowner  password=test
