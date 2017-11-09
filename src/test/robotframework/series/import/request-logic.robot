*** Settings ***
Documentation    Verify scenarios of importing a series from an external site
Library          Selenium2Library
Library          String
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       series  import-series  logic

*** Test Cases ***
Import series from an external site (in English, use category, country and date locators)
	[Documentation]             Verify import from a page in English and with different locators
	Input Text                  id=url  http://127.0.0.1:8080/series/2?lang=en
	Submit Form                 id=import-series-form
	${location}=                Get Location
	Should Match Regexp         ${location}  /series/import/request/\\d+
	Element Text Should Be      id=request-url        http://127.0.0.1:8080/series/2?lang=en
	Element Text Should Be      id=request-status     ParsingSucceeded
	Element Text Should Be      id=parsed-category    Prehistoric animals
	Element Text Should Be      id=parsed-country     Italy
	Element Text Should Be      id=parsed-image-url   http://127.0.0.1:8080/image/1
	Element Text Should Be      id=parsed-issue-year  2000
	Submit Form                 id=start-import-series-form
	${encodedUrl}=              Encode URL  http://127.0.0.1:8080/image/1
	Location Should Be          ${SITE_URL}/series/add?category=prehistoric-animals&country=italy&image_url=${encodedUrl}&year=2000
	${category}=                Get Selected List Label  id=category
	Should Be Equal             ${category}  Prehistoric animals
	# TODO: verify country
	# We can't use "Textfield Value Should Be" because it causes NPE:
	# https://github.com/MarkusBernhardt/robotframework-selenium2library-java/issues/92
	${value}=                   Get Value  id=image-url
	Should Be Equal             ${value}  http://127.0.0.1:8080/image/1
	${year}=                    Get Selected List Label  id=year
	Should Be Equal             ${year}  2000

Import series from an external site (in Russian, use description locator)
	[Documentation]             Verify import from a page in Russian and shared locator
	Input Text                  id=url  http://localhost:8080/series/2?lang=ru
	Submit Form                 id=import-series-form
	${location}=                Get Location
	Should Match Regexp         ${location}  /series/import/request/\\d+
	Element Text Should Be      id=request-url        http://localhost:8080/series/2?lang=ru
	Element Text Should Be      id=request-status     ParsingSucceeded
	Element Text Should Be      id=parsed-category    Prehistoric animals
	Element Text Should Be      id=parsed-country     Italy
	Element Text Should Be      id=parsed-image-url   http://localhost:8080/image/1
	Element Text Should Be      id=parsed-issue-year  2000
	Submit Form                 id=start-import-series-form
	${encodedUrl}=              Encode URL  http://localhost:8080/image/1
	Location Should Be          ${SITE_URL}/series/add?category=prehistoric-animals&country=italy&image_url=${encodedUrl}&year=2000
	${category}=                Get Selected List Label  id=category
	Should Be Equal             ${category}  Prehistoric animals
	# TODO: verify country
	# We can't use "Textfield Value Should Be" because it causes NPE:
	# https://github.com/MarkusBernhardt/robotframework-selenium2library-java/issues/92
	${value}=                   Get Value  id=image-url
	Should Be Equal             ${value}  http://localhost:8080/image/1
	${year}=                    Get Selected List Label  id=year
	Should Be Equal             ${year}  2000

Submit a request that will fail to download a file
	[Documentation]         Verify submitting a URL with a non-existing file
	Input Text              id=url  ${SITE_URL}/test/invalid/response-404
	Submit Form             id=import-series-form
	Element Text Should Be  id=request-status  DownloadingFailed

Submit a request with a document that couldn't be parsed
	[Documentation]         Verify submitting a URL with an empty HTML document
	Input Text              id=url  ${SITE_URL}/test/invalid/simple-html
	Submit Form             id=import-series-form
	Element Text Should Be  id=request-status  ParsingFailed

*** Keywords ***
Before Test Suite
	[Documentation]                     Open browser, register fail hook and login as admin
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test

Before Test
	[Documentation]  Open a page for requesting a series import
	Go To            ${SITE_URL}/series/import/request

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser

Encode URL
	[Documentation]  Simple implementation for encoding URL
	[Arguments]      ${url}
	${encodedUrl}=   Replace String  ${url}         /  %2F
	${encodedUrl}=   Replace String  ${encodedUrl}  :  %3A
	[Return]         ${encodedUrl}
