*** Settings ***
Documentation    Verify series removal from a user's collection
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       collection  series  logic

*** Test Cases ***
Remove a series from user's collection
	Go To                         ${SITE_URL}/series/3
	Submit Form                   id:remove-series-form
	Page Should Not Contain Link  css:[href="/series/3"]

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=seriesowner  password=test
