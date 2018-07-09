*** Settings ***
Documentation   Verify account activation validation scenarios
Library         Selenium2Library
Suite Setup     Before Test Suite
Suite Teardown  After Test Suite
Force Tags      account  activation  validation

*** Test Cases ***
Activate account with matching login and password
	Input Text              id=login            admin
	Input Text              id=password         admin
	Submit Form             id=activate-account-form
	Element Text Should Be  id=password.errors  Password and login must be different

Activate account with mismatching password and password confirmation
	Input Text              id=password                     password123
	Input Text              id=passwordConfirmation         password321
	Submit Form             id=activate-account-form
	Element Text Should Be  id=passwordConfirmation.errors  Password mismatch

Activate account with too short login
	Input Text              id=login         a
	Submit Form             id=activate-account-form
	Element Text Should Be  id=login.errors  Value is less than allowable minimum of 2 characters

Activate account with too long login
	Input Text              id=login         abcde12345fghkl6
	Submit Form             id=activate-account-form
	Element Text Should Be  id=login.errors  Value is greater than allowable maximum of 15 characters

Activate account with forbidden characters in login
	Input Text              id=login         't@$t'
	Submit Form             id=activate-account-form
	Element Text Should Be  id=login.errors  Login must consist only latin letters, digits, dot, hyphen or underscore

Activate account with existing login
	Input Text              id=login         coder
	Submit Form             id=activate-account-form
	Element Text Should Be  id=login.errors  Login already exists

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Go To                               ${SITE_URL}/account/activate

After Test Suite
	Close Browser
