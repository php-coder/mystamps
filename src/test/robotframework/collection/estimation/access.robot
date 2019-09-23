*** Settings ***
Documentation   Verify access to a collection estimation page
Library         SeleniumLibrary
Resource        ../../auth.steps.robot
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      collection  estimation  access

*** Test Cases ***
Anonymouser user doesn't have access to someone's estimation page
	Go To                   ${SITE_URL}/collection/paid/estimation
	Element Text Should Be  id:error-msg  Forbidden

User doesn't have access to someone's estimation page
	Log In As               login=coder  password=test  openPage=${true}
	Go To                   ${SITE_URL}/collection/paid/estimation
	Element Text Should Be  id:error-msg  Forbidden
	Log Out

Paid user has access only to its own estimation page
	Log In As               login=paid  password=test  openPage=${true}
	Go To                   ${SITE_URL}/collection/paid/estimation
	Element Text Should Be  tag:h3  Paid User's collection
	Go To                   ${SITE_URL}/collection/admin/estimation
	Element Text Should Be  id:error-msg  Forbidden
	Log Out

Admin has access to everyone's estimation page
	Log In As               login=admin  password=test  openPage=${true}
	Go To                   ${SITE_URL}/collection/paid/estimation
	Element Text Should Be  tag:h3  Paid User's collection
	Go To                   ${SITE_URL}/collection/admin/estimation
	Element Text Should Be  tag:h3  Site Admin's collection
	# No need to log out as a browser will be closed after the test

*** Keywords ***
Before Test Suite
	Open Browser                        about:blank  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

