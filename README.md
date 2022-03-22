# Multi-Room Audio
Multi-Room Audio is a system that uses the context to reproduce music in the indoor location where the user is found. It uses radio-fingerprinting to locate the user position inside the house and consequently deciding which speaker needs to be activated.

### Feautures
* Multi-platform: Android, Windows and Linux
* KNN, WKNN localization algorithms
* Easy to use interface


### How to build
````
$> cd ui
$> npm install
$> npm run build
$> cd ../pc
$> mvn clean package
````

### How to run
You don't need to do the build to run the programs, there are pre-compiled executables.
#### Server
````
$> cd pc
# On linux
$> ./run.sh server -m \music\directory -r
# On Windows
$> .\run.bat server -m /music/directory -r
````

#### Client
````
# On linux
$> ./run.sh client
# On Windows
$> .\run.bat client
````

#### Speaker
````
# On linux
$> ./run.sh speaker
# On Windows
$> .\run.bat speaker
````