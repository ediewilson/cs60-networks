#include <unistd.h> 
#include <stdio.h> 
#include <stdlib.h> 
#include <string.h> 
#include <netinet/in.h> 
#include <sys/socket.h> 

#define PORT 7777 
#define MESSAGE_MAX 50

int main(void) {
       
	system("nc -l 8099");
	system("HTTP/1.1 200 OK\n\nEdie\n");

	return(0);
}
