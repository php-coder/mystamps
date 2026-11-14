*** Settings ***
Documentation  Verify CSP report submission scenario
Library        RequestsLibrary
Force Tags     site  csp  logic

*** Test Cases ***
CSP report should be accepted
    Create Session   server  ${SITE_URL}
    POST On Session  server  /site/csp/reports  data="{}"  expected_status=204
