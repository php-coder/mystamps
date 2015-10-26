Feature: User creates country

  Scenario: Anonymous user opens page for country creation
      Given As anonymous user
       When I open create country page but I don't have enough permissions
       Then I see error message "Authorization required to access page"
        And I see error code "401"

  Scenario: Anonymous user opens non-existing country page
      Given As anonymous user
       When I open non-existing country page
       Then I see error message "Requested page not found"
        And I see error code "404"
