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
	# check that e-mail has been sent by querying Wiremock
	${capturedRequests}=    Wait Until Keyword Succeeds  3x  1s  Search For Request  { "method": "POST", "url": "/mailgun/send-message" }
	${body}=                Set Variable  ${capturedRequests[0]['body']}
	Should Contain          ${body}  coder@rock.home
	Should Contain          ${body}  My Stamps <dont-reply@my-stamps.ru>
	Should Contain          ${body}  [my-stamps.ru] Account activation
	${linkRegexp}=          Set Variable  ${SITE_URL}/account/activate\\?key=[0-9a-z]{10}
	${links}=               Get Regexp Matches  ${body}  ${linkRegexp}
	Length Should Be        ${links}  1

*** Keywords ***
Before Test Suite
	Open Browser  ${SITE_URL}/account/register  ${BROWSER}

Search For Request
	[Documentation]   Searching for requests captured by WireMock
	[Arguments]       ${requestSignature}
	Create Session    mailserver  ${MOCK_SERVER}
	# See http://wiremock.org/docs/verifying/
	${response}=      Post Request  mailserver  /__admin/requests/find  data=${requestSignature}
	Log               ${response.json}
	Length Should Be  ${response.json['requests']}  1
	[Return]          ${response.json['requests']}
