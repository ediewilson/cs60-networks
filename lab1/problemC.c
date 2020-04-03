#include <unistd.h> 
#include <stdio.h> 
#include <stdlib.h> 
#include <string.h> 
#include <netinet/in.h> 
#include <sys/socket.h>
#include <arpa/inet.h> 
#include <netinet/in.h> 

#define PORT 2020

int main (void) {

    int sockfd, sockServ; 
    struct sockaddr_in serverAddress, clientAddress;  
    char *message = "HTTP/1.1 200 OK\n\nEdie\n"; 

    if((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
        printf("error opening socket");
    }

    memset(&serverAddress, 0, sizeof(serverAddress)); 
    memset(&clientAddress, 0, sizeof(clientAddress));

    serverAddress.sin_family = AF_INET;
    serverAddress.sin_addr.s_addr = INADDR_ANY; 
    serverAddress.sin_port = htons(PORT); 

    clientAddress.sin_family = AF_INET;
    clientAddress.sin_addr.s_addr = INADDR_ANY;
    clientAddress.sin_port = htons(PORT);

    if (bind(sockfd,(struct sockaddr *)&serverAddress,sizeof(serverAddress)) < 0) { 
        printf("bind failed"); 
        exit(1); 
    } else printf("binding success\n");

    sendto(sockfd, (const char *)message, strlen(message),0, (const struct sockaddr *) &clientAddress, sizeof(clientAddress)); 
    
    printf("Hello message sent.\n");

    return 0;
}
