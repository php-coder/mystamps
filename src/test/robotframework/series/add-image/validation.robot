*** Settings ***
Documentation    Verify validation scenarios during adding additional image to a series
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  add-image  validation

*** Test Cases ***
Add image with empty required fields
	Submit Form             id=add-image-form
	Element Text Should Be  id=image.errors      Image or image URL must be specified
	Element Text Should Be  id=image-url.errors  Image or image URL must be specified

Add image with an empty file
	Choose File             id=image  ${TEST_RESOURCE_DIR}${/}empty.jpg
	Submit Form             id=add-image-form
	Element Text Should Be  id=image.errors  File must not be empty

Add image with both image and an image URL
	Choose File             id=image      ${MAIN_RESOURCE_DIR}${/}test.png
	Input Text              id=image-url  ${SITE_URL}/image/1
	Submit Form             id=add-image-form
	Element Text Should Be  id=image.errors      Image or image URL must be specified
	Element Text Should Be  id=image-url.errors  Image or image URL must be specified

Add image with invalid URL
	Input Text              id=image-url  invalid-url
	Submit Form             id=add-image-form
	Element Text Should Be  id=image-url.errors  Value must be a valid URL

Add image with URL with invalid response
	Input Text              id=image-url  ${MOCK_SERVER}/series/response-400
	Submit Form             id=add-image-form
	Element Text Should Be  id=image-url.errors  Could not download file

Add image with URL to a file that does not exist
	Input Text              id=image-url  ${MOCK_SERVER}/series/response-404
	Submit Form             id=add-image-form
	Element Text Should Be  id=image-url.errors  File not found

Add image with URL that causes a redirect
	Input Text              id=image-url  ${MOCK_SERVER}/series/response-301
	Submit Form             id=add-image-form
	Element Text Should Be  id=image-url.errors  URL must not redirect to another address

Add image with URL to an empty file
	Input Text              id=image-url  ${MOCK_SERVER}/series/empty-jpeg-file
	Submit Form             id=add-image-form
	Element Text Should Be  id=image-url.errors  File must not be empty

Add image with URL to a file of unsupported type (not an image)
	Input Text              id=image-url  ${MOCK_SERVER}/series/not-image-file
	Submit Form             id=add-image-form
	Element Text Should Be  id=image-url.errors  Invalid file type

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/1

After Test Suite
	Log Out
	Close Browser
