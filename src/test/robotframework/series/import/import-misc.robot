*** Settings ***
Documentation   Miscellaneous aspects of a series import
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  import-series  misc

*** Test Cases ***
Seller info should be visible where only price and currency have been extracted
	Go To                      ${SITE_URL}/series/import/request/3
	Element Text Should Be     id:request-url  http://example.com/issue/1232
	Element Should Be Visible  id:seller-group
	Element Should Be Visible  id:seller-name
	Element Should Be Visible  id:seller-url

Seller info should be invisible where seller id has been extracted
	Go To                          ${SITE_URL}/series/import/request/4
	List Selection Should Be       id:seller  gh1232
	Element Should Not Be Visible  id:seller-group
	Element Should Not Be Visible  id:seller-name
	Element Should Not Be Visible  id:seller-url

Alternative price and currency should be invisible when they are empty
	Go To                          ${SITE_URL}/series/import/request/5
	Element Text Should Be         id:request-url  http://example.com/issue/1279
	Textfield Value Should Be      id:price        100
	List Selection Should Be       id:currency     RUB
	Element Should Not Be Visible  id:alt-price
	Element Should Not Be Visible  id:alt-currency

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
