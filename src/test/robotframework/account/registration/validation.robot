*** Settings ***
Documentation   Verify account registration validation scenarios
Library         SeleniumLibrary
Suite Setup     Before Test Suite
Suite Teardown  After Test Suite
Force Tags      account  registration  validation

*** Test Cases ***
Register account with too long email
	${anyCharacter}=        Set Variable  0
	Input Text              id=email  ${anyCharacter * 255}@mail.ru
	Submit Form             id=register-account-form
	Element Text Should Be  id=email.errors  Value is greater than allowable maximum of 255 characters

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/register  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

After Test Suite
	Close Browser
