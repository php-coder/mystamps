<%@ page import="ru.mystamps.web.Url" session="false" contentType="text/plain" trimDirectiveWhitespaces="false" %>
# robots.txt for http://my-stamps.ru
User-Agent: *
Disallow: <%= Url.REGISTRATION_PAGE %>
Disallow: <%= Url.ACTIVATE_ACCOUNT_PAGE %>
Disallow: <%= Url.AUTHENTICATION_PAGE %>
Disallow: <%= Url.LOGIN_PAGE %>
Disallow: <%= Url.ADD_COUNTRY_PAGE %>
Disallow: <%= Url.ADD_SERIES_PAGE %>
Disallow: <%= Url.ADD_CATEGORY_PAGE %>
Disallow: <%= Url.UNAUTHORIZED_PAGE %>
Disallow: <%= Url.NOT_FOUND_PAGE %>
