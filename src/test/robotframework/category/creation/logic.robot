*** Settings ***
Documentation    Verify category creation scenarios
Library          Collections
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Test Setup       Before Test
Force Tags       category  logic

*** Test Cases ***
Create category with name in English (fill only mandatory fields)
	Input Text                        id:name  Cars
	Submit Form                       id:add-category-form
	Location Should Be                ${SITE_URL}/category/cars
	Element Text Should Be            id:page-header  Cars
	Element Text Should Match Regexp  id:msg-success  Category has been added\.[\\n\\r]+Now you could proceed with creating series\.
	Go To                             ${SITE_URL}/series/add
	${availableCategories}=           Get List Items  id:category
	List Should Contain Value         ${availableCategories}  Cars
	# FIXME: verify that after changing language, header will be in English

Create category with name in English and Russian
	Input Text              id:name  Space
	Input Text              id:nameRu  Космос
	Submit Form             id:add-category-form
	Location Should Be      ${SITE_URL}/category/space
	Element Text Should Be  id:page-header  Space
	Go To                   ${SITE_URL}/category/space?lang=ru
	Element Text Should Be  id:page-header  Космос

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test

Before Test
	Go To  ${SITE_URL}/category/add

