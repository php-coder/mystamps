*** Settings ***
Documentation    Verify miscellaneous aspects of series creation
Library          Collections
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       series  misc

*** Test Cases ***
Issue year should have options for range from 1840 to the current year
	Click Element                id:specify-issue-date-link
	${availableYears}=           Get List Items  id:year
	${currentYear}=              Get Time  year  NOW
	${numberOfYears}=            Get Length  ${availableYears}
	# +2 here is to include the current year and option with title
	${expectedNumberOfYears}=    Evaluate  ${currentYear}-1840+2
	List Should Contain Value    ${availableYears}  1840
	List Should Contain Value    ${availableYears}  ${currentYear}
	Should Be Equal As Integers  ${numberOfYears}   ${expectedNumberOfYears}

Catalog numbers should accept valid values
	[Template]  Valid Catalog Numbers Should Be Accepted
	7
	7,8
	71, 81, 91
	1000

Catalog numbers should be stripped from any spaces
	Go To                          ${SITE_URL}/series/add
	Disable Client Validation      add-series-form
	Click Element                  id:add-catalog-numbers-link
	Wait Until Element Is Visible  id:michelNumbers
	Input Text                     id:michelNumbers    ${SPACE * 2}1 , 2${SPACE * 2}
	Input Text                     id:scottNumbers     ${SPACE * 2}3 , 4${SPACE * 2}
	Input Text                     id:yvertNumbers     ${SPACE * 2}5 , 6${SPACE * 2}
	Input Text                     id:gibbonsNumbers   ${SPACE * 2}7 , 8${SPACE * 2}
	Input Text                     id:solovyovNumbers  ${SPACE * 2}9 , 10${SPACE * 2}
	Input Text                     id:zagorskiNumbers  ${SPACE * 2}11 , 12${SPACE * 2}
	Submit Form                    id:add-series-form
	Textfield Value Should Be      id:michelNumbers    1,2
	Textfield Value Should Be      id:scottNumbers     3,4
	Textfield Value Should Be      id:yvertNumbers     5,6
	Textfield Value Should Be      id:gibbonsNumbers   7,8
	Textfield Value Should Be      id:solovyovNumbers  9,10
	Textfield Value Should Be      id:zagorskiNumbers  11,12

Catalog numbers should ignore duplicate values
	[Tags]                     htmx
	Go To                      ${SITE_URL}/series/add
	Select From List By Label  id:category  Sport
	Input Text                 id:quantity  2
	Choose File                id:image  ${MAIN_RESOURCE_DIR}${/}test.png
	Click Element              id:add-catalog-numbers-link
	Input Text                 id:michelNumbers    104,105,104
	Input Text                 id:scottNumbers     114,115,114
	Input Text                 id:yvertNumbers     124,125,124
	Input Text                 id:gibbonsNumbers   134,135,134
	Input Text                 id:solovyovNumbers  144,145,144
	Input Text                 id:zagorskiNumbers  154,155,154
	Submit Form                id:add-series-form
	Element Text Should Be     id:michel_catalog_info    \#104, 105
	Element Text Should Be     id:scott_catalog_info     \#114, 115
	Element Text Should Be     id:yvert_catalog_info     \#124, 125
	Element Text Should Be     id:gibbons_catalog_info   \#134, 135
	Element Text Should Be     id:solovyov_catalog_info  \#144, 145
	Element Text Should Be     id:zagorski_catalog_info  \#154, 155

Catalog numbers should accept existing numbers
	[Tags]                     htmx
	Go To                      ${SITE_URL}/series/add
	Select From List By Label  id:category  Sport
	Input Text                 id:quantity  2
	Choose File                id:image  ${MAIN_RESOURCE_DIR}${/}test.png
	Click Element              id:add-catalog-numbers-link
	Input Text                 id:michelNumbers    99
	Input Text                 id:scottNumbers     99
	Input Text                 id:yvertNumbers     99
	Input Text                 id:gibbonsNumbers   99
	Input Text                 id:solovyovNumbers  77
	Input Text                 id:zagorskiNumbers  83
	Submit Form                id:add-series-form
	Element Text Should Be     id:michel_catalog_info    \#99
	Element Text Should Be     id:scott_catalog_info     \#99
	Element Text Should Be     id:yvert_catalog_info     \#99
	Element Text Should Be     id:gibbons_catalog_info   \#99
	Element Text Should Be     id:solovyov_catalog_info  \#77
	Element Text Should Be     id:zagorski_catalog_info  \#83

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test
	Go To                               ${SITE_URL}/series/add

Valid Catalog Numbers Should Be Accepted
	[Arguments]                      ${catalogNumbers}
	Go To                            ${SITE_URL}/series/add
	Disable Client Validation        add-series-form
	Click Element                    id:add-catalog-numbers-link
	Wait Until Element Is Visible    id:michelNumbers
	Input Text                       id:michelNumbers    ${catalogNumbers}
	Input Text                       id:scottNumbers     ${catalogNumbers}
	Input Text                       id:yvertNumbers     ${catalogNumbers}
	Input Text                       id:gibbonsNumbers   ${catalogNumbers}
	Input Text                       id:solovyovNumbers  ${catalogNumbers}
	Input Text                       id:zagorskiNumbers  ${catalogNumbers}
	Submit Form                      id:add-series-form
	Page Should Not Contain Element  id:michelNumbers.errors
	Page Should Not Contain Element  id:scottNumbers.errors
	Page Should Not Contain Element  id:yvertNumbers.errors
	Page Should Not Contain Element  id:gibbonsNumbers.errors
	Page Should Not Contain Element  id:solovyovNumbers.errors
	Page Should Not Contain Element  id:zagorskiNumbers.errors
