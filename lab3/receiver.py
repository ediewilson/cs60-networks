from socket import *
from sys import argv, stdout, shutdown 
from signal import *
from run import getChecksum, unpack, createPacket

ackSock = socket()
recvSock = socket()
nextAck, ackNum = 0
numComms = 0
outPort = ""
senderPort = 0

def mrt_open():
    global recvSock, ackSock, nextAck, outPort

    signal(SIGINT, shutdown)

    try:
        numComms = int(argv[1])
        listenPort = int(argv[2])
        senderIp = socket.gethostbyname(argv[3])
        senderPort = int(argv[4])

    mrt_accept1(listenPort)

    packet, addr = recvSock.recvfrom(576)
    sourcePort, destPort, seqNum, ackNum, headerLength, ack, final, windowSize, contents = unpack(packet)

    checksum = getChecksum(packet)
    packetValid = checksum == 0 and nextAck == ackNum

    if packetValid:
        nextAck += 1

    ackSock.connect((senderIp, senderPort))
    outPort = ackSock.getsockname()[1]
    ackSegment = createPacket(outPort, senderPort, seqNum, ackNum, packetValid, False, 1, "")
    ackSock.send(ackSegment)



def mrt_accept1(listenPort):
    global recvSock, ackSock

    try:
        # UDP socket for receiving file
        recvSock = socket(AF_INET, SOCK_DGRAM)
        recvSock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        recvSock.bind(("", listenPort))

        # TCP socket for sending ACKs
        ackSock = socket()
        ackSock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        return ackSock
    except socket.error:
        exit("Error creating socket.")


def mrt_acceptAll():
    i = 0
    comms = []
    for i in range(numComms):
        comms[i] = mrt_accept1

    return comms
        


def mrt_recieve1():
    global nextAck, ackNum, outPort

    packet, addr = recvSock.recvfrom(576)
    sourcePort, destPort, seqNum, ackNum, headerLength, ack, final, windowSize, contents = unpack(packet)
    checksum = getChecksum(packet)

    # this fixes the checksum for messages that are really long 
    if checksum == 0xFFFF:
        checksum = 0

    if packetValid:
        ackNum += 1

    ackSegment = createPacket(outPort, senderPort, seqNum, ackNum, packetValid, final, 1, "")
    ackSock.send(ackSegment)
    
    if final:
        mrt_close()            

def mrt_close():
    global recvSock, ackSock

    ackSock.close()
    recvSock.close()
  