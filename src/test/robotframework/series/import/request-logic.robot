*** Settings ***
Documentation    Verify scenarios of importing a series from an external site
Library          Selenium2Library
Library          DateTime
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Test Setup       Before Test
Force Tags       series  import-series  logic

*** Test Cases ***
Import series from an external site (in English, use category, country and date locators)
	[Documentation]                  Verify import from a page in English and with different locators
	${importUrl}=                    Set Variable  http://127.0.0.1:8080/series/2?lang=en
	Input Text                       id=url  ${importUrl}
	Submit Form                      id=import-series-form
	${requestLocation}=              Get Location
	Should Match Regexp              ${requestLocation}  /series/import/request/\\d+
	${category}=                     Get Selected List Label  id=category
	${country}=                      Get Selected List Label  id=country
	# We can't use "Textfield Value Should Be" because it causes NPE on inputs of type url/number:
	# https://github.com/MarkusBernhardt/robotframework-selenium2library-java/issues/92
	${quantity}=                     Get Value  id=quantity
	${imageUrl}=                     Get Value  id=image-url
	${year}=                         Get Selected List Label  id=year
	Element Text Should Be           id=request-url     ${importUrl}
	Element Text Should Be           id=request-status  ParsingSucceeded
	Should Be Equal                  ${category}        Prehistoric animals
	Should Be Equal                  ${country}         Italy
	Should Be Empty                  ${quantity}
	Checkbox Should Be Selected      id=perforated
	Should Be Equal                  ${imageUrl}        http://127.0.0.1:8080/image/1
	Should Be Equal                  ${year}            2000
	Input Text                       id=quantity  1
	Submit Form                      id=create-series-form
	${seriesLocation}=               Get Location
	Should Match Regexp              ${seriesLocation}  /series/\\d+
	Element Text Should Be           id=category_name  Prehistoric animals
	Element Text Should Be           id=country_name   Italy
	Element Text Should Be           id=issue_date     2000
	Element Text Should Be           id=quantity       1
	Element Text Should Be           id=perforated     Yes
	# @todo #749 /series/{id}: add integration test that import info is only visible to admin
	Element Text Should Be           id=import-info    ${importUrl}
	Page Should Contain Image        id=series-image-1
	Go To                            ${requestLocation}
	Element Text Should Be           id=request-status  ImportSucceeded
	Element Should Be Disabled       id=category
	Element Should Be Disabled       id=country
	Page Should Not Contain Element  id=quantity
	Page Should Not Contain Element  id=perforated
	Element Should Be Disabled       id=image-url
	Element Should Be Disabled       id=year
	Page Should Not Contain Element  id=create-series-btn
	Page Should Contain Link         link=${seriesLocation}

Import series from an external site (in Russian, use description locator)
	[Documentation]              Verify import from a page in Russian and shared locator
	Input Text                   id=url  http://localhost:8080/series/2?lang=ru&str=тест
	Submit Form                  id=import-series-form
	${location}=                 Get Location
	Should Match Regexp          ${location}  /series/import/request/\\d+
	${category}=                 Get Selected List Label  id=category
	${country}=                  Get Selected List Label  id=country
	# We can't use "Textfield Value Should Be" because it causes NPE on inputs of type url/number:
	# https://github.com/MarkusBernhardt/robotframework-selenium2library-java/issues/92
	${quantity}=                 Get Value  id=quantity
	${imageUrl}=                 Get Value  id=image-url
	${year}=                     Get Selected List Label  id=year
	Element Text Should Be       id=request-url     http://localhost:8080/series/2?lang=ru&str=тест
	Element Text Should Be       id=request-status  ParsingSucceeded
	Should Be Equal              ${category}        Prehistoric animals
	Should Be Equal              ${country}         Italy
	Should Be Empty              ${quantity}
	Checkbox Should Be Selected  id=perforated
	Should Be Equal              ${imageUrl}        http://localhost:8080/image/1
	Should Be Equal              ${year}            2000

Import series and series sale with existing seller from an external site
	[Documentation]             Verify import series and sale (with existing seller)
	Input Text                  id=url  http://localhost:8080/test/valid/series-info/existing-seller
	Submit Form                 id=import-series-form
	${requestLocation}=         Get Location
	Should Match Regexp         ${requestLocation}  /series/import/request/\\d+
	# sale info should be parsed and shown at the request page
	List Selection Should Be    id=seller    Eicca Toppinen
	Textfield Value Should Be   id=price     111
	List Selection Should Be    id=currency  RUB
	Submit Form                 id=create-series-form
	${seriesLocation}=          Get Location
	Should Match Regexp         ${seriesLocation}  /series/\\d+
	# after importing a series, sale info should be shown at the info page
	${currentDate}=             Get Current Date  result_format=%d.%m.%Y
	Element Text Should Be      id=series-sale-1-info         ${currentDate} Eicca Toppinen was selling for 111.00 RUB
	Link Should Point To        id=series-sale-1-seller       http://example.com/eicca-toppinen
	Link Should Point To        id=series-sale-1-transaction  http://localhost:8080/test/valid/series-info/existing-seller
	Go To                       ${requestLocation}
	# after importing a series, sale info at the request page should be shown as read-only
	Element Should Be Disabled  id=seller
	Element Should Be Disabled  id=price
	Element Should Be Disabled  id=currency

Import series and series sale with a new seller from an external site
	[Documentation]             Verify import series and sale (with a new seller)
	Input Text                  id=url  http://localhost:8080/test/valid/series-info/new-seller
	Submit Form                 id=import-series-form
	${requestLocation}=         Get Location
	Should Match Regexp         ${requestLocation}  /series/import/request/\\d+
	# seller info should be parsed and shown at the request page
	Textfield Value Should Be   id=seller-name  Lando Livianus
	# We can't use "Textfield Value Should Be" because it causes NPE:
	# https://github.com/MarkusBernhardt/robotframework-selenium2library-java/issues/92
	${sellerUrl}=               Get Value     id=seller-url
	Should Be Equal             ${sellerUrl}  http://example.com/lando-livianus
	Submit Form                 id=create-series-form
	${seriesLocation}=          Get Location
	Should Match Regexp         ${seriesLocation}  /series/\\d+
	# after importing a series, sale info should contain a new seller
	${currentDate}=             Get Current Date  result_format=%d.%m.%Y
	Element Text Should Be      id=series-sale-1-info    ${currentDate} Lando Livianus was selling for 320.50 RUB
	Link Should Point To        id=series-sale-1-seller  http://example.com/lando-livianus
	Go To                       ${requestLocation}
	# after importing a series, sale info at the request page should be shown as read-only
	Element Should Be Disabled  id=seller-name
	Element Should Be Disabled  id=seller-url

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

Link Should Point To
	[Documentation]  Verify that "href" attribute of the element refers to a link
	[Arguments]      ${locator}  ${expectedUrl}
	${url}=          Get Element Attribute  ${locator}@href
	Should Be Equal  ${expectedUrl}  ${url}
