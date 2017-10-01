*** Settings ***
Documentation    Verify validation scenarios during adding additional image to a series
Library          Selenium2Library
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  add-image  validation

*** Test Cases ***
Add image with empty required fields
	[Documentation]         Verify validation of mandatory fields
	Submit Form             id=add-image-form
	Element Text Should Be  id=image.errors      Image or image URL must be specified
	Element Text Should Be  id=image-url.errors  Image or image URL must be specified

Add image with empty file
	[Documentation]         Verify validation of an empty file
	Choose File             id=image  ${TEST_RESOURCE_DIR}${/}empty.png
	Submit Form             id=add-image-form
	Element Text Should Be  id=image.errors  File must not be empty

Add image with both image and an image URL
	[Documentation]         Verify validation of an image and an image URL provided at the same time
	Choose File             id=image      ${MAIN_RESOURCE_DIR}${/}test.png
	Input Text              id=image-url  ${SITE_URL}/image/1
	Submit Form             id=add-image-form
	Element Text Should Be  id=image.errors      Image or image URL must be specified
	Element Text Should Be  id=image-url.errors  Image or image URL must be specified

Add image with image URL with invalid response
	[Documentation]         Verify validation of invalid response from a server
	Input Text              id=image-url  ${SITE_URL}/test/invalid/response-400
	Submit Form             id=add-image-form
	Element Text Should Be  id=image-url.errors  Could not download file

Add image with image URL to a file that does not exist
	[Documentation]         Verify validation of URL to non existing file
	Input Text              id=image-url  ${SITE_URL}/test/invalid/response-404
	Submit Form             id=add-image-form
	Element Text Should Be  id=image-url.errors  File not found

Add image with image URL that causes a redirect
	[Documentation]         Verify validation of URL with redirect
	Input Text              id=image-url  ${SITE_URL}/test/invalid/response-301
	Submit Form             id=add-image-form
	Element Text Should Be  id=image-url.errors  URL must not redirect to another address

Add image with image URL to an empty file
	[Documentation]         Verify validation of URL to an empty file
	Input Text              id=image-url  ${SITE_URL}/test/invalid/empty-jpeg-file
	Submit Form             id=add-image-form
	Element Text Should Be  id=image-url.errors  File must not be empty

Add image with image URL to not an image file
	[Documentation]         Verify validation of URL to a file of unsupported type
	Input Text              id=image-url  ${SITE_URL}/test/invalid/not-image-file
	Submit Form             id=add-image-form
	Element Text Should Be  id=image-url.errors  Invalid file type

*** Keywords ***
Before Test Suite
	[Documentation]                     Login as admin and open a page with a series info
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/1

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser
