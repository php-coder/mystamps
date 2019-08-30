*** Settings ***
Documentation   Verify collection estimation scenarios
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Resource        ../../selenium.utils.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      collection  estimation  logic

*** Test Cases ***
Message should be shown when a collection is empty
	Go To                   ${SITE_URL}/collection/paid/estimation
	Element Text Should Be  id=empty-collection-msg  In this collection is no stamps

Series with its price should be taken into account
	[Tags]                       unstable
	Go To                        ${SITE_URL}/series/1
	Input Text                   id=paid-price  100
	Select From List By Value    id=paid-currency  ${expectedCurrency}
	Submit Form                  id=add-series-form
	Go To                        ${SITE_URL}/collection/paid/estimation
	Table Cell Should Contain    collection-estimation  row=2  column=2  text=100.00 ${expectedCurrency}
	# TODO: use "Table Footer Should Contain" instead, when it will be fixed.
	# See https://github.com/Hi-Fi/robotframework-seleniumlibrary-java/issues/88
	Table Header Should Contain  collection-estimation  100.00 ${expectedCurrency}

Series without price should be shown but not taken into account
	[Tags]                       unstable
	Go To                        ${SITE_URL}/series/2
	Submit Form                  id=add-series-form
	Go To                        ${SITE_URL}/collection/paid/estimation
	Table Cell Should Contain    collection-estimation  row=3  column=2  text=${EMPTY}
	# TODO: use "Table Footer Should Contain" instead, when it will be fixed.
	# See https://github.com/Hi-Fi/robotframework-seleniumlibrary-java/issues/88
	Table Header Should Contain  collection-estimation  100.00 ${expectedCurrency}

*** Keywords ***
Before Test Suite
	@{currencies}=                      Create List	USD  EUR  RUB  CZK
	${randomCurrency}=                  Evaluate  random.choice(${currencies})  modules=random  
	Set Suite Variable                  ${expectedCurrency}  ${randomCurrency}
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=paid  password=test
