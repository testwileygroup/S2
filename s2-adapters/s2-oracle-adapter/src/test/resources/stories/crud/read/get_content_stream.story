Narrative:
I as a user of S2
Want to be able to get content of specific file stored in S2
In order to use it

Scenario: list files for specific folder and its subfolders
Given There is a folder with multiple files and/or folders
When I request a list of files for it
Then I get the list of all files from the folder and its subfolders