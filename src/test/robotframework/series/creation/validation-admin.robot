*** Settings ***
Documentation    Verify series creation validation scenarios from admin
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  validation

*** Test Cases ***
Create series with empty required fields
	Submit Form             id=add-series-form
	Element Text Should Be  id=category.errors  Value must not be empty
	Element Text Should Be  id=quantity.errors  Value must not be empty
	Element Text Should Be  id=image.errors      Image or image URL must be specified
	Element Text Should Be  id=image-url.errors  Image or image URL must be specified

Create series with both image and an image URL
	Choose File             id=image      ${MAIN_RESOURCE_DIR}${/}test.png
	Input Text              id=image-url  ${SITE_URL}/image/1
	Submit Form             id=add-series-form
	Element Text Should Be  id=image.errors      Image or image URL must be specified
	Element Text Should Be  id=image-url.errors  Image or image URL must be specified

Create series with invalid image URL
	Input Text              id=image-url  invalid-url
	Submit Form             id=add-series-form
	Element Text Should Be  id=image-url.errors  Value must be a valid URL

Create series with image URL with invalid response
	Input Text              id=image-url  ${MOCK_SERVER}/series/response-400
	Submit Form             id=add-series-form
	Element Text Should Be  id=image-url.errors  Could not download file

Create series with image URL to a file that does not exist
	Input Text              id=image-url  ${MOCK_SERVER}/series/response-404
	Submit Form             id=add-series-form
	Element Text Should Be  id=image-url.errors  File not found

Create series with image URL that causes a redirect
	Input Text              id=image-url  ${MOCK_SERVER}/series/response-301
	Submit Form             id=add-series-form
	Element Text Should Be  id=image-url.errors  URL must not redirect to another address

Create series with image URL to an empty file
	Input Text              id=image-url  ${MOCK_SERVER}/series/empty-jpeg-file
	Submit Form             id=add-series-form
	Element Text Should Be  id=image-url.errors  File must not be empty

Create series with image URL to a file of unsupported type (not an image)
	Input Text              id=image-url  ${MOCK_SERVER}/series/not-image-file
	Submit Form             id=add-series-form
	Element Text Should Be  id=image-url.errors  Invalid file type

Create series with too long comment
	${letter}=                     Set Variable  x
	Click Element                  id=add-comment-link
	Wait Until Element Is Visible  id=comment
	Input Text                     id=comment  ${letter * 256}
	Submit Form                    id=add-series-form
	Element Text Should Be         id=comment.errors  Value is greater than allowable maximum of 255 characters

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/add

After Test Suite
	Log Out
	Close Browser
