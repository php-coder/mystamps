*** Settings ***
Documentation    Verify participant creation scenarios
Library          Collections
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       participant  logic

*** Test Cases ***
Create participant with name only (fill only mandatory fields)
	Input Text                     id=name  participant1
	Select Checkbox                id=seller
	Submit Form                    id=add-participant-form
	Location Should Be             ${SITE_URL}/
	Go To                          ${SITE_URL}/series/1
	${availableSellers}=           Get List Items  id=seller
	${availableBuyers}=            Get List Items  id=buyer
	List Should Contain Value      ${availableSellers}  participant1
	List Should Not Contain Value  ${availableBuyers}   participant1

Create participant with full info (fill all fields)
	Input Text                 id=name  participant2
	Select From List By Label  id=group  example.com
	Select Checkbox            id=buyer
	Select Checkbox            id=seller
	Input Text                 id=url   http://participant2.example.org
	Submit Form                id=add-participant-form
	Location Should Be         ${SITE_URL}/
	Go To                      ${SITE_URL}/series/1
	# TODO: check that buyer and seller listed in the "example.com" group
	${availableSellers}=       Get List Items  id=seller
	${availableBuyers}=        Get List Items  id=buyer
	List Should Contain Value  ${availableSellers}  participant2
	List Should Contain Value  ${availableBuyers}   participant2

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test

Before Test
	Go To  ${SITE_URL}/participant/add

After Test Suite
	Log Out
	Close Browser
