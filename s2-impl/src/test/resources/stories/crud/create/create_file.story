Narrative:
I as a user of S2
Want to be able to create a single file
In order to store it in persistent place

Scenario: create a single file in S2
Given I have a file
When I create it in S2 storage
Then I can retrieve it from there

