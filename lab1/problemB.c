#include <unistd.h> 
#include <stdio.h> 
#include <stdlib.h> 
#include <string.h> 
#include <netinet/in.h> 
#include <sys/socket.h> 
#include <sys/types.h> 

#define PORT 8099 
#define MESSAGE_MAX 50

int main(void) {

    printf("start");
		
    int serverSock, sock; 
    struct sockaddr_in serverAddress, clientAddress; 
    
    int length = sizeof(serverAddress); 
    char *message = "HTTP/1.1 200 OK\n\nEdie"; 
       
    if((serverSock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        printf("error opening socket");
    } else printf("opened socket");

    memset(&serverAddress,0, sizeof(serverAddress));
    
    /*defining server properties*/
    serverAddress.sin_family = AF_INET; 
    serverAddress.sin_addr.s_addr = INADDR_ANY; 
    serverAddress.sin_port = htons(PORT); 
       
    /*attatching server to communication port*/
    if (bind(serverSock,(struct sockaddr *)&serverAddress,sizeof(serverAddress)) < 0) { 
        printf("bind failed"); 
        exit(1); 
    } 
    
    if (listen(serverSock,5) < 0) 
    { 
        printf("listen error"); 
        exit(1); 
    } 
    
    if ((sock = accept(serverSock,(struct sockaddr *)&serverAddress,(socklen_t*)&length)) < 0) { 
        printf("accepting error"); 
        exit(1); 
    } 
    
    /*
    int success = write(sock,message,strlen(message)); 
    
    if(success >= 0) {
        printf("Name sent\n"); 
    }
    else printf("failure sending message");
*/
    send(serverSock,message,strlen(message), 0);
    return 0; 
} 
