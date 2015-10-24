Feature: User creates category

  Scenario: Anonymous user opens page for category creation
      Given As anonymous user
       When I open create category page
       Then I see error message "Authorization required to access page"
        And I see error code "401"

