Feature: User on index page

  Scenario: Anonymous user opens index page
      Given As anonymous user
       When I open index page
       Then I see welcome text
        And I see 2 navigation links
        And I see link for list of categories
        And I see link for list of countries
        But I don't see link for adding series
        But I don't see link for adding categories
        But I don't see link for adding countries

  Scenario: Authenticated user opens index page
      Given As authenticated user
       When I open index page
       Then I see welcome text
        And I see 5 navigation links
        And I see link for list of categories
        And I see link for list of countries
        And I see link for adding series
        And I see link for adding categories
        And I see link for adding countries

  Scenario: Administrator opens index page
      Given As administrator
       When I open index page
       Then I see welcome text
        And I see 6 navigation links
        And I see link for list of categories
        And I see link for list of countries
        And I see link for adding series
        And I see link for adding categories
        And I see link for adding countries
        And I see link for viewing suspicious activities

