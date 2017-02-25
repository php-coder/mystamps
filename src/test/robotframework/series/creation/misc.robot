*** Settings ***
Documentation    Verify miscellaneous aspects of series creation
Library          Selenium2Library
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  misc

*** Test Cases ***
Catalog numbers should accept valid values
	[Documentation]  Verify that fields with catalog numbers accept valid values
	[Template]       Valid Catalog Numbers Should Be Accepted
	7
	7,8
	71, 81, 91
	1000

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

Valid Catalog Numbers Should Be Accepted
	[Documentation]                  Test that specifying catalog numbers don't cause an error
	[Arguments]                      ${catalogNumbers}
	Click Element                    id=add-catalog-numbers-link
	Input Text                       id=michelNumbers  ${catalogNumbers}
	Input Text                       id=scottNumbers  ${catalogNumbers}
	Input Text                       id=yvertNumbers  ${catalogNumbers}
	Input Text                       id=gibbonsNumbers  ${catalogNumbers}
	Submit Form                      id=add-series-form
	Page Should Not Contain Element  id=michelNumbers.errors
	Page Should Not Contain Element  id=scottNumbers.errors
	Page Should Not Contain Element  id=yvertNumbers.errors
	Page Should Not Contain Element  id=gibbonsNumbers.errors
