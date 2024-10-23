package client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class TFTP_UDPClient extends Thread {
    DatagramSocket socket;
    DatagramPacket packetReceive;
    DatagramPacket packetSend;

    int serverPort;
    int clientPort;

    File fileCurrent;
    FileInputStream readerCurrent;
    FileOutputStream writerCurrent;

    boolean reading;
    boolean writing;
    boolean close;
    boolean dallyClose;

    byte[] readBytes;

    byte[] block;
    int blockNo;

    public TFTP_UDPClient(int sPort) throws SocketException {
        int randPort = (int) Math.floor(Math.random() * 1000 + 2001);
        clientPort = randPort;
        serverPort = sPort;
        socket = new DatagramSocket(clientPort);
    }

    public void start() {
        System.out.println("Client Starting...");

        reading = false;
        writing = false;
        blockNo = 0;

        menu();

        run();
    }

    public void menu() {
        System.out.println("Press 1 for read, Press 2 to write:\n");
        Scanner in = new Scanner(System.in);
        String stdIn = in.nextLine();

        PacketMessage message = new PacketMessage();

        if (stdIn.equals("1")) {
            reading = true;
            writing = false;
            System.out.println("Enter file name to read:");
            Scanner fileName = new Scanner(System.in);
            String fileInput = fileName.nextLine();
            message.RRQ(fileInput);

            fileCurrent = new File(fileInput);
            try {
                writerCurrent = new FileOutputStream(fileCurrent);
            } catch(Exception e) {
                System.err.println(e);
            }


        } else if (stdIn.equals("2")) {
            reading = false;
            writing = true;
            System.out.println("Enter file name to write:");
            Scanner fileName = new Scanner(System.in);
            String fileInput = fileName.nextLine();
            message.WRQ(fileInput);

            fileCurrent = new File(fileInput);

		    if (fileCurrent.exists()) {
                try {
                    readerCurrent = new FileInputStream(fileCurrent);
                    // System.out.println("Scanner made");
                } catch (FileNotFoundException e) {
                    System.err.println(e);
                }
            }

        } else {
            System.out.println("Invalid input");
            menu();
        }

        try {

            InetAddress addr = InetAddress.getByName("127.0.0.1");

            DatagramPacket packet = new DatagramPacket(message.getBuf(), message.length());
            packet.setAddress(addr);
            packet.setPort(serverPort);

            socket.send(packet);

            System.out.println();
            System.out.println("Sent:");
            System.out.println(Arrays.toString(packet.getData()));
            System.out.println(message);
            System.out.println();

        } catch (IOException e) {
            System.err.println(e);
        }

    }

    public void run() {
        System.out.println("Client Running...");
        System.out.println();

        try{
            socket.setSoTimeout(1000);
        } catch(Exception e) {
            System.err.println(e);
        }
        
        byte[] buffer = new byte[516];
        packetReceive = new DatagramPacket(buffer, buffer.length);

        try {
            while (true) {
                try {
                    if (!socket.isClosed()) {
    
                        socket.receive(packetReceive);

                        PacketMessage packetReceiveData = new PacketMessage(packetReceive.getData(), packetReceive.getLength());
    
                        System.out.println("Received:");
                        System.out.println(Arrays.toString(packetReceiveData.getBuf()));
                        System.out.println(packetReceiveData);
                        System.out.println();
                        
                        PacketMessage packetSendData = getSendData(packetReceiveData);
    
                        packetSend = new DatagramPacket(packetSendData.getBuf(), packetSendData.length());
                        packetSend.setAddress(packetReceive.getAddress());
                        packetSend.setPort(packetReceive.getPort());
    
                        if (!socket.isClosed() && !close) {
                            socket.send(packetSend);
    
                            System.out.println("Sent:");
                            System.out.println(Arrays.toString(packetSend.getData()));
                            System.out.println(packetSendData);
                            System.out.println();
                        }
                        
                        if (dallyClose) {
                            socket.close();
                            System.out.println("Socket closed");
                            // System.exit(0);
                            break;
                        }
    
                        packetReceive = new DatagramPacket(buffer, buffer.length);
                    }

                } catch (SocketTimeoutException t) {
                    try {
                        if(!socket.isClosed()) {
                            socket.send(packetSend); 
        
                            System.out.println("Re-sent last packet:");
                            System.out.println(Arrays.toString(packetSend.getData()));
                            System.out.println();
                        }
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        } 
    }

    public PacketMessage getSendData(PacketMessage packetReceiveData) {
        PacketMessage packetSendData;

        int opcode = packetReceiveData.getOpcode();
        
        if (opcode == 3) {
            packetSendData = receiveData(packetReceiveData);
        } else if (opcode == 4) {
            packetSendData = receiveAck(packetReceiveData);
        } else if (opcode == 5) {
            packetSendData = new PacketMessage();
            receiveError(packetReceiveData);
        } else {
            packetSendData = new PacketMessage();
            packetSendData.ERROR(4, "Illegal TFTP opteration");
            socket.close();
        }

        return packetSendData;
    }

    public PacketMessage receiveData(PacketMessage packetReceiveData) {
        // System.out.println("receiveData");

        PacketMessage packetSendData = new PacketMessage();

        try {
            writerCurrent.write(packetReceiveData.getByteData());
        } catch(Exception e) {
            System.err.println(e);
        }

        if (packetReceiveData.length() < 516) {
            //write writerCurrent to file
            try{
                writerCurrent.flush();
                writerCurrent.close();
            } catch(Exception e) {
                System.out.println(e);
            }  
			dallyClose = true;
		}

        int blockNoReceived = packetReceiveData.getBlockNo();

        blockNo++;
        packetSendData.ACK(blockNoReceived);

        return packetSendData;
    }

    public byte[] getDataBlock() {
		byte[] data = new byte[512];
		try {
			if (readerCurrent.available() < 512) {
				data = new byte[readerCurrent.available()];
			}
			readerCurrent.read(data);
		} catch (IOException e) {
			System.err.println(e);
		}

		return data;
	}

	public PacketMessage receiveAck(PacketMessage packetReceiveData) {
		// System.out.println("receiveAck");
        // System.out.println();

        if (close) {
            System.out.println("Socket Closed");
            socket.close();
            System.exit(0);
        } 

		PacketMessage packetSendData = new PacketMessage();

		byte[] data = getDataBlock();

        // System.out.println(data.length);

        if (data.length < 512) {
            // System.out.println("Last data packet");
			try {
				readerCurrent.close();
			} catch (IOException e) {
				System.err.println(e);
			}
            close = true;
		}

		blockNo++;
		packetSendData.DATA(blockNo, data);

        // System.out.println(packetSendData.length());

		return packetSendData;
	}

    public void receiveError(PacketMessage packetReceiveData) {
        // System.out.println("receiveError");

        PacketMessage packetSendData = new PacketMessage();

        System.out.println(packetReceiveData.getData());

        socket.close();
        close = true;

        // packetSendData.ERROR(1, "Error Response");

        // return packetSendData;
    }

    public static void main(String[] args) throws IOException {
        // File file = new File("client.txt");
        // Scanner scanner = new Scanner(file);
        // System.out.println(scanner.nextLine());

        TFTP_UDPClient client = new TFTP_UDPClient(2000);
        client.start();
    }
}
