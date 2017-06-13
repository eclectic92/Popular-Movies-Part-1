# Popular Movies Part 1

##Phase one of the udacity android project "Popular Movies"

NOTE: To run locally, please replace the key "tmdbApiKey" in gradle.properties with your API key

Main activity displays a grid of popluar movies to the user, who can then click the action bar menu to toggle between
poplar movies or top rated movies from themoviedb.org.

Clicking a movie's poster will take the user to a details screen which will display an individual movie's:
1. title
2. poster
3. release date
4. running time
5. user vote average from themoviedb.org
6. MPAA rating icon if data is available
7. plot overview if available

User's state is saved on transition between activities, rotation, and when clicking the Android home button so that
fewer network calls will be made to the API at the movie database. Additionally the app will alert the user if 
there is no current network connection or it there is a problem retrieving a list of movies.

###See screenshots in wiki

###Attributions
UI Styling:
Layout and elements mixed and matched from suggested UI, but mostly inspired by Plex
and thetvdb.com

MPAA rating icons:
Taken from wikicommons (with public domain licenses) and re-tooled by hand editing SVG files

Async tasks as class files:
https://stackoverflow.com/questions/26202568/android-pass-function-reference-to-asynctask

Margins for grid view:
http://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing

Network http calls and url builder:
Adapted from the udacity Sunshine app and exercises

Storing API key in config file:
https://technobells.com/best-way-to-store-your-api-keys-for-your-android-studio-project-e4b5e8bb7d23

ConnectionChecker:
https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out

###Additional features will be added in part 2

Master branch lints clean and passes code inspection in Android Studio
