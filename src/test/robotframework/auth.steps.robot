*** Settings ***
Documentation  Common steps for users authentication

*** Keywords ***
Log In As
	[Documentation]  Log in as a user
	[Arguments]      ${login}  ${password}  ${openPage}=${false}
	Run Keyword If   ${openPage}  Go To  ${SITE_URL}/account/auth
	Input Text       id:login     ${login}
	Input Password   id:password  ${password}
	Submit Form      id:auth-account-form

Log Out
	[Documentation]  Logout the current user
	Submit Form      id:logout-form
