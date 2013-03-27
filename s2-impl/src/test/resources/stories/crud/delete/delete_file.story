Narrative:
I as a user of S2
Want to be able to delete a single file
In order to get rid of unused file

Scenario: delete a single file in S2
Given There is a file in S2
When I delete it from S2
Then File is not managed by S2 cannot be retrieved from S2