*** Settings ***
Documentation   Verify scenarios of adding catalog numbers to a series
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  add-numbers  logic  htmx

*** Test Cases ***
Add catalog numbers
	[Template]  Add numbers
	michel      10-12,100  10-12, 100
	scott       20-22,200  20-22, 200
	yvert       30-32,300  30-32, 300
	gibbons     40-42,400  40-42, 400
	solovyov    50-52,500  50-52, 500
	zagorski    60-62,600  60-62, 600

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/5

Add numbers
	[Arguments]                       ${catalog}  ${numbers}  ${expectedNumbers}
	Wait Until Page Contains Element  id:add-catalog-numbers-form
	Select From List By Value         id:catalog-name  ${catalog}
	Input Text                        id:catalog-numbers  ${numbers}
	Submit Form                       id:add-catalog-numbers-form
	Wait Until Page Contains Element  id:${catalog}_catalog_info
	Element Text Should Be            id:${catalog}_catalog_info  \#${expectedNumbers}
