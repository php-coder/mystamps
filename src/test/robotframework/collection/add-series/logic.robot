*** Settings ***
Documentation    Verify add series to user's collection
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   Close Browser
Force Tags       collection  series  logic

*** Test Cases ***
Add a series to user's collection (all stamps)
	Go To                      ${SITE_URL}/series/2
	Element Text Should Be     id:series-status-msg  You don't have this series. Add one instance:
	Textfield Value Should Be  id:number-of-stamps  2
	Element Text Should Be     id:number-of-stamps-block  I have out of 2 stamps
	Submit Form                id:add-series-form
	Page Should Contain Link   css:.image-gallery figcaption [href="/series/2"]
	# See https://developer.mozilla.org/en-US/docs/Web/CSS/General_sibling_combinator
	Element Text Should Be     css:.image-gallery figcaption [href="/series/2"] ~ .label-success  New

Add the same series to user's collection again (incomplete series)
	Go To                       ${SITE_URL}/series/2
	Element Text Should Be      id:series-status-msg  You already have this series. Add another one instance:
	Input Text                  id:number-of-stamps  1
	Submit Form                 id:add-series-form
	# See https://stackoverflow.com/questions/1604471/how-can-i-find-an-element-by-css-class-with-xpath
	${linkXpath}=               Catenate  SEPARATOR=
	...                         //*[contains(concat(" ", normalize-space(@class), " "), " image-gallery ")]
	...                         //figcaption
	...                         //a[@href="/series/2"]
	${successLabelElem}=        Set Variable  *[contains(concat(" ", normalize-space(@class), " "), " label-success ")]
	Xpath Should Match X Times  xpath:${linkXpath}  expectedXpathCount=2
	Xpath Should Match X Times  xpath:${linkXpath}/following-sibling::${successLabelElem}  expectedXpathCount=1
	Element Text Should Be      css:.image-gallery figcaption [href="/series/2"] ~ .label-success  New
	# See https://developer.mozilla.org/en-US/docs/Web/CSS/General_sibling_combinator
	Element Text Should Be      css:.image-gallery figcaption [href="/series/2"] ~ .label-default  1 out of 2

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=seriesowner  password=test
