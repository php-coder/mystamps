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

  Scenario: All fields should be mandatory
      Given As administrator
       When I open create country page
        And I fill field "Name (on English)" with value "" in create country form
        And I fill field "Name (on Russian)" with value "" in create country form
        And I submit create country form
       Then I see that field "Name (on English)" has error "Value must not be empty" in create country form
       And  I see that field "Name (on Russian)" has error "Value must not be empty" in create country form

  Scenario Outline: Country name should not be too short
      Given As administrator
       When I open create country page
        And I fill create country form with valid values
        And I fill field "<fieldName>" with value "<value>" in create country form
        And I submit create country form
       Then I see that field "<fieldName>" has error "Value is less than allowable minimum of 3 characters" in create country form
  Examples:
          | fieldName         | value |
          | Name (on English) | ee    |
          | Name (on Russian) | яя    |

  Scenario Outline: Country name should not be too long
      Given As administrator
       When I open create country page
        And I fill create country form with valid values
        And I fill field "<fieldName>" with value "<value>" in create country form
        And I submit create country form
       Then I see that field "<fieldName>" has error "Value is greater than allowable maximum of 50 characters" in create country form
  Examples:
          | fieldName         | value                                               |
          | Name (on English) | eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee |
          | Name (on Russian) | яяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяяя |

  Scenario Outline: Country name should be unique
      Given As administrator
       When I open create country page
        And I fill create country form with valid values
        And I fill field "<fieldName>" with value "<value>" in create country form
        And I submit create country form
       Then I see that field "<fieldName>" has error "Country already exists" in create country form
    Examples:
          | fieldName         | value  |
          | Name (on English) | Italy  |
          | Name (on Russian) | Италия |

  Scenario Outline: Country name should accept all valid characters
      Given As administrator
       When I open create country page
        And I fill create country form with invalid values
        And I fill field "<fieldName>" with value "<value>" in create country form
        And I submit create country form
       Then I see that field "<fieldName>" has no error in create country form
    Examples:
          | fieldName         | value                         |
          | Name (on English) | Valid-Name Country            |
          | Name (on Russian) | Ёё Нормальное-название страны |

  Scenario Outline: Country name should reject forbidden characters
      Given As administrator
       When I open create country page
        And I fill create country form with valid values
        And I fill field "<fieldName>" with value "<value>" in create country form
        And I submit create country form
       Then I see that field "<fieldName>" has error "<errorMessage>" in create country form
    Examples:
          | fieldName         | value               | errorMessage                                                     |
          | Name (on English) | S0m3+CountryN_ame   | Country name must consist only latin letters, hyphen or spaces   |
          | Name (on Russian) | Нек0торо3+наз_вание | Country name must consist only Russian letters, hyphen or spaces |

    Scenario Outline: Country name should not start or end with hyphen
        Given As administrator
         When I open create country page
          And I fill create country form with valid values
          And I fill field "<fieldName>" with value "<value>" in create country form
          And I submit create country form
         Then I see that field "<fieldName>" has error "Value must not start or end with hyphen" in create country form
      Examples:
            | fieldName         | value |
            | Name (on English) | -test |
            | Name (on English) | test- |
            | Name (on Russian) | -тест |
            | Name (on Russian) | тест- |

    Scenario Outline: Country name should be stripped from leading and trailing spaces
        Given As administrator
         When I open create country page
          And I fill create country form with valid values
          And I fill field "<fieldName>" with value " <value> " in create country form
          And I submit create country form
         Then I see that field "<fieldName>" has value "<value>" in create country form
      Examples:
            | fieldName         | value |
            | Name (on English) | t3st  |
            | Name (on Russian) | т3ст  |

    Scenario: Administrator creates a country
        Given As administrator
         When I open create country page
          And I fill field "Name (on English)" with value "Israel" in create country form
          And I fill field "Name (on Russian)" with value "Израиль" in create country form
          And I submit create country form
         Then I'm on a country info page
          And I see a header "Israel" on country info page

    # TODO: see issue #280
    @ignore
    Scenario: Country should be available for choosing after its creation
        Given As administrator
         When I open create country page
          And I fill field "Name (on English)" with value "Germany" in create country form
          And I fill field "Name (on Russian)" with value "Германия" in create country form
          And I submit create country form
         Then I open add series page
          And Field "Country" in create series form contains "Germany"
