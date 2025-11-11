package com.example.test.handler;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

import com.example.test.domain.response.problem.ProblemResult;

public interface ProblemHandler {

        default ProblemResult processTcpBuffered(
                        Socket socket,
                        String studentCode,
                        String qCode) throws Exception {
                throw new UnsupportedOperationException(
                                "Handler does not support TCP Buffered (BUFFERED)");
        }

        default ProblemResult processTcpRaw(
                        Socket socket,
                        String studentCode,
                        String qCode) throws Exception {
                throw new UnsupportedOperationException(
                                "Handler does not support TCP Raw (RAW)");
        }

        default ProblemResult processTcpObject(
                        Socket socket,
                        String studentCode,
                        String qCode) throws Exception {
                throw new UnsupportedOperationException(
                                "Handler does not support TCP Object (OBJECT)");
        }

        default ProblemResult processTcpData(
                        Socket socket,
                        String studentCode,
                        String qCode) throws Exception {
                throw new UnsupportedOperationException(
                                "Handler does not support TCP Data (DATA)");
        }

        default ProblemResult processUdp(
                        DatagramSocket serverSocket,
                        DatagramPacket packet,
                        String studentCode,
                        String qCode) throws Exception {
                throw new UnsupportedOperationException(
                                "Handler does not support UDP");
        }
}
