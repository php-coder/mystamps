*** Settings ***
Documentation   Verify change language scenario
Library         SeleniumLibrary
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      main-page  lang  logic

*** Test Cases ***
Language should be changed after switching language
	Element Text Should Be  id:logo  My stamps
	Click Link              id:change-lang-link
	Element Text Should Be  id:logo  Мои марки

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
