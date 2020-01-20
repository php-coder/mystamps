*** Settings ***
Documentation   Verify miscellaneous aspects of account registration from anonymous user
Library         SeleniumLibrary
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      account  registration  misc

*** Test Cases ***
Email should be striped from leading and trailing spaces
	Input Text                 id:email  ${SPACE * 2}test${SPACE * 2}
	Submit Form                id:register-account-form
	Textfield Value Should Be  id:email  test

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/register  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

