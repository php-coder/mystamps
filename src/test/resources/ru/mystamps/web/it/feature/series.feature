Feature: User or admin add series

  Scenario: Anonymous user opens page for adding series
      Given As anonymous user
       When I open add series page but I don't have enough permissions
       Then I see error message "Authorization required to access page"
        And I see error code "401"

  Scenario: Anonymous user opens non-existing series page
      Given As anonymous user
       When I open non-existing series page
       Then I see error message "Requested page not found"
        And I see error code "404"
