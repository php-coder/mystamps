*** Settings ***
Documentation    Verify series removal from a user's collection
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       collection  series  logic

*** Test Cases ***
Remove the first instance of a series from user's collection
	Go To                       ${SITE_URL}/series/3
	Xpath Should Match X Times  xpath://input[@value="Remove from collection"]  2
	# Submit the first form
	Submit Form                 css:.remove-series-form
	Page Should Contain Link    css:[href="/series/3"]
	# See https://developer.mozilla.org/en-US/docs/Web/CSS/General_sibling_combinator
	Element Text Should Be      css:[href="/series/3"] ~ .label-default  2 out of 3

Remove the last instance of a series from user's collection
	Go To                         ${SITE_URL}/series/3
	Xpath Should Match X Times    xpath://input[@value="Remove from collection"]  1
	Element Text Should Be        css:.remove-series-form .label-default  2 out of 3
	Submit Form                   css:.remove-series-form
	Page Should Not Contain Link  css:[href="/series/3"]

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=seriesowner  password=test
