# My Stamps

[![Build Status](https://travis-ci.org/php-coder/mystamps.svg?branch=master)](https://travis-ci.org/php-coder/mystamps)
[![Dependency Status](https://www.versioneye.com/user/projects/55b783256537620017001225/badge.svg?style=flat)](https://www.versioneye.com/user/projects/55b783256537620017001225)
[![Coverage Status](https://coveralls.io/repos/php-coder/mystamps/badge.svg?branch=master)](https://coveralls.io/r/php-coder/mystamps)

## What's it?

It's a website for anybody who collects post stamps and wants to have online version of collection.

## How it can be useful for me?

With this site you can:
* see the statistic (including charts) about your collection (how many series and stamps do you have? From which countries and in which categories they are?)
* share link to collection with friends
* use it as list of stamps which you are selling on auction
* add to signature on forums
* use it where photo of your collection is needed

## How I can try it?

You can look at it and try on https://my-stamps.ru

If you are programmer/sysadmin or you just feeling that you are able to run local version of the site then follow the following instructions:

* install Java (at least 8th version is required)
* install Maven
* clone this project
* from the console inside the directory with source code, execute command `mvn spring-boot:run`
* open up `http://127.0.0.1:8080` in the browser
* browse the site or log in as one of the pre-created users: `admin` / `test` or `coder` / `test`
* press `Ctrl-C` to stop the server

**Caution!** The purpose of that version is preview of the site and its capabilities. Because of that, the **changes** that you can make on the site **will be lost after stopping the server**!

## What's inside? (interesting only for programmers)

* *At the heart of*: Spring Framework (and especially Spring Boot)
* *Template engine*: Thymeleaf
* *UI*: HTML, Bootstrap and a bit of JavaScript with JQuery
* *Security*: Spring Security
* *Databases*: H2 or MySQL
* *Database access*: Spring's `JdbcTemplate`
* *Database migrations*: Liquibase
* *Validation*: JSR-303 (Hibernate Validator)
* *Logging*: Slf4j (Logback)
* *Unit tests*: Groovy and Spock Framework (for Java code), jasmine (for JavaScript code)
* *Integration tests*: Selenium2, RobotFramework, TestNG and fest-assert
* *Others*: Lombok, Togglz, WebJars
