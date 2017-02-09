*** Settings ***
Documentation    Verify series creation validation scenarios
Library          Selenium2Library
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  validation

*** Test Cases ***
Create series with non-numeric quantity
	[Documentation]         Verify validation of non-numeric quantity
	Input Text              id=quantity  NaN
	Submit Form             id=add-series-form
	Element Text Should Be  id=quantity.errors  Invalid value

Create series with quantity that is less than 1
	[Documentation]         Verify validation of too small quantity
	Input Text              id=quantity  0
	Submit Form             id=add-series-form
	Element Text Should Be  id=quantity.errors  Value must be greater than or equal to 1

Create series with quantity that is greater than 50
	[Documentation]         Verify validation of too large quantity
	Input Text              id=quantity  51
	Submit Form             id=add-series-form
	Element Text Should Be  id=quantity.errors  Value must be less than or equal to 50

Create series with too long comment
	[Documentation]                Verify validation of too long comment
	${letter}=                     Set Variable  x
	Click Element                  id=add-comment-link
	Wait Until Element Is Visible  id=comment
	Input Text                     id=comment  ${letter * 256}
	Submit Form                    id=add-series-form
	Element Text Should Be         id=comment.errors  Value is greater than allowable maximum of 255 characters

*** Keywords ***
Before Test Suite
	[Documentation]                     Login as admin and go to create series page
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/add

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser

Log In As
	[Documentation]  Log in as a user
	[Arguments]      ${login}  ${password}
	Go To            ${SITE_URL}/account/auth
	Input Text       id=login  ${login}
	Input Password   id=password  ${password}
	Submit Form      id=auth-account-form

Log Out
	[Documentation]  Log out current user
	Submit Form      id=logout-form
