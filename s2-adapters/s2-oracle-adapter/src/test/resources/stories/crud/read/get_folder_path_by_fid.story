Narrative:
I as a user of S2
Want to be able to get folder path by  Folder ID
In order to use it

Scenario: get folder path by FID
Given There is a folder in S2
When I request folder path by its FID
Then I get folder path