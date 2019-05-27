*** Settings ***
Documentation   Verify account activation validation scenarios
Library         SeleniumLibrary
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
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

Activate account with repetition of the special characters in login
	[Template]  Login should not contain repeated special characters
	te__st
	te--st
	te..st
	te_-st
	te-._st

Activate account with too long name
	${letter}=              Set Variable    j
	Input Text              id=name         ${letter * 101}
	Submit Form             id=activate-account-form
	Element Text Should Be  id=name.errors  Value is greater than allowable maximum of 100 characters

Activate account with with forbidden characters in name
	Input Text              id=name         M@st3r_
	Submit Form             id=activate-account-form
	Element Text Should Be  id=name.errors  Name must consist only letters, hyphen or spaces

Activate account with name that starts with hyphen
	Input Text              id=name         -test
	Submit Form             id=activate-account-form
	Element Text Should Be  id=name.errors  Value must not start or end with hyphen

Activate account with name that ends with hyphen
	Input Text              id=name         test-
	Submit Form             id=activate-account-form
	Element Text Should Be  id=name.errors  Value must not start or end with hyphen

Activate account with too short password
	Input Text              id=password         123
	Submit Form             id=activate-account-form
	Element Text Should Be  id=password.errors  Value is less than allowable minimum of 4 characters

Activate account with too long password
	${letter}=              Set Variable        j
	Input Text              id=password         ${letter * 73}
	Submit Form             id=activate-account-form
	Element Text Should Be  id=password.errors  Value is greater than allowable maximum of 72 characters

Activate account with too short activation key
	Input Text              id=activationKey         12345
	Submit Form             id=activate-account-form
	Element Text Should Be  id=activationKey.errors  Value length must be equal to 10 characters

Activate account with too long activation key
	Input Text              id=activationKey         1234567890123
	Submit Form             id=activate-account-form
	Element Text Should Be  id=activationKey.errors  Value length must be equal to 10 characters

Activate account with forbidden characters in activation key
	Input Text              id=activationKey         A123=+TEST
	Submit Form             id=activate-account-form
	Element Text Should Be  id=activationKey.errors  Key must consist only latin letters in lower case or digits

Activate account with wrong activation key
	Input Text              id=activationKey         1112223334
	Submit Form             id=activate-account-form
	Element Text Should Be  id=activationKey.errors  Invalid activation key

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/activate  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

Login should not contain repeated special characters
	[Arguments]             ${login}
	Input Text              id=login         ${login}
	Submit Form             id=activate-account-form
	Element Text Should Be  id=login.errors  Login must not contain repetition of hyphen, dot or underscore
