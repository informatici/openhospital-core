# OpenHospital-core
[![Java CI](https://github.com/informatici/openhospital-core/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/informatici/openhospital-core/actions?query=workflow%3A%22Java+CI+with+Maven%22)

OpenHospital 2.0 (ISF OpenHospital web version) - WIP

**How to build with Maven:**
_(requires Maven 3.2.5 or lesser installed and configured)_

    mvn clean install
    
You need a local (or remote) MySQL server where to run the JUnit tests. Simply run:

	docker-compose up 

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
