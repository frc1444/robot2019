## First 1444 Robot code for Deep Space
The code for the robot for 2019 Destination: Deep Space
### NOTE
When you first clone this project from the internet, make sure that you run
```./gradlew build``` or ```gradlew.bat build``` so you can cache needed files.
Some times you must run ```gradlew downloadAll``` to get everything.
### TODO
* Rumble driver controller when operator places hatch/cargo
* Test out absolute encoders using voltage regulator thing
* Crack down on what's causing overruns. Try disabling encoder thread, see what happens
* Try configClosedLoopPeriod to see if making that higher decreases encoder count access time
* [DONE] Put orientation on Shuffleboard
### Conventions
* Use tabs in .java files
* Use 4 spaces in gradle configuration
* lowerCamelCase for methods and variables
* PascalCase for classes
* Use doubles instead of floats
* Parameters involving left and right should be in the order: left, right
* Only use ```this.``` when necessary