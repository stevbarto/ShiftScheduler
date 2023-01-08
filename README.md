# ShiftScheduler

-About ShiftScheduler

This is the approved version of my computer science capstone project at WGU.  This application takes historic shift manning data and runs shift variables 
through analysis via a K-Means algorithm to provide guiding insights to the user for scheduling future shifts.  The insights are intended to provide 
information to the scheduler, allowing them to schedule shifts that optimize shift variables.  Some features in this application are not complete.  It was
written primarily to satisfy capstone project success criteria (I wanted to graduate) and provide a platform for future experimentation and development of 
concepts contained in the project.

-Data

The shift data in this project is based on actual shift data, but any industry internal or personally identifiable information was removed.  The actual 
data, while based on actual shift structures, has been rendered arbitrary for honing the algorithm's use.

-Environment

This project was built on Java 11 as a maven project.  It utilizes JavaFX for the interface and Java-ML for the analysis algorithm.  It was developed in 
IntelliJ Idea CE on MacOS Monterey 12.5.xx.

-Loading and Running

Once the ShiftScheduler project is loaded into the IDE and dependencies are satisfied you can run it from the IDE.  There is no login function currently 
implemented, simply click login.  The first tab is the informational tab displaying data.  The second tab is the scheduling tab with rough basic 
functionality.  The current version contains bugs as you play with various controls.
