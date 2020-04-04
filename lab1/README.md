# Lab 1 
## Part 1
1. Wireshark v 3.2.2 was installed by visiting the wireshark website and downloading the application.
2. Picked my programming language, C, since I did CS50 last term. 
3. For this lab, I'm using vim along with VSCode as a text editor and gcc as a compiler. VSCode is a lot easier to use for writing most of the code, then I use vim to fix the bugs and errors. I use vim in the terminal on my computer, which is also where I compile my code using gcc. 

A. The browser response was 
ERR_INVALID_HTTP_RESPONSE
This happened because the port was not expecting the text input that was typed in in the terminal. It gave this response after typing "hi \n\n hi \n".
The response on Wireshark is in the lab 1 folder, alomg with a screenshot of the webpage response.

B. problemB.c contains the code that was used to send my name to the localhost port 8099 with TCP protocol. It creates the socket, binds, listens, then accepts, then sends the message, exiting with code 1 if any steps are unsuccessful. problemB.pcapng contains the packets. 

C. problemC.c contains the code that was used to send my name to the localhost port 2020 with UDP protocol. it uses most of the same steps, but SOCK_DGRAM is used to set up the socket instead of SOCK_STREAM. For some reason, the UDP packets aren't working on my wireshark, but the packets used are in the problemC.pcapng wireshark file in the folder. 
