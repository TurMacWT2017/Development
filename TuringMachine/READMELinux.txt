***********The included installers are compiled for 64-bit systems************
***********A 64 bit platform is required to run them**************************


System requirements for these packages include only a 64-bit Linux OS. *No external Java Dependencies are required*


This README contains information on the included Linux Distributables. Packaged within this Linux Bundle you will find two installers built for Linux systems. One of these is a .deb file and the other is a .rpm file.


Depending on your distribution of Linux, you will want to install one of thse files. These can usually be installed via your distributions built in software center, making them very accessible to the standard user with no java or command line knowledge. Alternatively, commands for installing on a few major distributions can be found below, as well as any notes and solutions on any issues that were encountered during testing.


Developers note:
The included .deb file is not digitally signed, as the team did not posess a means of doing so, on some systems, a user might receiving a warning about installing an unsigned .deb file. These warnings can be ignored, or solved should the package be able to be properly signed in the future by someone with the resources to do so.


---------------------------------------------------------------------------


Fedora Linux


The built in software manager included in Fedora Linux is capable of installing the program, however, should you desire to install it manually, please reference the below commands.


First navigate to the direction containing the .rpm, then issue the following command:


sudo dnf install turingmachine-1.0-1.x86_64.rpm


Developers note:
In testing, Fedora's built in software manager was found to not be able to properly uninstall the machine should you want to remove the machine or remove it to install an updated version. This can be remedied fairly easy by uninstalling from the command line with the following command:


sudo dnf remove turingmachine-1.0-1.x86_64.rpm


If for some reason the package is not found, try locating the package name with the following command:


rpm -qa | grep "turing"


---------------------------------------------------------------------------


Ubuntu Linux 


Ubuntu Software Center is capable of installing the program from the .deb package, however, you will receive a quality warning do to the package not being signed. You can ignore this warning and proceed with installation anyway.


As a recommendation, GDebi Package manager is both lighter weight, faster, and seems to handle installation and uninstallation of the package with much more elegance and success than the Ubuntu Software Center. (On 14.04, on which this was first tested, it is possible the software center could be improved in newer releases)


Should you desire to install the .deb file via the command line, you can use the following commands:


First navigate to the folder contained the .deb package, and open a terminal. Input the following command: 


sudo dpkg -i turingmachine-1.0.deb 


Alternatively, the package could be installed with any number of existing package managers, such as synaptic.


Developers note:
In testing, it was found that Ubuntu's Software Center, similarly to Fedora's, seems to struggle in uninstalling the package if you desire to do so. The preferred option on Ubuntu would be to remove the package via Synaptic Package Manager. Alternatively, it could be also be easily uninstalled from the command line with the following command


sudo dpkg -r turingmachine