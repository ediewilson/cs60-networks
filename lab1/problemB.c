#include <unistd.h> 
#include <stdio.h> 
#include <stdlib.h> 
#include <string.h> 
#include <netinet/in.h> 
#include <sys/socket.h> 
#include <sys/types.h> 

#define PORT 8099 

int main(void) {

    int serverSock, sock; 
    struct sockaddr_in serverAddress, clientAddress; 
    
    char message[] = "Edie"; 
       
    if((serverSock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        printf("error opening socket");
    } else printf("opened socket");

    /*defining server properties*/
    serverAddress.sin_family = AF_INET; 
    serverAddress.sin_addr.s_addr = INADDR_ANY; 
    serverAddress.sin_port = htons(PORT); 
    
    memset(&serverAddress.sin_zero,0,sizeof(serverAddress.sin_zero));

    unsigned int length;
    length = sizeof(struct sockaddr_in);

    /*attatching server to communication port*/
    if (bind(serverSock,(struct sockaddr *)&serverAddress,length) < 0) { 
        printf("bind failed"); 
        exit(1); 
    } else printf("success on bind");
    
    if (listen(serverSock,5) < 0) { 
        printf("listen error"); 
        exit(1); 
    } else printf("listening");
    
    if ((sock = accept(serverSock,(struct sockaddr *)&clientAddress,&length)) < 0) { 
        printf("accepting error"); 
        exit(1); 
    } else printf("accepting");
    
    send(sock,message,strlen(message),0);
    printf("message sent");

    return 0; 
} 
