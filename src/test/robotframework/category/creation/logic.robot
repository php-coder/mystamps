*** Settings ***
Documentation    Verify category creation scenarios
Library          Collections
Library          Selenium2Library
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       category  logic

*** Test Cases ***
Create category with name in English
	[Documentation]            Verify creation of category by filling only mandatory fields
	Input Text                 id=name  Cars
	Submit Form                id=add-category-form
	Location Should Be         ${SITE_URL}/category/cars
	Element Text Should Be     id=page-header  Cars
	Go To                      ${SITE_URL}/series/add
	${availableCategories}=    Get List Items  id=category
	List Should Contain Value  ${availableCategories}  Cars
	# TODO: verify that after changing language, header will be in English

Create category with name in English and Russian
	[Documentation]         Verify creation of category by specifying names in 2 languages
	Input Text              id=name  Space
	Input Text              id=nameRu  Космос
	Submit Form             id=add-category-form
	Location Should Be      ${SITE_URL}/category/space
	Element Text Should Be  id=page-header  Space
	# TODO: verify that after changing language, header will be in Russian

*** Keywords ***
Before Test Suite
	[Documentation]                     Open browsers, register fail hook and login as admin
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test

Before Test
	[Documentation]  Open create category page
	Go To            ${SITE_URL}/category/add

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
