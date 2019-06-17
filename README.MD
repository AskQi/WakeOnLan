This little programs is (being) created for the company VRCafeHaarlem.
It should be used to hibernate and wake from hibernate windows pc's.

Usage
-

1: this program is made to be an executable on a windows system, turning on/off other windows pc's connected via LAN.

2: every pc needs to have psshutdown.exe installed, host and remotes.  https://docs.microsoft.com/en-us/sysinternals/downloads/psshutdown

3: every remote pc needs have the following registry change: https://prnt.sc/o2wl3j

4: every remote pc needs to be publicly accessible on the lan network it is connected to. 

    So file sharing and printer sharing have to be turned on
5: every remote pc needs to have remote registry service turned ON: https://prnt.sc/o2wmfx

6: every remote pc needs to have a username and password!

7: for remote pc note down the following: 
    - networkname: usually looks something like *DESKTOP-D4CU42G*
    - the username
    - the password
    - the macadress (found using ipconfig)
    - in a next update the local ipadres would also be nice to save
    
8: add every value for every remote pc you want to a textfile called application.properties and save it to a directory.
    the default directory is c:/vr/application.properties. You can just change this in the code if you want.
    
9: add the file from step 2 (psshutdown.exe) to the same directory as step 8.

You should be all set. Compile the application, making sure you point to the correct directories as mentioned in step 8 and 9
If you filled all the remote pc properties in correctly, you will be able to shutdown every pc you added.
