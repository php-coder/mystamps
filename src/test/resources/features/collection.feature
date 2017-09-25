Feature: User has a collection of stamps. He can add series of stamps to a collection.

  Scenario:

    Given as a user
    When I add series to my collection
    Then I am on the page with my collection
    And I see that this series has been added to the collection
