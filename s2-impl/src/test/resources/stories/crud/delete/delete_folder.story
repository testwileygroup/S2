Narrative:
I as a user of S2
Want to be able to delete multiple files using a single operation
In order to get rid of unused files

Scenario: delete multiple files in S2
Given There is a folder with multiple files and/or folders
When I delete it from S2
Then Folder and its content is not managed by S2