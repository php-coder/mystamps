*** Settings ***
Documentation    Verify series creation validation scenarios from a user
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Test Setup       Disable Client Validation
Force Tags       series  validation

*** Test Cases ***
Create series with empty required fields
	Submit Form                      id=add-series-form
	Element Text Should Be           id=category.errors  Value must not be empty
	Element Text Should Be           id=quantity.errors  Value must not be empty
	Element Text Should Be           id=image.errors     Value must not be empty
	Page Should Not Contain Element  id=image-url.errors

Create series with non-numeric quantity
	Input Text              id=quantity  NaN
	Submit Form             id=add-series-form
	Element Text Should Be  id=quantity.errors  Invalid value

Create series with too small quantity
	Input Text              id=quantity  0
	Submit Form             id=add-series-form
	Element Text Should Be  id=quantity.errors  Value must be greater than or equal to 1

Create series with too large quantity
	Input Text              id=quantity  51
	Submit Form             id=add-series-form
	Element Text Should Be  id=quantity.errors  Value must be less than or equal to 50

Create series with an empty image
	Choose File             id=image  ${TEST_RESOURCE_DIR}${/}empty.jpg
	Submit Form             id=add-series-form
	Element Text Should Be  id=image.errors  File must not be empty

Catalog numbers should reject invalid values
	[Tags]      unstable
	[Template]  Invalid Catalog Numbers Should Be Rejected
	t
	t,t
	,1
	1,
	1,,2
	0
	05
	1,09
	10000

Catalog price should reject invalid values
	[Tags]      unstable
	[Template]  Invalid Catalog Price Should Be Rejected
	0
	-1
	NaN

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=coder  password=test
	Go To                               ${SITE_URL}/series/add

Disable Client Validation
	Remove Element Attribute  quantity  required
	Remove Element Attribute  image     required

Invalid Catalog Numbers Should Be Rejected
	[Arguments]                    ${catalogNumbers}
	# open page each time to be sure that we're starting from the clean state.
	# Otherwise it's possible that there errors from the previous test and when
	# we'll click on link for adding catalog numbers then fields become
	# invisible (because link is toggling the visibility and when there are
	# errors, fields are visible from the begining).
	Go To                          ${SITE_URL}/series/add
	Click Element                  id=add-catalog-numbers-link
	# we should wait until all 4 fields with class js-catalogs-info will be
	# visible but for simplicity we just check that the last field is visible
	Wait Until Element Is Visible  id=gibbonsNumbers
	Input Text                     id=michelNumbers    ${catalogNumbers}
	Input Text                     id=scottNumbers     ${catalogNumbers}
	Input Text                     id=yvertNumbers     ${catalogNumbers}
	Input Text                     id=gibbonsNumbers   ${catalogNumbers}
	Input Text                     id=solovyovNumbers  ${catalogNumbers}
	Input Text                     id=zagorskiNumbers  ${catalogNumbers}
	Submit Form                    id=add-series-form
	${alnumMessage}                Catenate  SEPARATOR=${SPACE}
	...                            Value must be a list of numbers separated by comma.
	...                            Any number may end with a latin letter in lower case
	Element Text Should Be         id=michelNumbers.errors    Value must be a list of numbers separated by comma
	Element Text Should Be         id=scottNumbers.errors     ${alnumMessage}
	Element Text Should Be         id=yvertNumbers.errors     Value must be a list of numbers separated by comma
	Element Text Should Be         id=gibbonsNumbers.errors   Value must be a list of numbers separated by comma
	Element Text Should Be         id=solovyovNumbers.errors  Value must be a list of numbers separated by comma
	Element Text Should Be         id=zagorskiNumbers.errors  Value must be a list of numbers separated by comma

Invalid Catalog Price Should Be Rejected
	[Arguments]                    ${catalogPrice}
	# open page each time to be sure that we're starting from the clean state.
	# Otherwise it's possible that there errors from the previous test and when
	# we'll click on link for adding catalog numbers then fields become
	# invisible (because link is toggling the visibility and when there are
	# errors, fields are visible from the begining).
	Go To                          ${SITE_URL}/series/add
	Click Element                  id=add-catalog-numbers-link
	# we should wait until all 4 fields with class js-catalogs-info will be
	# visible but for simplicity we just check that the last field is visible
	Wait Until Element Is Visible  id=gibbonsPrice
	Input Text                     id=michelPrice    ${catalogPrice}
	Input Text                     id=scottPrice     ${catalogPrice}
	Input Text                     id=yvertPrice     ${catalogPrice}
	Input Text                     id=gibbonsPrice   ${catalogPrice}
	Input Text                     id=solovyovPrice  ${catalogPrice}
	Input Text                     id=zagorskiPrice  ${catalogPrice}
	Submit Form                    id=add-series-form
	Element Text Should Be         id=michelPrice.errors    Invalid value
	Element Text Should Be         id=scottPrice.errors     Invalid value
	Element Text Should Be         id=yvertPrice.errors     Invalid value
	Element Text Should Be         id=gibbonsPrice.errors   Invalid value
	Element Text Should Be         id=solovyovPrice.errors  Invalid value
	Element Text Should Be         id=zagorskiPrice.errors  Invalid value
