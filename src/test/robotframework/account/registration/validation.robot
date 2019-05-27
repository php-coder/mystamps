*** Settings ***
Documentation   Verify account registration validation scenarios
Library         SeleniumLibrary
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      account  registration  validation

*** Test Cases ***
Register account with too long email
	${anyCharacter}=        Set Variable  0
	Input Text              id=email  ${anyCharacter * 255}@mail.ru
	Submit Form             id=register-account-form
	Element Text Should Be  id=email.errors  Value is greater than allowable maximum of 255 characters

Register account with invalid email
	[Template]  Invalid Email Should Be Rejected
	login
	login@domain

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/register  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

Invalid Email Should Be Rejected
	[Arguments]             ${email}
	Input Text              id=email  ${email}
	Submit Form             id=register-account-form
	Element Text Should Be  id=email.errors  Invalid e-mail address
