# Mini Reliable Transport Lab
Author : Elizabeth Wilson 
Term : 20S 

# Overview 
The purpose of this lab was to create a mini reliable transport layer on top of UDP without using UDPs checksum feature. This lab contains 3 files, run, which is a common module for the sender and reciever, as well as the sender and receiver. The sender and receiver implimented guarentee uncorrupted, in order packet delivery.


# Assignment Questions 
## Testing against packet loss
Packs contain an ACK number as well as a sequence number. Packets that are in the expeced order and not corrupt are accepted by the reciever and the ackNum is incremented. If the packet meets the valid criteria, it is ACKed. If not, the reciever sends a message to the sender over TCP to let it know that the packet was lost. Another measure taken against pakcet loss is the sender implementation of a round trip time caluculator. By keeping track of an expected round trip time, the sender is able to prevent against packet loss and retransmit packets that aren't ACKed in time. 
The packet loss was tested with a bad network simulator that I found and a log output file that I used to print out the packets as they were sent and delivered. 

## Preventing data corruption 
The checksum of the packet is what prevents data corruption. When the packet is sent and the reciever gets it, the reciever checks that the packets checksum is valid to ensure there was no data corruption. This was tested by writing a random checksum scrambler for some of the packets and making sure the reciever knew and dropped them. 

## Preventing out of order delivery 
Preventing out of order delivery was similar to the testing I did to test against packet lass. Out of order delivery was prevented by my reciever with a check that the segment number of the packet was the one it was expecting 

## High latency testing 
The program is a little weak with high latency because it uses a window size of 1. However, with enough time, any amount of data can be transmitted. Also, the receiver has the funcitonality to fix checksums that get messed up due to size. 

## Dealing with small amounts of data 
The program deals with small amounts of data the way that it would deal with any amount of data. It showed this in testing with small inputs 

## Dealing with large amounts of data 
When the program has large amounts of data, it is split into packages of appropriate size and the checksum is modified if it was scrambled due to size. The sender breaks up the large amounts of data by only accepting what it can at one time.

## Flow control 
Flow control is managed by the sender calculating a round trip time for packets so that it doesn't call a timeout too early, and so that it doesn't overload the reciever with duplicate packets. 

## Sender and Receiver funtions 
In my MRT, the sender implements connect, send, and disconnect. The receiver impliments open, accept1, acceptAll, receive one, and close. I didn't implement a probe function because the receiver is programmed to accept messages as they come. Since the packet keeps track of ack numbers and which packet is expected, the receiver is able to take in data without probe. 

## How other functions were tested 
The createPacket and unpack functions were tested by passing dumby data into them within run.py. The checksum was tested by running sample outputs and comparing them to the tutorial where I learned how to calculate the checksum.

## Implementation of required functions 
Sender and Reciever implement the required funcitons in sender.py and receiver.py 

# Description of MRT Protocol 
My MRT protocol works by sending packets via UDP and ACKs via TCP. All messages have a 20 byte header containing source port, desitnation port, sequence number, acknowledgement number, flags, recieving window size, checksum, and an 'urgent pointer' always set to zero which goes unused in this protocol. The sequence number identifies the packet. The ACK number contains the sequence number of the next ACK that sender expects so that the receiver can keep track. There are two flags in the protocol, FIN and ACK: FIN indicates the packet is the final one. This is a stop and wait protocol with a window size of 1. In future iterations, the window size could grow and accept more packets. The timeout of the sender will cause a packet to be resent if there is no ACK recieved. The roundtrip time calculations are done by the sender based on the average round trip time of packets. When a packet is recieved, the reciever checks if the packet is corrupted in the recieve1 function. If yes, it is discarded and no ACK is sent. If no, it is ACKed. The protocol uses a round trip time estimator with a window size of one for packet delivery. 