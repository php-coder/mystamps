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

  Scenario Outline: Category name should not be too short
      Given As administrator
       When I open create category page
        And I fill create category form with valid values
        And I fill field "<fieldName>" with value "<value>" in create category form
        And I submit create category form
       Then I see that field "<fieldName>" has error "Value is less than allowable minimum of 3 characters" in create category form
  Examples:
          | fieldName         | value |
          | Name (on English) | ee    |
          | Name (on Russian) | яя    |

