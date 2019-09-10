*** Settings ***
Documentation    Verify miscellaneous aspects of account activation from anonymous user
Library          SeleniumLibrary
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Test Setup       Disable Client Validation
Force Tags       account  activation  misc

*** Test Cases ***
Activation key should be auto filled from url
	Go To                      ${SITE_URL}/account/activate?key=7777755555
	Textfield Value Should Be  id=activationKey  7777755555

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

Name with allowed characters should be accepted
	[Template]  Name should not cause an error
	x
	Slava Se-mushin
	Семён Якушев

Name should be striped from leading and trailing spaces
	Input Text                 id=name  ${SPACE * 2}test${SPACE * 2}
	Submit Form                id=activate-account-form
	Textfield Value Should Be  id=name  test

Most short password should be accepted
	Input Text                       id=password  1234
	Submit Form                      id=activate-account-form
	Page Should Not Contain Element  id=password.errors

Password with allowed characters should be accepted
	Input Text                       id=password  t3s7-T_E_S_T
	Submit Form                      id=activate-account-form
	Page Should Not Contain Element  id=password.errors

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/activate  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

Disable Client Validation
	Remove Element Attribute  password              required
	Remove Element Attribute  passwordConfirmation  required

Name should not cause an error
	[Arguments]                      ${name}
	Disable Client Validation
	Input Text                       id=name  ${name}
	Submit Form                      id=activate-account-form
	Page Should Not Contain Element  id=name.errors
