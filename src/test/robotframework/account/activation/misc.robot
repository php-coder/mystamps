*** Settings ***
Documentation    Verify miscellaneous aspects of account activation
Library          Selenium2Library
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

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Go To                               ${SITE_URL}/account/activate

After Test Suite
	Close Browser
