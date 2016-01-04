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

  Scenario: All fields should be mandatory
      Given As administrator
       When I open create category page
        And I fill field "Name (in English)" with value "" in create category form
        And I fill field "Name (in Russian)" with value "" in create category form
        And I submit create category form
       Then I see that field "Name (in English)" has error "Value must not be empty" in create category form
        And I see that field "Name (in Russian)" has error "Value must not be empty" in create category form

  Scenario Outline: Category name should not be too short
      Given As administrator
       When I open create category page
        And I fill create category form with valid values
        And I fill field "<fieldName>" with value "<value>" in create category form
        And I submit create category form
       Then I see that field "<fieldName>" has error "Value is less than allowable minimum of 3 characters" in create category form
  Examples:
          | fieldName         | value |
          | Name (in English) | ee    |
          | Name (in Russian) | яя    |

  Scenario Outline: Category name should not be too long
      Given As administrator
       When I open create category page
        And I fill create category form with valid values
        And I fill field "<fieldName>" with value "<value>" in create category form
        And I submit create category form
       Then I see that field "<fieldName>" has error "Value is greater than allowable maximum of 50 characters" in create category form
  Examples:
          | fieldName         | value                                               |
          | Name (in English) | eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee |
          | Name (in Russian) | яяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяя |

  Scenario Outline: Category name should be unique
      Given As administrator
       When I open create category page
        And I fill create category form with valid values
        And I fill field "<fieldName>" with value "<value>" in create category form
        And I submit create category form
       Then I see that field "<fieldName>" has error "Category already exists" in create category form
    Examples:
          | fieldName         | value    |
          | Name (in English) | Animals  |
          | Name (in Russian) | Животные |

  Scenario Outline: Category name should accept all valid characters
      Given As administrator
       When I open create category page
        And I fill create category form with invalid values
        And I fill field "<fieldName>" with value "<value>" in create category form
        And I submit create category form
       Then I see that field "<fieldName>" has no error in create category form
    Examples:
          | fieldName         | value               |
          | Name (in English) | Valid-Name Category |
          | Name (in Russian) | Категория Ё-ё       |

  Scenario Outline: Category name should reject forbidden characters
      Given As administrator
       When I open create category page
        And I fill create category form with valid values
        And I fill field "<fieldName>" with value "<value>" in create category form
        And I submit create category form
       Then I see that field "<fieldName>" has error "<errorMessage>" in create category form
    Examples:
          | fieldName         | value              | errorMessage                                                      |
          | Name (in English) | S0m3+CategoryN_ame | Category name must consist only latin letters, hyphen or spaces   |
          | Name (in Russian) | Категория+1_2_3    | Category name must consist only Russian letters, hyphen or spaces |

  Scenario Outline: Category name should not start or end with hyphen
      Given As administrator
       When I open create category page
        And I fill create category form with valid values
        And I fill field "<fieldName>" with value "<value>" in create category form
        And I submit create category form
       Then I see that field "<fieldName>" has error "Value must not start or end with hyphen" in create category form
    Examples:
          | fieldName         | value |
          | Name (in English) | -test |
          | Name (in English) | test- |
          | Name (in Russian) | -тест |
          | Name (in Russian) | тест- |

  Scenario Outline: Category name should be stripped from leading and trailing spaces
      Given As administrator
       When I open create category page
        And I fill create category form with valid values
        And I fill field "<fieldName>" with value " <value> " in create category form
        And I submit create category form
       Then I see that field "<fieldName>" has value "<value>" in create category form
    Examples:
          | fieldName         | value |
          | Name (in English) | t3st  |
          | Name (in Russian) | т3ст  |

  Scenario: Administrator creates a category
      Given As administrator
       When I open create category page
        And I fill field "Name (in English)" with value "Mushrooms" in create category form
        And I fill field "Name (in Russian)" with value "Грибы" in create category form
        And I submit create category form
       Then I'm on a category info page
        And I see a header "Mushrooms" on category info page

  Scenario: Category should be available for choosing after its creation
      Given As administrator
       When I open create category page
        And I fill field "Name (in English)" with value "Flowers" in create category form
        And I fill field "Name (in Russian)" with value "Цветы" in create category form
        And I submit create category form
       Then I open add series page
        And Field "Category" in create series form contains "Flowers"
