*** Settings ***
Documentation   Verify account activation scenarios
Library         Selenium2Library
Suite Setup     Before Test Suite
Suite Teardown  After Test Suite
Test Setup      Before Test
Force Tags      account  activation  logic

*** Test Cases ***
Activate account with full info (fill all fields)
	Input Text              id=login                 1st-test-login
	Input Text              id=name                  Test Suite
	Input Text              id=password              test-password
	Input Text              id=passwordConfirmation  test-password
	Input Text              id=activationKey         7777744444
	Submit Form             id=activate-account-form
	Location Should Be      ${SITE_URL}/account/auth
	Element Text Should Be  id=msg-success           Account successfully activated! Now you can pass authentication.

Activate account with only required info (fill only mandatory fields)
	Input Text              id=login                 2nd-test-login
	Input Text              id=name                  ${EMPTY}
	Input Text              id=password              test-password
	Input Text              id=passwordConfirmation  test-password
	Input Text              id=activationKey         4444477777
	Submit Form             id=activate-account-form
	Location Should Be      ${SITE_URL}/account/auth
	Element Text Should Be  id=msg-success           Account successfully activated! Now you can pass authentication.

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

Before Test
	Go To  ${SITE_URL}/account/activate

After Test Suite
	Close Browser
