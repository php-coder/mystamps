*** Settings ***
Documentation    Verify access to category related pages (including non-existing)
Library          Selenium2Library
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       category  access

*** Test Cases ***
Anonymous user cannot create category
	[Documentation]         Verify that anonymous user gets 403 error
	Go To                   ${SITE_URL}/category/add
	Element Text Should Be  id=error-code  403
	Element Text Should Be  id=error-msg  Forbidden

Opening a page of non-existing category show an error
	[Documentation]         Verify that access to non-existing category returns 404 error
	Go To                   ${SITE_URL}/category/category-404-error-test
	Element Text Should Be  id=error-code  404
	Element Text Should Be  id=error-msg  Requested page${\n}not found

*** Keywords ***
Before Test Suite
	[Documentation]                     Open browser and register fail hook
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

After Test Suite
	[Documentation]  Close browser
	Close Browser
