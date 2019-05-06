*** Settings ***
Documentation    Verify miscellaneous aspects of series creation
Library          Collections
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  misc

*** Test Cases ***
Issue year should have options for range from 1840 to the current year
	Click Element                id=specify-issue-date-link
	${availableYears}=           Get List Items  id=year
	${currentYear}=              Get Time  year  NOW
	${numberOfYears}=            Get Length  ${availableYears}
	# +2 here is to include the current year and option with title
	${expectedNumberOfYears}=    Evaluate  ${currentYear}-1840+2
	List Should Contain Value    ${availableYears}  1840
	List Should Contain Value    ${availableYears}  ${currentYear}
	Should Be Equal As Integers  ${numberOfYears}   ${expectedNumberOfYears}

Catalog numbers should accept valid values
	[Tags]      unstable
	[Template]  Valid Catalog Numbers Should Be Accepted
	7
	7,8
	71, 81, 91
	1000

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test
	Go To                               ${SITE_URL}/series/add

After Test Suite
	Log Out
	Close Browser

Valid Catalog Numbers Should Be Accepted
	[Arguments]                      ${catalogNumbers}
	Click Element                    id=add-catalog-numbers-link
	Input Text                       id=michelNumbers    ${catalogNumbers}
	Input Text                       id=scottNumbers     ${catalogNumbers}
	Input Text                       id=yvertNumbers     ${catalogNumbers}
	Input Text                       id=gibbonsNumbers   ${catalogNumbers}
	Input Text                       id=solovyovNumbers  ${catalogNumbers}
	Input Text                       id=zagorskiNumbers  ${catalogNumbers}
	Submit Form                      id=add-series-form
	Page Should Not Contain Element  id=michelNumbers.errors
	Page Should Not Contain Element  id=scottNumbers.errors
	Page Should Not Contain Element  id=yvertNumbers.errors
	Page Should Not Contain Element  id=gibbonsNumbers.errors
	Page Should Not Contain Element  id=solovyovNumbers.errors
	Page Should Not Contain Element  id=zagorskiNumbers.errors
