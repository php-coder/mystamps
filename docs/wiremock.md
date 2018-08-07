# WireMock

## What and why
WireMock is a simulator for HTTP-based APIs. We use this tool in the integration tests for mocking
external services. For example, for testing import of the stamps series from sites.

## Running on TravisCI
WireMock is automatically running/stopping during integration tests execution (`mvn verify`).

## Running manually
In order to run the server as a standlone process:
* uncomment `<keepRunning>` option in the `pom.xml` file
* execute the `mvn wiremock:run` command

After that the mock server will be available on 8888 port.

## Troubleshooting
* [run the plugin separately](#running-manually) to be able to test it with a browser/curl
* append `--verbose` flag to the options from `<params>` tag in the `pom.xml` file. This instructs
  WireMock to produce more detailed output to the console

## Configuration
WireMock is fully configured by the `wiremock-maven-plugin` in the `pom.xml` file:

* the server is listening on `8888` port
* you can refer to the mock server from within RobotFramework test cases by using `${MOCK_SERVER}`
  variable.
* static files are reside inside the `src/test/wiremock/__files` directory
* mocks and stubs are reside inside the `src/test/wiremock/mappings` directory

By our convention the files in these directories should have the same hierarchy as their
RobotFramework test cases. For instance, the mocks/files used solely by
`category/creation/logic.robot` test case, should be placed into
`src/test/wiremock/__files/category/creation/logic` and
`src/test/wiremock/mappings/category/creation/logic` directories.

In the case, mocks/files are being used by the different test cases, they should reside in a
directory that is the base for all such test cases.

## Links
* http://wiremock.org/
* https://github.com/tomakehurst/wiremock
* https://github.com/automatictester/wiremock-maven-plugin
