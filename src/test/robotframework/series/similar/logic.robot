*** Settings ***
Documentation   Verify logic for similar series
Library         SeleniumLibrary
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  similar-series  logic

*** Test Cases ***
Similar series should be linked to each other
	Go To                        ${SITE_URL}/series/4
	Page Should Contain Element  css:#similar-series [href="/series/5"]
	Go To                        ${SITE_URL}/series/5
	Page Should Contain Element  css:#similar-series [href="/series/4"]

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
