*** Settings ***
Documentation   Validation of a series import
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  import-series  validation  htmx

*** Test Cases ***
Price and currency should be optional when seller information isn't provided
	Go To                      ${SITE_URL}/series/import/request/2
	Textfield Value Should Be  id:seller-name    Issue 1256
	Input Text                 id:seller-name    ${EMPTY}
	Textfield Value Should Be  id:seller-url     http://example.com/issue/1256
	Input Text                 id:seller-url     ${EMPTY}
	Submit Form                id:create-series-form
	Element Text Should Be     id:category_name  Prehistoric animals

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
