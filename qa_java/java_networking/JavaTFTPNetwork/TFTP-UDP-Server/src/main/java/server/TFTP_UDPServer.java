package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class TFTP_UDPServer {
	DatagramSocket socket;
	DatagramSocket threadSocket;
	DatagramPacket packetReceive;

	int serverPort;
	int randPort;

	public TFTP_UDPServer(int sPort) throws SocketException {
		serverPort = sPort;
		socket = new DatagramSocket(serverPort);
	}

	public void run() {

		System.out.println("Server Running...");
		System.out.println();

		byte[] buffer = new byte[516];
		packetReceive = new DatagramPacket(buffer, buffer.length);

		try {
			while (true) {

				int randPort = (int) Math.floor(Math.random() * 1000 + 2001);
				
				socket.receive(packetReceive);

				threadSocket = new DatagramSocket(randPort);
				// System.out.println(threadSocket.getPort());

				PacketMessage packetReceiveData = new PacketMessage(packetReceive.getData());

				System.out.println("Received request: " + packetReceiveData);
				// System.out.println(Arrays.toString(packetReceive.getData()));
				// System.out.println(packetReceiveData);
				System.out.println();

				System.out.println("Creating thread to handle request at port: " + randPort);
				new TFTP_UDPServerThread(threadSocket, packetReceive).start();
				System.out.println();

			}
		} catch (IOException e) {
			System.err.println(e);
		} 
	}

	
    public static void main(String[] args) {
		// Scanner scanner = new Scanner(file);
		// System.out.println(scanner.nextLine());
		try {
			TFTP_UDPServer server = new TFTP_UDPServer(2000);
			server.run();
		} catch (IOException e) {
			System.out.println(e);
		}

	}
}
