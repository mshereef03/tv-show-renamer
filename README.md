# Tv Show Renamer

This is a Java program that renames episode/subtitle files for TV shows based on the episode's name obtained from an online movie database (OMDb).

The program expects the user to have all files for the TV show in a specific folder and to provide four parameters:

- folderPath: The path to the folder containing the subtitle files.
- extension: The file extension of the subtitle files that need to be renamed.
- showName: The name of the TV show for which the subtitle files need to be renamed.
- seasonNum: The season number of the TV show for which the subtitle files need to be renamed.

The program then connects to the OMDb API using the provided API key and fetches the episode name for each file using the show name, season number, and an episode counter. The episode counter is incremented after every successful fetch.

The new file name is created using the fetched episode name and the provided season number and episode counter. If the file is successfully renamed, the old file name and the new file name are printed to the console. If the file cannot be renamed, an error message is printed.

Overall, this program automates the process of renaming subtitles/episodes for a TV show, making it easier for users to manage their media libraries.
