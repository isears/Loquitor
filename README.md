MercuryChat
===========

A simple chat server/client bundle written in Java. The client includes a minimal GUI that connects to the server and allows the user to talk to all other clients connected to the same server. The server is a command line application that manages information flow between two or more clients.

Installation Instructions (client)
----------------------------------
* Prerequisites: This application should work on any computer that is running Java 1.8 or better, so just make sure       typing the 'java -version' command into your command line returns 'java version 1.8.x.x' or something to that effect.

* Installation: Installation is as simple as downloading the contents of the                  MercuryChat/MercuryClient/artifacts/out/ChatClient directory and running the ChatClient.jar file inside. Make sure the   .xml files (Protocol.xml, pref.xml) are in the same directory as the .jar file, else you might run into problems. Of     course, all the code required to compile your own version of the ChatClient is present on this repo, so feel free to    download it and compile yourself.

Installation Instructions (server)
----------------------------------
* Prerequisites: It is recommended that you have Java 1.8 or better. 

* Network Configuration: If you wish to access the server from anywhere with an internet connection, make sure the        machine on which it's running has a public ip address. If the machine does not have a public IP address, but you have   admin access to the local router, configure port forwarding on the router such that port 14471 on the router is         forwarded to the machine on which the server is running. If the machine does not have a public ip address AND you       cannot configure port forwarding on your router, the server can still run, but it will only be accessible from inside   the local network. If the machine has a public IP address, the address of the server will be the address of the         machine on which it is running. If port forwarding is configured from the router, the address of the server will be     the address of the router. If the server running without a public IP address and without port forwarding from the       router, the address of the server will be the local address of the machine it is running on, but it will only be        accessible from within the local network.

* Installation: Installation is as simple as downloading the contents of the       MercuryChat/MercuryServer/artifacts/out/ChatServer directory and running the ChatServer.jar file inside. Make sure the    .xml files (Protocol.xml, ServerParams.xml) are in the same directory, else you might run into problems. As with the    client, all the source code necessary to compile your own version of the server is up on this repo, so feel free to     download the .java files and compile yourself.

Contact/Liscencing
------------------
I wrote this application as an exercise in GUI and network programming, but if you find any part of it useful, feel free to use it for your own projects. This application, however, comes with ABSOLUTELY NO WARRANTY, to the extent permitted by applicable law. Send questions/comments/concerns to isaac.j.sears@gmail.com
