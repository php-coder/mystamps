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
