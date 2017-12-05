*** Settings ***
Documentation    Verify required elements appearance on the main page from an admin
Library          Selenium2Library
Resource         ../auth.steps.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       misc  main-page

*** Test Cases ***
Admin should see a link to a page for importing a series
	[Documentation]           Verify presence of a link for importing a series
	[Tags]                    import-series
	Page Should Contain Link  link=import a series

*** Keywords ***
Before Test Suite
	[Documentation]                     Login as admin and go to the main page
	Open Browser                        ${SITE_URL}  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/

After Test Suite
	[Documentation]  Log out and close browser
	Log Out
	Close Browser
