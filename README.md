![logo](https://user-images.githubusercontent.com/13294361/155899840-8fbc423c-0b2b-487e-a702-eda9aca40399.png)

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/nomisma)

[![](http://cf.way2muchnoise.eu/290952.svg)](https://www.curseforge.com/minecraft/mc-mods/nomisma)
[![](http://cf.way2muchnoise.eu/versions/290952.svg)](https://www.curseforge.com/minecraft/mc-mods/nomisma)

Simple Currency mod featuring RPG type coins and coin purses with easy recipes.  Generally meant to be used for adventure maps or crafted worlds and economies.

### Credits
Programming: Ferreusveritas, Contains code from Vazkii's Autoreg library(WTFPL).
Art: Ferreusveritas.
Additional art assets: Thomas Feichtmeir(Cyangmou) for [Fantasy v1: 021 - Gold Pieces] pixel art asset(and contained derivative works)"

### Compiling
* Clone the repository.
* Open a command prompt/terminal to the repository directory.
* Run `gradlew build` on Windows, or `./gradlew build` for MacOS or Linux.
* The built jar file will be in `build/libs/`.

### Developing

Step 1: Open your command-line and browse to the folder where you cloned this project

Step 2: You're left with a choice.
If you prefer to use Eclipse:
1. Run the following command: `gradlew genEclipseRuns` (`./gradlew genEclipseRuns` if you are on Mac/Linux)
2. Open Eclipse, Import > Existing Gradle Project > Select Folder 
   or run `gradlew eclipse` to generate the project.

If you prefer to use IntelliJ:
1. Open IDEA, and import project.
2. Select your build.gradle file and have it import.
3. Run the following command: `gradlew genIntellijRuns` (`./gradlew genIntellijRuns` if you are on Mac/Linux)
4. Refresh the Gradle Project in IDEA if required.

If at any point you are missing libraries in your IDE, or you've run into problems you can 
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
(this does not affect your code) and then start the process again.
