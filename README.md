# My Stamps

[![Integration Tests](https://github.com/php-coder/mystamps/actions/workflows/integration-tests-h2.yml/badge.svg?branch=master)](https://github.com/php-coder/mystamps/actions/workflows/integration-tests-h2.yml)
[![Unit Tests](https://github.com/php-coder/mystamps/actions/workflows/unit-tests.yml/badge.svg)](https://github.com/php-coder/mystamps/actions/workflows/unit-tests.yml)
[![Static Analysis](https://github.com/php-coder/mystamps/actions/workflows/static-analysis.yml/badge.svg)](https://github.com/php-coder/mystamps/actions/workflows/static-analysis.yml)
[![Uptime Statistic](https://badgen.net/uptime-robot/month/ur243278-551fbb732949dbdee27c7552)](https://stats.uptimerobot.com/1jXAjFpgP)

## What is it?

This is a website for anybody who collects post stamps and wants to have an online version of the collection.

## How can it be useful to me?

On the site you can:
* see the statistics (including charts) about your collection
  * number of series and stamps
  * from what countries
  * in what categories
  * amount of money that has been spent to buy the stamps
* share a link to the collection with friends
* use it as a list of the stamps that you are selling on an auction
* add to the signature on the forums or e-mail
* use it where a photo of your collection is needed

## How can I try it?

You can look at it and try on https://my-stamps.ru

If you are programmer/sysadmin, or you are just feeling that you are able to run a local version of the site, then follow the instructions:

* install JDK (8th version is required) and Maven
  * the preferred way to set up tools is to use [`mise`](https://mise.jdx.dev/getting-started.html). After its activation, the required tools will be installed automatically
* clone this project
* from the console inside the directory with source code, execute the command `mvn spring-boot:run`
* open up `http://127.0.0.1:8080` in a browser
* browse the site or log in as one of the pre-created users: `admin`, `paid`, or `coder` with password `test`
* press `Ctrl-C` to stop the server

**Caution!** The purpose of that version is a preview of the site and its capabilities. Because of that, the **changes** that you can make on the site **will be lost after stopping the server**!

## What is inside?

* *At the heart of*: Spring Framework (and especially Spring Boot)
* *Template engine*: Thymeleaf
* *UI*: HTML, Bootstrap and JavaScript (React, JQuery)
* *Security*: Spring Security
* *Databases*: H2, MySQL or PostgreSQL
* *Database access*: Spring's `JdbcTemplate`
* *Database migrations*: Liquibase
* *Validation*: Hibernate Validator
* *Logging*: Slf4j (Logback)
* *Unit tests*: Groovy with Spock Framework or JUnit (for Java code), Jest (for JavaScript code)
* *Integration tests*: Selenium3, RobotFramework, WireMock
* *Deployment*: bash, Ansible, Terraform
* *Others*: Lombok, Togglz, WebJars, AssertJ, Mockito
