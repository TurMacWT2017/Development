**************************** Turing Machine V1.0 Structure, Information, Installation Instructions ****************************************
This folder contains everything that you will need to get started using Version 1.0 of the Machine.
This README describes the folder contents, their purposes, and the appropriate README files with instructions on how to use them.

System Requirements: 64 Bit OS for all installers
Operating Systems Supported: All major desktop platforms. Windows, Linux, Mac OS
Other Requirements: Java Version 7+, JavaFX 8 (jar only, installers: none)
Included Deployable's: Jar file and supporting documentation, Linux installable .deb and .rpm files, .msi installer for Windows

This folder and it's files, and subfolders are described below:
----------------------------------------------------------------
TuringMachine.jar - jar version of the Machine, can be run from the command line on any system that meets the above other requirements without installation. 

Bundles Folder -
Instructions for running the jar file are included in a separate README, should you desire to run it. Alternatively, you can quickly and easily install the machine on your platform using the included installers. These installers work with the existing software tools in your OS you are already familar with, and require no java or development knowledge to install. These installers are ideal for deploying the project to other users who are interested in using the machine for learning or desire a familar and straightforward experience, without worrying about any external dependecies. These installers have **no external requirements** and are fully self contained, allowing them to run even on systems with no JRE or without the appropriate JRE installed. These installers can be found within the bundles folders.

*All installers provided are compiled and targeted for 64 Bit Systems* 

For more information on each systems installers, see the included README's in the folder. For Linux platforms, see "READMELinux.txt." For windows platforms, see "READMEWindows.txt"

Developer’s note:
Currently, as is noted in our documentation, there exists (to our knowledge) only 2 bugs within JavaFX that our causing bugs within the program. 
You will notice on Linux the following bug: https://bugs.openjdk.java.net/browse/JDK-8087981
This is slated to be fixed in a future release. However, it is HIGHLY important to note that because the installers are self contained packages, once this bug is fixed, the installers might need to be recompiled for the target platform to include the bug fix, since Self Contained Applications contain their own JRE’s and have no outside dependencies. Once an updated installer is made however, it is very easy for the user to update their version of the program by uninstalling, and re-installing the program (just as they are used to with other programs). Instructions for un-installation are included in all README documentation as well.


