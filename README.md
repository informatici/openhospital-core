# OpenHospital-core
[![Java CI](https://github.com/informatici/openhospital-core/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/informatici/openhospital-core/actions?query=workflow%3A%22Java+CI+with+Maven%22)

OpenHospital 2.0 (ISF OpenHospital web version) - WIP

**How to build with Maven:**
_(requires Maven 3.2.5 or lesser installed and configured)_

    mvn clean install
    
To run the JUnit tests simply run:

	mvn test

**How to launch the software:**

You need a GUI (Graphic User Interface) in order to use the core:

* clone [OpenHospital-gui](https://github.com/informatici/openhospital-gui) for a Java Swing interface
* follow the instructions in the related README.md


**Rebuild local database:**
If you want to rebuild local db you should follow these steps:

    docker-compose down --rmi all
    docker-compose up [-d]*  
    
    
    * -d optional for detach 


# How to contribute

Please read the OpenHospital [Wiki](https://openhospital.atlassian.net/wiki/display/OH/Contribution+Guidelines)

See the Open Issues on [Jira](https://openhospital.atlassian.net/issues/)

## Code style

This project uses a consistent code style and provides definitions for use in both IntelliJ and Eclipse IDEs.

<details><summary>IntelliJ IDEA instructions</summary>

For IntelliJ IDEA the process for importing the code style is:

* Select *Settings* in the *File* menu
* Select *Editor*
* Select *Code Style*
* Expand the menu item and select *Java*
* Go to *Scheme* at the top, click on the setting button by the side of the drop-down list
* Select *Import Scheme*
* Select *IntelliJ IDE code style XML*
* Navigate to the location of the file which relative to the project root is:  `.ide-settings/idea/OpenHospital-code-style-configuration.xml`
* Select *OK* 
* At this point the code style is stored as part of the IDE and is used for **all** projects opened in the editor.  To restrict the settings to just this project again select the setting button by the side of the *Scheme* list and select *Copy to Project...*. If successful a notice appears in the window that reads: *For current project*.

</details>

<details><summary>Eclipse instructions</summary>

For Eclipse the process requires loading the formatting style and the import order separately.

* Select *Preferences* in the *Window* menu
* Select *Java*
* Select *Code Style* and expand the menu
* Select *Formatter*
* Select the *Import...* button
* Navigate to the location of the file which relative to the project root is:  `.ide-settings/eclipse/OpenHospital-Java-CodeStyle-Formatter.xml`
* Select *Open*
* At this point the code style is stored and is applicable to all projects opened in the IDE.  To restrict the settings just to this project select *Configure Project Specific Settings...* in the upper right.  In the next dialog select the *openhospital* repository and select *OK*.  In the next dialog select the *Enable project specific settings* checkbox.  Finally select *Apply and Close*.
* Back in the *Code Style* menu area, select *Organize Imports*
* Select *Import...*
* Navigate to the location of the file which relative to the project root is:  `.ide-settings/eclipse/OpenHospital.importorder`
* Select *Open*
* As with the formatting styles the import order is applicable to all projects.  In order to change it just for this project repeat the same steps as above for *Configure Project Specific Settings...*
 
</details> 
