## First 1444 Robot code for Deep Space
The code for the robot for 2019 Destination: Deep Space
### NOTE
When you first clone this project from the internet, make sure that you run
```./gradlew build``` or ```gradlew.bat build``` so you can cache needed files.
Some times you must run ```gradlew downloadAll``` to get everything.
### TODO
* Rumble driver controller when operator places hatch/cargo
* Make vision work
* Update CTRE firmware on Talons
### Done
* Fixed switching camera
* Test the MatchScheduler class
* Add option in auto modes so you can choose whether or not to use vision
### TODO at competition
* Put gyro on robot
* Make sure RPi is on right power port (5V 2A)
* Put 4 cameras on the robot
* Update CTRE firmware on Talons and Victors
### Changes made after bag 'n tag
* Deactivated hatch stow forward limit switch
* Made sure that after manual boom is released, it locks the position
* Made switching cameras work
* Currently testing vision
### Conventions
* Use tabs in .java files
* Use 4 spaces in gradle configuration
* lowerCamelCase for methods and variables
* PascalCase for classes
* Use doubles instead of floats
* Parameters involving left and right should be in the order: left, right
* Only use ```this.``` when necessary
* Use Shuffleboard for as much as possible and use SmartDashboard for unimportant debugging


### TODO Next year
* Create helper library
* Create interface to be used for all motors