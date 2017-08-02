*** Settings ***
Documentation    Verify participant creation scenarios
Library          Collections
Library          Selenium2Library
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       participant  logic

*** Test Cases ***
Create participant with name only
	[Documentation]            Verify creation of participant by filling only mandatory fields
	Input Text                 id=name  participant1
	Submit Form                id=add-participant-form
	Location Should Be         ${SITE_URL}/
	Go To                      ${SITE_URL}/series/1
	${availableSellers}=       Get List Items  id=seller
	${availableBuyers}=        Get List Items  id=buyer
	List Should Contain Value  ${availableSellers}  participant1
	List Should Contain Value  ${availableBuyers}   participant1

Create participant with name and url
	[Documentation]            Verify creation of participant by filling all fields
	Input Text                 id=name  participant2
	Input Text                 id=url   http://participant2.example.org
	Submit Form                id=add-participant-form
	Location Should Be         ${SITE_URL}/
	Go To                      ${SITE_URL}/series/1
	${availableSellers}=       Get List Items  id=seller
	${availableBuyers}=        Get List Items  id=buyer
	List Should Contain Value  ${availableSellers}  participant2
	List Should Contain Value  ${availableBuyers}   participant2

*** Keywords ***
Before Test Suite
	[Documentation]                     Open browser, register fail hook and login as admin
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test

Before Test
	[Documentation]  Open add participant page
	Go To            ${SITE_URL}/participant/add

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
