*** Settings ***
Documentation   Verify that togglz works
Library         SeleniumLibrary
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      togglz  misc

*** Test Cases ***
Extra characters should never be shown if Togglz works
	Page Should Not Contain Element  id=always-disabled-element

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
