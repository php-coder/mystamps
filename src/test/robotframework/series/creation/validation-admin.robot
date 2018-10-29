*** Settings ***
Documentation    Verify series creation validation scenarios from admin
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       series  validation

*** Test Cases ***
Create series with empty required fields
	Submit Form                       id=add-series-form
	Wait Until Page Contains Element  id=category.errors
	Element Text Should Be            id=category.errors   Value must not be empty
	Element Text Should Be            id=quantity.errors   Value must not be empty
	Element Text Should Be            id=image.errors      Image or image URL must be specified
	Element Text Should Be            id=image-url.errors  Image or image URL must be specified

Create series with non-numeric quantity
	Set Input Type              quantity  text
	Input Text                  id=quantity  NaN
	Submit Form                 id=add-series-form
	Wait Until Element Text Is  quantity.errors  Invalid value

Create series with too small quantity
	Input Text                  id=quantity  0
	Submit Form                 id=add-series-form
	Wait Until Element Text Is  quantity.errors  Value must be greater than or equal to 1

Create series with too large quantity
	Input Text                  id=quantity  51
	Submit Form                 id=add-series-form
	Wait Until Element Text Is  quantity.errors  Value must be less than or equal to 50

Create series with an empty image
	Choose File                 id=image  ${TEST_RESOURCE_DIR}${/}empty.jpg
	Submit Form                 id=add-series-form
	Wait Until Element Text Is  image.errors  File must not be empty

Create series with both image and an image URL
	Choose File                 id=image      ${MAIN_RESOURCE_DIR}${/}test.png
	Input Text                  id=image-url  ${SITE_URL}/image/1
	Submit Form                 id=add-series-form
	Wait Until Element Text Is  image.errors  Image or image URL must be specified
	Element Text Should Be      id=image-url.errors  Image or image URL must be specified

Create series with invalid image URL
	Input Text                  id=image-url  invalid-url
	Submit Form                 id=add-series-form
	Wait Until Element Text Is  image-url.errors  Value must be a valid URL

Create series with image URL with invalid response
	Input Text                  id=image-url  ${MOCK_SERVER}/series/response-400
	Submit Form                 id=add-series-form
	Wait Until Element Text Is  image-url.errors  Could not download file

Create series with image URL to a file that does not exist
	Input Text                  id=image-url  ${MOCK_SERVER}/series/response-404
	Submit Form                 id=add-series-form
	Wait Until Element Text Is  image-url.errors  File not found

Create series with image URL that causes a redirect
	Input Text                  id=image-url  ${MOCK_SERVER}/series/response-301
	Submit Form                 id=add-series-form
	Wait Until Element Text Is  image-url.errors  URL must not redirect to another address

Create series with image URL to an empty file
	Input Text                  id=image-url  ${MOCK_SERVER}/series/empty-jpeg-file
	Submit Form                 id=add-series-form
	Wait Until Element Text Is  image-url.errors  File must not be empty

Create series with image URL to a file of unsupported type (not an image)
	Input Text                  id=image-url  ${MOCK_SERVER}/series/not-image-file
	Submit Form                 id=add-series-form
	Wait Until Element Text Is  image-url.errors  Invalid file type

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

Create series with too long comment
	${letter}=                     Set Variable  x
	Click Element                  id=add-comment-link
	Wait Until Element Is Visible  id=comment
	Input Text                     id=comment  ${letter * 256}
	Submit Form                    id=add-series-form
	Wait Until Element Text Is     comment.errors  Value is greater than allowable maximum of 255 characters

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/add

After Test Suite
	Log Out
	Close Browser

# remove "required" attributes from mandatory fields to be able to test server validation
Before Test
	Remove Element Attribute  category  required
	Remove Element Attribute  quantity  required

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
	Input Text                     id=michelNumbers  ${catalogNumbers}
	Input Text                     id=scottNumbers  ${catalogNumbers}
	Input Text                     id=yvertNumbers  ${catalogNumbers}
	Input Text                     id=gibbonsNumbers  ${catalogNumbers}
	Submit Form                    id=add-series-form
	${alnumMessage}                Catenate  SEPARATOR=${SPACE}
	...                            Value must be a list of numbers separated by comma.
	...                            Any number may end with a latin letter in lower case
	Wait Until Element Text Is     michelNumbers.errors      Value must be a list of numbers separated by comma
	Element Text Should Be         id=scottNumbers.errors    ${alnumMessage}
	Element Text Should Be         id=yvertNumbers.errors    Value must be a list of numbers separated by comma
	Element Text Should Be         id=gibbonsNumbers.errors  Value must be a list of numbers separated by comma

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
	Input Text                     id=michelPrice  ${catalogPrice}
	Input Text                     id=scottPrice  ${catalogPrice}
	Input Text                     id=yvertPrice  ${catalogPrice}
	Input Text                     id=gibbonsPrice  ${catalogPrice}
	Submit Form                    id=add-series-form
	Wait Until Element Text Is     michelPrice.errors  Invalid value
	Element Text Should Be         id=scottPrice.errors  Invalid value
	Element Text Should Be         id=yvertPrice.errors  Invalid value
	Element Text Should Be         id=gibbonsPrice.errors  Invalid value
