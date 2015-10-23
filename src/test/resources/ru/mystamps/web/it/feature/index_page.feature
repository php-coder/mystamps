Feature: User on index page

  Scenario: Anonymous user open index page
      Given As anonymous user
       When I open index page
       Then I see welcome text
        And I see 2 navigation links
        And I see link for list of categories
        And I see link for list of countries
        But I don't see link for adding series
        But I don't see link for adding categories
        But I don't see link for adding countries

  Scenario: Authenticated user open index page
      Given As authenticated user
       When I open index page
       Then I see welcome text
        And I see 3 navigation links
        And I see link for list of categories
        And I see link for list of countries
        And I see link for adding series
        But I don't see link for adding categories
        But I don't see link for adding countries

