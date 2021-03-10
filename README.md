# Chat-Exercise
Group C1 - CphBusiness
Bornholm


## Getting started:
Clients should set ip to “x"
And port to x

This can easily be done by opening IDE and navigating to Run>Edit Configurations and selecting Client from where you can set “Program arguments”.
If using a pre-build version, it should be set as startup arguments.

A server must be available for testing the client.


## Design choice:
Continued design pattern as demonstrated by the mentor, 
Splitting the server-side into fewer subclasses and unique methods in order to accomplish a simplistic and easy to navigate design.
The server is running as a single thread, creating a new thread for each client connecting. 
The thread is then closed, once a client logs off, or an unknown client is disconnected.
Error to follow the demanded syntax results in an instant disconnect from the server.

The same design applies to the client, except the client is run as two simultaneous threads each responsible for receiving and sending connections to the connected server.


## Who did what?
The project was a group effort.
Each team member has actively contributed to the success of the software.
Although it is not accurately represented by github, no commit was made without sparring with at least one other team member.
As such, github alone should not be used as an indicator for the students activity. 


## Testing:
During the test phase with group C3, the group received feedback regarding how the server behaved when a user disconnected without first sending the “CLOSE” command.
We also received feedback on how our exception handling might be misleading for some individuals.

## Startcode by Lars - 
https://github.com/cph-dat-sem2/chat-server-startcode
### Quick Start Project for the Chat - Server

Simple Maven Project which can be used for the Chat-CA 

Using this project as your start code will make deploying your server (the jar-file) to Digital Ocean a "no brainer" if you follow the instructions given here

https://docs.google.com/document/d/1aE1MlsTAAYksCPpI4YZu-I_uLYqZssoobsmA-GHmiHk/edit?usp=sharing 

