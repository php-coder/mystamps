*** Settings ***
Documentation   Verify account registration scenario
Library         String
Library         SeleniumLibrary
Library         HttpRequestLibrary
Suite Setup     Before Test Suite
Suite Teardown  Close Browser
Force Tags      account  registration  logic

*** Test Cases ***
After account creation an e-mail with activation link should be send
	[Tags]                  unstable
	Input Text              id:email  coder@rock.home
	Submit Form             id:register-account-form
	Element Text Should Be  id:msg-success  Instructions to finish registration have been sent to your e-mail
	# check that e-mail has been sent by querying Wiremock. See http://wiremock.org/docs/verifying/
	Create Session          mailserver  ${MOCK_SERVER}
	${searchQuery}=         Set Variable  { "method": "POST", "url": "/mailgun/send-message" }
	${response}=            Post Request  mailserver  /__admin/requests/find  data=${searchQuery}
	Log                     ${response.json}
	Length Should Be        ${response.json['requests']}  1
	${linkRegexp}=          Set Variable  ${SITE_URL}/account/activate\\?key=[0-9a-z]{10}
	${links}=               Get Regexp Matches  ${response.json['requests'][0]['body']}  ${linkRegexp}
	Length Should Be        ${links}  1

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/register  ${BROWSER}
	Register Keyword To Run On Failure  Log Source

