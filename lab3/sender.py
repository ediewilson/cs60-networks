from socket import socket, AF_INET, SOCK_DGRAM, SOL_SOCKET, SO_REUSEADDR, timeout, error, gethostbyname
import sys, run, time 
from signal import signal, SIGALRM, alarm

SEGMENT_SIZE = 100
WINDOW_SIZE = 1

# uses a UDP socket for packet transmission and a TCP socket for acks
def mrt_connect():

    remoteIp = gethostbyname(sys.argv[2])
    remotePort = int(sys.argv[3])
    ackPort = int(sys.argv[4])
    try:
        sendSock = socket(AF_INET, SOCK_DGRAM)
        sendSock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        sendSock.bind(("", ackPort))

        ackSock = socket()
        ackSock.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
        ackSock.bind(("", ackPort))
        ackSock.listen(1)
    except error:
        exit('Socket creation error.')


def mrt_send(sendSock, ackSock):

    remoteIp = gethostbyname(sys.argv[2])
    remotePort = int(sys.argv[3])
    ackPort = int(sys.argv[4])
    seqNum = 0
    ackNum = 0
    final = False
    sent = 0
    retransmitted = 0

    for line in sys.stdin: 
        content = line

    tcpConnect = False
    text = line.read(556)

    timeout0 = 1

    while not tcpConnect:
        try:
            packet = run.createPacket(ackPort, remotePort, seqNum, ackNum, False, False, WINDOW_SIZE, text)

            sendSock.sendto(packet, (remoteIp, remotePort))
            sent += 1
            sendTime = time()

            signal(SIGALRM, timeout)
            alarm(1)

            recvSock, addr = ackSock.accept()

            alarm(0)
            tcpConnect = True
            recvTime = time()
            estimatedRTT = recvTime - sendTime
            devRTT = 0
            recvSock.settimeout(timeout0)
        except timeout:
            retransmitted += 1
            continue

    while True:
        try:
            ack = recvSock.recv(20)
            recvTime = time()

            ackSrcPort, ackDestPort, ackSeqNum, ackAckNum, ackHeadLength, ackValid, ackFinal, ackWindow, ackContent = run.unpack(ack)

            if ackAckNum == ackNum and ackValid:

                # RTT calculations
                sample_rtt = recvTime - sendTime
                estimatedRTT = estimatedRTT * 0.875 + sample_rtt * 0.125
                dev_rtt = 0.75 * dev_rtt + 0.25 * abs(sample_rtt - estimatedRTT)
                recvSock.settimeout(estimatedRTT + 4 * dev_rtt)


                text = line.read(556)  # 576 - TCP header

                if text == "":
                    final = True

                seqNum += 1
                ackNum += 1

                packet = run.createPacket(ackPort, remotePort, seqNum, ackNum, False, final, WINDOW_SIZE, text)
                sendSock.sendto(packet, (remoteIp, remotePort))

                sent += 1
                sendTime = time()

            else:
                raise socket.timeout

        except timeout:
            packet = run.createPacket(ackPort, remotePort, seqNum, ackNum, False, final, WINDOW_SIZE, text)
            sendSock.sendto(packet, (remoteIp, remotePort))
            sent += 1
            retransmitted += 1

def mrt_disconnect(ackSock, sendSock):
    ackSock.shutdown(1)
    sendSock.shutdown(1)
    sendSock.close()
