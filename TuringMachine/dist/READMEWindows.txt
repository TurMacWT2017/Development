********************************* Windows Installation Instructions **************************************

Installation of the machine to a Windows platform is fairly straightforward. Alternatively, you can also choose to run the program directly by simply double clicking on the included .EXE file.

The included .msi installer will function exactly as you are used to and only requires that you double click on the .msi file to begin the installation process. Once the install begins, wait for it to finish as you normally would. Once the installer finishes, you will be able to find the installed program within your start menu, or by typing "TuringMachine" into your search bar. On Windows 10, for instance, once installed the program can be launched simply by pressing the Windows key, typing "TuringMachine" and pressing enter.

You can also simply look for the “Turing Machine” folder within your start menu, and you will find the program. If you would like to create a shortcut to it at any point, simply right click the shortcut to the program in your start menu, click “Open File Location” and then copy paste the shortcut that is displayed to wherever else you would like a shortcut, for instance, “Desktop.”

To remove the installed program from your Windows Machine, navigate to Control Panel -> Add/Remove Programs. From there, find "TuringMachine" in your list of installed programs, and click "Uninstall"

Upgrading
-------------
It is recommended when upgrading to a newer version of the program to uninstall old versions first via the control panel. The uninstallation fairly quick, and it will ensure that the installation of an upgraded version is done quickly and properly. 

Have no fear, should you ever decide or need to upgrade your version of the Machine, your old user settings will still persist (assuming you are still on the same machine).

----------------------------------------------------------------------------------------------------------------------------

Developer’s Notes: 
Regarding Program shortcuts. It seems that without a properly signed package, the application can’t request that a shortcut is added automatically for the user, although the option to request one be created at install is a possible argument to the <fx:deploy>. 
Regarding the .exe file, there was an issue regarding the tool used to package the windows .exe file when attempts were made to compile it on Windows 10, 64 Bit, using Netbeans 8.2. The Inno Setup 5 tools would repeatedly fail to make a bundle, despite the ability of the WIX tools to create the .msi, which ultimately creates an .exe. Inno Setup Tools were verified to be on the path properly, and no solution for this bug was found. The included .exe file was installed pulled from the .exe created by the .msi on a Windows 10 64 Bit System, but should (theoretically) still work fully.




