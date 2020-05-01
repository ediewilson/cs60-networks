# Some functions shared between sender and reciever kept in a common module 

import socket
import struct

HEADER_FORMAT = ""
HEADER_SIZE = 5


def getChecksum (data):
    i = len(data)

    if (i & 1):
        i -= 1
        sum = ord(data[i])
    else:
        sum = 0
    while i > 0:
        i -= 2
        sum += (ord(data[i + 1]) << 8) + ord(data[i])
    sum = (sum >> 16) + (sum & 0xffff)

    result = (~ sum) & 0xffff  
    result = result >> 8 | ((result & 0xff) << 8)  

    return result


def createPacket (sourcePort, destPort, seqNum, ackNum, ack, final, windowSize, content):
    if final:
        flags = 1
    else:
        flags = 0
    if ack:
        flags += 16
        
    header = struct.pack(HEADER_FORMAT, sourcePort, destPort, seqNum, ackNum, HEADER_SIZE, flags, windowSize, 0, 0)
    check = getChecksum(header + content)
    header = struct.pack(HEADER_FORMAT, sourcePort, destPort, seqNum, ackNum, HEADER_SIZE, flags, windowSize, check, 0)

    return header + content


def unpack (segment):
    header = segment[:20]
    packSrcPort, packDestPort, packSeqNum, packAckNum, packHeaderLen, packFlags, packet_window_size, packet_checksum, packet_urgent = struct.unpack(HEADER_FORMAT, header)

    packet_ack = (packFlags >> 4) == 1
    packet_final = int(packFlags % 2 == 1)
    packContent = segment[20:]

    return packSrcPort, packDestPort,packSeqNum, packAckNum, packHeaderLen, packet_ack, packet_final, packet_window_size, packContent


def timeout(sigNum, frame):
    raise socket.timeout