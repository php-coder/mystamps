*** Settings ***
Documentation  Verify CSP report submission scenario
Library        HttpRequestLibrary
Force Tags     site  csp  logic

*** Test Cases ***
CSP report should be accepted
    Create Session           server  ${SITE_URL}
    Post Request             server  /site/csp/reports  data="{}"
    Response Code Should Be  server  204
