Feature: User opens series info page, he should see a button for adding series to collection.

  @in12
Scenario:
Given user opens series info page
Then user should see a button for adding series to collection
When user clicks on the button for adding series to collection
Then user should be redirected to collection page
And  series on info page should be listed.