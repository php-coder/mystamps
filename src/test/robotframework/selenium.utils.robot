*** Settings ***
Documentation  Keywords (and workarounds) that are missing in the SeleniumLibrary for Robot Framework

*** Keywords ***
Element Text Should Match Regexp
	[Documentation]      Verify the text of the element identified by locator matches the given pattern
	[Arguments]          ${locator}  ${regexp}
	${text}=             Get Text  ${locator}
	Should Match Regexp  ${text}  ${regexp}

# Workaround for "Textfield Value Should Be" that causes NPE with <input type="url">:
# https://github.com/Hi-Fi/robotframework-seleniumlibrary-java/issues/52
Urlfield Value Should Be
	[Documentation]  Verifies text field locator has exactly text expected
	[Arguments]      ${locator}  ${expected}
	${value}=        Get Value  ${locator}
	Should Be Equal  ${expected}  ${value}

# Workaround for "Textfield Value Should Be" that causes NPE with <input type="email">:
# https://github.com/Hi-Fi/robotframework-seleniumlibrary-java/issues/52
Emailfield Value Should Be
	[Documentation]  Verifies text field locator has exactly text expected
	[Arguments]      ${locator}  ${expected}
	${value}=        Get Value  ${locator}
	Should Be Equal  ${expected}  ${value}

Select Country
	[Documentation]                   Select the given value in a select list that is using selectize.js
	[Arguments]                       ${value}
	# We can't use "Select From List By Label" because
	# 1) it doesn't work with invisible elements (and selectize.js makes field invisible)
	# 2) selectize.js dynamically creates list of countries only when we're clicking on the field
	Click Element                     id=country-selectized
	${countryOption}=                 Catenate  SEPARATOR=/
	...                               //*[contains(@class, "selectize-control")]
	...                               *[contains(@class, "selectize-dropdown")]
	...                               *[contains(@class, "selectize-dropdown-content")]
	...                               *[contains(@class, "option") and text()="${value}"]
	Wait Until Page Contains Element  xpath=${countryOption}
	Click Element                     xpath=${countryOption}

Country Field Should Have Option
	[Documentation]                   Verify the selection of the select list that is using selectize.js
	[Arguments]                       ${value}
	# We can't use "List Selection Should Be" because
	# 1) it doesn't work with invisible elements (and selectize.js makes field invisible)
	# 2) selectize.js dynamically creates list of countries only when we're clicking on the field
	Click Element                     id=country-selectized
	${dropdownXpath}=                 Set Variable  //*[contains(@class, "selectize-dropdown-content")]
	Wait Until Page Contains Element  xpath=${dropdownXpath}/*[contains(@class, "option")]
	Xpath Should Match X Times        xpath=${dropdownXpath}/*[text() = "${value}"]  expectedXpathCount=1

Link Should Point To
	[Documentation]  Verify that "href" attribute of the element refers to a link
	[Arguments]      ${locator}  ${expectedUrl}
	${url}=          Get Element Attribute  ${locator}@href
	Should Be Equal  ${expectedUrl}  ${url}

Wait Until Element Value Is
	[Documentation]     Hybrid of "Wait Until Page Contains Element" and "Textfield Value Should Be" keywords
	[Arguments]         ${id}  ${text}
	${elemHasValue}=    Catenate  SEPARATOR=
	...                 var el = window.document.getElementById('
	...                 ${id}
	...                 '); return el != null && el.value == '
	...                 ${text}
	...                 ';
	Wait For Condition  ${elemHasValue}
