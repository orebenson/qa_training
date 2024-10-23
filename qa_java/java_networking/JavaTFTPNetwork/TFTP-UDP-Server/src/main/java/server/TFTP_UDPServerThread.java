package server;

import java.io.*;
import java.net.*;
import java.util.*;

import java.lang.*;

public class TFTP_UDPServerThread extends Thread{

    DatagramSocket socket = null;
    DatagramPacket packetReceive;
	DatagramPacket packetSend;

    int serverPort;
	int randPort;

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

    public TFTP_UDPServerThread(DatagramSocket sock, DatagramPacket packet) {
        super("TFTP_UDPServerThread");
        this.socket = sock;
        packetReceive = packet;
    }

    public void start() {
		// System.out.println("New thread starting at port: " + socket.getPort());

		reading = false;
		writing = false;
		blockNo = 0;

		run();
	}

	public void run() {
		
        System.out.println("Thread running...");
        System.out.println();

        byte[] buffer = new byte[516];
        // packetReceive = new DatagramPacket(buffer, buffer.length);

		try {

            PacketMessage packetReceiveData = new PacketMessage(packetReceive.getData(), packetReceive.getLength());
                    
            PacketMessage packetSendData = getSendData(packetReceiveData);

            System.out.println("Received:");
            System.out.println(Arrays.toString(packetReceiveData.getBuf()));
            System.out.println(packetReceiveData);
            System.out.println();

            packetSend = new DatagramPacket(packetSendData.getBuf(), packetSendData.length());
            packetSend.setAddress(packetReceive.getAddress());
            packetSend.setPort(packetReceive.getPort());
            
            socket.send(packetSend);

            System.out.println("Sent:");
            System.out.println(Arrays.toString(packetSend.getData()));
            System.out.println(packetSendData);
            System.out.println();
            
            socket.setSoTimeout(1000);
            
            packetReceive = new DatagramPacket(buffer, buffer.length);

			while (true) {
                try {
                    if (!socket.isClosed()) {
    
                        socket.receive(packetReceive);
    
                        packetReceiveData = new PacketMessage(packetReceive.getData(), packetReceive.getLength());
    
                        System.out.println("Received:");
                        System.out.println(Arrays.toString(packetReceiveData.getBuf()));
                        System.out.println(packetReceiveData);
                        System.out.println();

                        packetSendData = getSendData(packetReceiveData);
    
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

		if (opcode == 1) {
			packetSendData = receiveRead(packetReceiveData);
		} else if (opcode == 2) {
			packetSendData = receiveWrite(packetReceiveData);
		} else if (opcode == 3) {
			packetSendData = receiveData(packetReceiveData);
		} else if (opcode == 4) {
			packetSendData = receiveAck(packetReceiveData);
		} else if (opcode == 5) {
			packetSendData = receiveError(packetReceiveData);
		} else {
			packetSendData = new PacketMessage();
			packetSendData.ERROR(4, "Illegal TFTP opteration");
			socket.close();
		}

		return packetSendData;
	}

	public PacketMessage receiveRead(PacketMessage packetReceiveData) {
		// System.out.println("receiveRead");
        // System.out.println();

		PacketMessage packetSendData = new PacketMessage();

		String fileName = packetReceiveData.getFileName();

		fileCurrent = new File(fileName);

		if (fileCurrent.exists()) {
			try {
				readerCurrent = new FileInputStream(fileCurrent);
			} catch (FileNotFoundException e) {
				System.err.println(e);
			}

			byte[] data = getDataBlock();

			if (data.length < 512) {
				try {
					readerCurrent.close();
				} catch (IOException e) {
					System.err.println(e);
				}
                close = true;
			}

			blockNo++;
			packetSendData.DATA(blockNo, data);

			reading = true;
			writing = false;
		} else {
			reading = false;
			writing = false;
			packetSendData.ERROR(1, "File \"" + fileName + "\" does not exist.`");
			dallyClose = true;
			System.out.println("file does not exist");
		}

		return packetSendData;
	}

	public PacketMessage receiveWrite(PacketMessage packetReceiveData) {
		// System.out.println("receiveWrite");
        // System.out.println();

		PacketMessage packetSendData = new PacketMessage();

		String fileName = packetReceiveData.getFileName();

		fileCurrent = new File(fileName);

		try {
			fileCurrent.createNewFile();
		} catch (IOException e) {
			System.err.println(e);
		}

        try {
            writerCurrent = new FileOutputStream(fileCurrent);
        } catch(Exception e) {
            System.err.println(e);
        }

		reading = false;
		writing = true;

		packetSendData.ACK(blockNo);

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
        //reads blocks of bytes from readerCurrent into data
        // try{System.out.println("readerCurrent available: " + readerCurrent.available());} catch (Exception e){System.err.println(e);}
        
		byte[] data = new byte[512];
		try {
			if (readerCurrent.available() < 512) {
                //reads final block of bytes into data
				data = new byte[readerCurrent.available()];
			}
			readerCurrent.read(data);
		} catch (IOException e) {
			System.err.println(e);
            data = new byte[0];
		}

        // System.out.println("data block length: " + data.length);

		return data;
	}

	public PacketMessage receiveAck(PacketMessage packetReceiveData) {
		// System.out.println("receiveAck");
        // System.out.println();

        if (close) {
            System.out.println("Socket Closed");
            socket.close();
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

	public PacketMessage receiveError(PacketMessage packetReceiveData) {
		// System.out.println("receiveError");
        // System.out.println();

		PacketMessage packetSendData = new PacketMessage();

		System.out.println(packetReceiveData.getData());

		socket.close();

		packetSendData.ERROR(1, "Error Response");

		return packetSendData;
	}

}
