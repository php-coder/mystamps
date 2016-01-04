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

  Scenario Outline: Quantity should be a valid value
      Given As administrator
       When I open add series page
        And I fill field "Quantity" with value "<value>" in add series form
        And I submit add series form
        And I see that field "Quantity" has error "<errorMessage>" in add series form
    Examples:
          | value | errorMessage                              |
          | NaN   | Invalid value                             |
          | 0     | Value must be greater than or equal to 1  |
          | 100   | Value must be less than or equal to 50    |

  Scenario Outline: Catalog numbers should accept valid values
      Given As administrator
       When I open add series page
        And I show up "Add information from stamps catalogues" section at add series page
        And I fill field "Michel" with value "<catalogNumbers>" in add series form
        And I fill field "Scott" with value "<catalogNumbers>" in add series form
        And I fill field "Yvert" with value "<catalogNumbers>" in add series form
        And I fill field "Gibbons" with value "<catalogNumbers>" in add series form
        And I submit add series form
       Then I see that field "Michel" has no error in add series form
       Then I see that field "Scott" has no error in add series form
       Then I see that field "Yvert" has no error in add series form
       Then I see that field "Gibbons" has no error in add series form
    Examples:
          | catalogNumbers |
          | 7              |
          | 7,8            |
          | 71, 81, 91     |
          | 1000           |
