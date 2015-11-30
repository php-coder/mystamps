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

  Scenario: Some fields should be mandatory
      Given As administrator
       When I open add series page
        And I fill field "Category" with value "" in add series form
        And I fill field "Quantity" with value "" in add series form
        And I fill field "Image" with value "" in add series form
        And I submit add series form
       Then I see that field "Category" has error "Value must not be empty" in add series form
        And I see that field "Quantity" has error "Value must not be empty" in add series form
        And I see that field "Image" has error "Value must not be empty" in add series form
