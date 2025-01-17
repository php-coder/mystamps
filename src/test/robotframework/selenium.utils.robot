*** Settings ***
Documentation  Keywords (and workarounds) that are missing in the SeleniumLibrary for Robot Framework

*** Keywords ***
Element Text Should Match Regexp
	[Documentation]      Verify the text of the element identified by locator matches the given pattern
	[Arguments]          ${locator}  ${regexp}
	${text}=             Get Text  ${locator}
	Should Match Regexp  ${text}  ${regexp}

# We can't use "Select From List By Label" because
# 1) it doesn't work with invisible elements (and selectize.js makes a field invisible)
# 2) selectize.js dynamically creates a list of countries only when we click on a field
Selectize By Value
	[Documentation]     Select the given value in a select that is using selectize.js by provided id
	[Arguments]         ${id}  ${slug}
	Execute Javascript  return $('#${id}').selectize()[0].selectize.setValue('${slug}');

Country Field Should Have Option
	[Documentation]                   Verify the selection of the select list that is using selectize.js
	[Arguments]                       ${value}
	# We can't use "List Selection Should Be" because
	# 1) it doesn't work with invisible elements (and selectize.js makes field invisible)
	# 2) selectize.js dynamically creates list of countries only when we're clicking on the field
	Click Element                     id:country-selectized
	${dropdownXpath}=                 Set Variable  //*[contains(@class, "selectize-dropdown-content")]
	Wait Until Page Contains Element  xpath:${dropdownXpath}/*[contains(@class, "option")]
	Xpath Should Match X Times        xpath:${dropdownXpath}/*[text() = "${value}"]  expectedXpathCount=1

Link Should Point To
	[Documentation]  Verify that "href" attribute of the element refers to a link
	[Arguments]      ${locator}  ${expectedUrl}
	${url}=          Get Element Attribute  ${locator}@href
	Should Be Equal  ${expectedUrl}  ${url}

# NOTE: this keyword should be used as a last resort. Prefer "Wait Until Page Contains Element"
# with some other keyword for checking element's state where possible
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

Select Random Option From List
	[Documentation]            Choose a random option from a select element
	[Arguments]                ${locator}
	${options}=                Get List Items  ${locator}
	${size}=                   Get Length  ${options}
	${randomIndex}=            Evaluate  random.randint(0, ${size}-1)  modules=random
	Select From List By Index  ${locator}  ${randomIndex}

Disable Client Validation
	[Documentation]     Disable client validation for a form with a specified ID
	[Arguments]         ${id}
	Execute Javascript  return window.document.getElementById('${id}').setAttribute('novalidate', 'true');

Modify Input Type
	[Documentation]     Modifies <input> "type" attribute to bypass possible browser's validations
	[Arguments]         ${id}  ${type}
	Execute Javascript  return window.document.getElementById('${id}').type = '${type}';
