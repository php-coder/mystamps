Feature: User opens series info page, he should see a button for adding series to collection.

  @in12
  Scenario:
    Given as a user
    When I add series to my collection
    Then I am on the page with my collection
    And I see that this series have been added to the collection