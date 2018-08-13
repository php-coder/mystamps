*** Settings ***
Documentation    Verify access to category related pages (including non-existing)
Library          Selenium2Library
Resource         ../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       category  access

*** Test Cases ***
Anonymous user cannot create category
	Go To                   ${SITE_URL}/category/add
	Element Text Should Be  id=error-code  403
	Element Text Should Be  id=error-msg  Forbidden

Opening a page of non-existing category show an error
	Go To                             ${SITE_URL}/category/category-404-error-test
	Element Text Should Be            id=error-code  404
	Element Text Should Match Regexp  id=error-msg   Requested page[\\n\\r]+not found

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

After Test Suite
	Close Browser
