Narrative:
I as a user of S2
Want to be able to get Folder ID by folder path
In order to use it

Scenario: get FID by folder path
Given There is a folder in S2
When I request its FID by folder path
Then I get FID