*** Settings ***
Documentation    Verify scenarios of importing a series from an external site
Library          SeleniumLibrary
Library          DateTime
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Test Setup       Before Test
Force Tags       series  import-series  logic

*** Test Cases ***
Import series from an external site (in English, use category, country and date locators)
	[Documentation]                  Verify import from a page in English and with different locators
	${importUrl}=                    Set Variable  http://127.0.0.1:8080/series/2?lang=en
	Input Text                       id=url  ${importUrl}
	Submit Form                      id=import-series-form
	${requestLocation}=              Get Location
	${category}=                     Get Selected List Label  id=category
	${country}=                      Get Selected List Label  id=country
	${quantity}=                     Get Value  id=quantity
	${year}=                         Get Selected List Label  id=year
	Element Text Should Be           id=request-url     ${importUrl}
	Element Text Should Be           id=request-status  ParsingSucceeded
	Should Be Equal                  ${category}        Prehistoric animals
	Should Be Equal                  ${country}         Italy
	Should Be Empty                  ${quantity}
	Checkbox Should Be Selected      id=perforated
	Urlfield Value Should Be         id=image-url       http://127.0.0.1:8080/image/1
	Should Be Equal                  ${year}            2000
	Input Text                       id=quantity  1
	Submit Form                      id=create-series-form
	${seriesLocation}=               Get Location
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
	${category}=                 Get Selected List Label  id=category
	${country}=                  Get Selected List Label  id=country
	${quantity}=                 Get Value  id=quantity
	${year}=                     Get Selected List Label  id=year
	Element Text Should Be       id=request-url     http://localhost:8080/series/2?lang=ru&str=тест
	Element Text Should Be       id=request-status  ParsingSucceeded
	Should Be Equal              ${category}        Prehistoric animals
	Should Be Equal              ${country}         Italy
	Should Be Empty              ${quantity}
	Checkbox Should Be Selected  id=perforated
	Urlfield Value Should Be     id=image-url       http://localhost:8080/image/1
	Should Be Equal              ${year}            2000

Import series from external site with catalog numbers (use description locator)
	[Documentation]             Verify import of catalog numbers by extracting them from a description
	Input Text                  id=url  ${MOCK_SERVER}/series/import/request-logic/catalog-numbers-in-description.html
	Submit Form                 id=import-series-form
	Textfield Value Should Be   id=michel-numbers   2242-2246
	Submit Form                 id=create-series-form
	Element Text Should Be      id=michel_catalog_info  \#2242-2246
	Click Link                  id=import-request-link
	Element Should Be Disabled  id=michel-numbers

Import series and series sale with existing seller from an external site
	[Documentation]             Verify import series and sale (with existing seller)
	Input Text                  id=url  ${MOCK_SERVER}/series/import/request-logic/existing-seller.html
	Submit Form                 id=import-series-form
	${requestLocation}=         Get Location
	# sale info should be parsed and shown at the request page
	List Selection Should Be    id=seller    Eicca Toppinen
	Textfield Value Should Be   id=price     111
	List Selection Should Be    id=currency  RUB
	Submit Form                 id=create-series-form
	# after importing a series, sale info should be shown at the info page
	${currentDate}=             Get Current Date  result_format=%d.%m.%Y
	Element Text Should Be      id=series-sale-1-info         ${currentDate} Eicca Toppinen was selling for 111.00 RUB
	Link Should Point To        id=series-sale-1-seller       http://example.com/eicca-toppinen
	Link Should Point To        id=series-sale-1-transaction  ${MOCK_SERVER}/series/import/request-logic/existing-seller.html
	Go To                       ${requestLocation}
	# after importing a series, sale info at the request page should be shown as read-only
	Element Should Be Disabled  id=seller
	Element Should Be Disabled  id=price
	Element Should Be Disabled  id=currency

Import series and series sale with a new seller from an external site
	[Documentation]             Verify import series and sale (with a new seller)
	Input Text                  id=url  ${MOCK_SERVER}/series/import/request-logic/new-seller.html
	Submit Form                 id=import-series-form
	${requestLocation}=         Get Location
	# seller info should be parsed and shown at the request page
	${group}=                   Get Selected List Label  id=seller-group
	Should Be Equal             ${group}  example.com
	Textfield Value Should Be   id=seller-name  Lando Livianus
	Urlfield Value Should Be    id=seller-url   http://example.com/lando-livianus
	Submit Form                 id=create-series-form
	# after importing a series, sale info should contain a new seller
	${currentDate}=             Get Current Date  result_format=%d.%m.%Y
	Element Text Should Be      id=series-sale-1-info    ${currentDate} Lando Livianus was selling for 320.50 RUB
	Link Should Point To        id=series-sale-1-seller  http://example.com/lando-livianus
	# @todo #857 Check that a just created seller belongs to the "example.com" group
	Go To                       ${requestLocation}
	# after importing a series, sale info at the request page should be shown as read-only
	Element Should Be Disabled  id=seller-group
	Element Should Be Disabled  id=seller-name
	Element Should Be Disabled  id=seller-url

Submit a request that will fail to download a file
	Input Text              id=url  ${MOCK_SERVER}/series/response-404
	Submit Form             id=import-series-form
	Element Text Should Be  id=request-status  DownloadingFailed

Submit a request with a document that couldn't be parsed
	[Documentation]         Verify submitting a URL with an empty HTML document
	Input Text              id=url  ${MOCK_SERVER}/series/import/request-logic/simple.html
	Submit Form             id=import-series-form
	Element Text Should Be  id=request-status  ParsingFailed

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test

Before Test
	Go To  ${SITE_URL}/series/import/request
