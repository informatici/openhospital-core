# Open Hospital - Core
[![Java CI](https://github.com/informatici/openhospital-core/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/informatici/openhospital-core/actions?query=workflow%3A%22Java+CI+with+Maven%22)

This is the Core component of [Open Hospital][openhospital]: it contains the business logic and the data abstraction layer.  
The Core component is used by the [Java Swing desktop GUI][openhospital-gui], and by the [web UI][openhospital-ui] (through the [API component][openhospital-api]).

## How to build

After having installed Java JDK 8+ and Maven, to build this project issue:  

    mvn package

To use the Core component in the other projects, you'll need to install it locally with:

    mvn install
    
To run the tests simply issue:

    mvn test

Tests are run against an in-memory database (H2).  
To test the application against MySQL, you can change [`database.properties`][database.prop] and run the Docker container in the root folder with:

    # clean previous build
    docker compose rm --stop --volumes --force
    docker-compose up


## How to run Open Hospital

To run Open Hospital, you'll need a user interface, which is provided in the [GUI][openhospital-gui] and in the [UI][openhospital-ui] projects.  
Please follow the instructions in the documentation of those repositories.

## How to contribute

You can find the contribution guidelines in the [Open Hospital wiki][contribution-guide].  
A list of open issues is available on [Jira][jira].

## Community

You can reach out to the community of contributors by joining 
our [Slack workspace][slack] or by subscribing to our [mailing list][ml].

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

 [openhospital]: https://www.open-hospital.org/
 [openhospital-gui]: https://github.com/informatici/openhospital-gui
 [openhospital-ui]: https://github.com/informatici/openhospital-ui
 [openhospital-api]: https://github.com/informatici/openhospital-api
 [contribution-guide]: https://openhospital.atlassian.net/wiki/display/OH/Contribution+Guidelines
 [jira]: https://openhospital.atlassian.net/jira/software/c/projects/OP/issues/
 [database.prop]: https://github.com/informatici/openhospital-core/blob/develop/src/test/resources/database.properties
 [slack]: https://join.slack.com/t/openhospitalworkspace/shared_invite/enQtOTc1Nzc0MzE2NjQ0LWIyMzRlZTU5NmNlMjE2MDcwM2FhMjRkNmM4YzI0MTAzYTA0YTI3NjZiOTVhMDZlNWUwNWEzMjE5ZDgzNWQ1YzE
 [ml]: https://sourceforge.net/projects/openhospital/lists/openhospital-devel
