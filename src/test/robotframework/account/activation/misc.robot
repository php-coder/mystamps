*** Settings ***
Documentation    Verify miscellaneous aspects of account activation
Library          SeleniumLibrary
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       account  activation  misc

*** Test Cases ***
Activation key should be auto filled from url
	Go To                      ${SITE_URL}/account/activate?key=7777744444
	Textfield Value Should Be  id=activationKey  7777744444

Most short login should be accepted
	Input Text                       id=login  ab
	Submit Form                      id=activate-account-form
	Page Should Not Contain Element  id=login.errors

Most long login should be accepted
	Input Text                       id=login  abcde1234567890
	Submit Form                      id=activate-account-form
	Page Should Not Contain Element  id=login.errors

Login with allowed characters should be accepted
	Input Text                       id=login  t.3.s.7-T_E_S_T
	Submit Form                      id=activate-account-form
	Page Should Not Contain Element  id=login.errors

Login should be striped from leading and trailing spaces
	Input Text                 id=login  ${SPACE * 2}testLogin${SPACE * 2}
	Submit Form                id=activate-account-form
	Textfield Value Should Be  id=login  testLogin

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Go To                               ${SITE_URL}/account/activate

After Test Suite
	Close Browser
