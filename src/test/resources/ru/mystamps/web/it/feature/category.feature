Feature: User creates category

  Scenario: Anonymous user opens page for category creation
      Given As anonymous user
       When I open create category page but I don't have enough permissions
       Then I see error message "Authorization required to access page"
        And I see error code "401"

  Scenario: Anonymous user opens non-existing category page
      Given As anonymous user
       When I open non-existing category page
       Then I see error message "Requested page not found"
        And I see error code "404"
