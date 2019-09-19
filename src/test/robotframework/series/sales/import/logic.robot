*** Settings ***
Documentation   Verify scenarios of importing a series sale from an external site
Library         SeleniumLibrary
Resource        ../../../auth.steps.robot
Resource        ../../../selenium.utils.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      series  sales  import-sales  logic  react-related

*** Test Cases ***
Import a series sale with an existing seller
	Input Text                   id:series-sale-url  ${MOCK_SERVER}/series/sales/import/logic/existing-seller.html
	Submit Form                  id:import-series-sale-form
	# the original field is emptied after successful request,  so we wait for it
	Wait Until Element Value Is  series-sale-url     ${EMPTY}
	Urlfield Value Should Be     id:url              ${MOCK_SERVER}/series/sales/import/logic/existing-seller.html
	List Selection Should Be     id:seller           Eicca Toppinen
	Textfield Value Should Be    id:price            350
	List Selection Should Be     id:currency         RUB

Import a series sale without information
	Input Text                     id:series-sale-url  ${MOCK_SERVER}/series/sales/import/logic/empty.html
	Submit Form                    id:import-series-sale-form
	Wait Until Element Is Visible  id:import-series-sale-failed-msg
	Element Text Should Be         id:import-series-sale-failed-msg  Could not import information from this page

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To  ${SITE_URL}/series/1
