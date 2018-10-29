*** Settings ***
Documentation  Keywords (and workarounds) that are missing in the SeleniumLibrary for Robot Framework

*** Keywords ***
Element Text Should Match Regexp
	[Documentation]      Verify the text of the element identified by locator matches the given pattern
	[Arguments]          ${locator}  ${regexp}
	${text}=             Get Text  ${locator}
	Should Match Regexp  ${text}  ${regexp}

# Workaround for "Textfield Value Should Be" that causes NPE with <input type="url">:
# https://github.com/MarkusBernhardt/robotframework-selenium2library-java/issues/92
Urlfield Value Should Be
	[Documentation]  Verifies text field locator has exactly text expected
	[Arguments]      ${locator}  ${expected}
	${value}=        Get Value  ${locator}
	Should Be Equal  ${expected}  ${value}

Remove Element Attribute
	[Documentation]     Removes an attribute with a specified name from the element identified by its id
	[Arguments]         ${id}  ${attributeName}
	${removeAttr}=      Catenate  SEPARATOR=
	...                 return window.document.getElementById('
	...                 ${id}
	...                 ').removeAttribute('
	...                 ${attributeName}
	...                 ');
	Execute Javascript  ${removeAttr}

Set Input Type
	[Documentation]     Changes a type of the input element with a specified id
	[Arguments]         ${id}  ${type}
	${setType}=         Catenate  SEPARATOR=
	...                 return window.document.getElementById('
	...                 ${id}
	...                 ').type = '
	...                 ${type}
	...                 ';
	Execute Javascript  ${setType}

Wait Until Element Text Is
	[Documentation]     Hybrid of "Wait Until Page Contains Element" and "Element Text Should Be" keywords
	[Arguments]         ${id}  ${text}
	${elemHasText}=     Catenate  SEPARATOR=
	...                 var el = window.document.getElementById('
	...                 ${id}
	...                 '); return el != null && el.textContent == '
	...                 ${text}
	...                 ';
	Wait For Condition  ${elemHasText}
