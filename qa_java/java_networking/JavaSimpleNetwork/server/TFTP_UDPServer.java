import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class TFTP_UDPServer extends Thread {
	DatagramSocket socket;
	DatagramPacket packetReceive;
	DatagramPacket packetSend;

	File fileCurrent;
	Scanner scannerCurrent;
	FileWriter writerCurrent;

	boolean reading;
	boolean writing;

	byte[] readBytes;

	byte[] block;
	int blockNo;

	public TFTP_UDPServer() throws SocketException {
		socket = new DatagramSocket(2000);
	}

	public byte[] convertBytes(List<Byte> bytes) {
		byte[] out = new byte[bytes.size()];
		int i = 0;
		for (Byte b : bytes) {
			out[i++] = b.byteValue();
		}
		return out;
	}

	public void start() {
		System.out.println("Server Starting...");

		byte[] buffer = new byte[516];
		packetReceive = new DatagramPacket(buffer, buffer.length);

		reading = false;
		writing = false;
		blockNo = 0;

		run();
	}

	public void run() {
		System.out.println("Server Running...");
		// socket.setSoTimeout(100000);

		try {
			while (true) {
				if (!socket.isClosed()) {
					socket.receive(packetReceive);

					PacketMessage packetReceiveData = new PacketMessage(packetReceive.getData());

					System.out.println("Received:");
					System.out.println(Arrays.toString(packetReceive.getData()));
					System.out.println(packetReceiveData);

					PacketMessage packetSendData = getSendData(packetReceiveData);

					packetSend = new DatagramPacket(packetSendData.getBuf(), packetSendData.length());
					packetSend.setAddress(packetReceive.getAddress());
					packetSend.setPort(packetReceive.getPort());

					if (!socket.isClosed()) {
						socket.send(packetSend);

						System.out.println("Sent:");
						System.out.println(Arrays.toString(packetSend.getData()));
						System.out.println(packetSendData);
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
		System.out.println("receiveRead");
		PacketMessage packetSendData = new PacketMessage();

		String fileName = packetReceiveData.getFileName();

		fileCurrent = new File(fileName);

		if (fileCurrent.exists()) {
			// data stuff
			try {
				scannerCurrent = new Scanner(fileCurrent);
			} catch (FileNotFoundException e) {
				System.err.println(e);
			}

			byte[] data = new byte[512];

			for (int i = 0; i < 512; i++) {
				if (scannerCurrent.hasNextByte()) {
					data[i] = scannerCurrent.nextByte();
				}
			}

			blockNo++;
			packetSendData.DATA(blockNo, data);

			reading = true;
			writing = false;
		} else {
			reading = false;
			writing = false;
			packetSendData.ERROR(1, "File \"" + fileName + "\" does not exist.`");
			socket.close();
		}

		return packetSendData;
	}

	public PacketMessage receiveWrite(PacketMessage packetReceiveData) {
		System.out.println("receiveWrite");
		PacketMessage packetSendData = new PacketMessage();

		String fileName = packetReceiveData.getFileName();

		fileCurrent = new File(fileName);

		try {
			fileCurrent.createNewFile();
		} catch (IOException e) {
			System.err.println(e);
		}

		reading = false;
		writing = true;

		packetSendData.ACK(blockNo);
		blockNo++;

		return packetSendData;
	}

	public PacketMessage receiveData(PacketMessage packetReceiveData) {
		System.out.println("receiveData");
		PacketMessage packetSendData = new PacketMessage();

		// check status

		// byte[] byteData = packetReceiveData.getByteData();
		// byte[] allBytes = new byte[readBytes.length + byteData.length];
		// System.arraycopy(readBytes, 0, allBytes, 0, readBytes.length);
		// System.arraycopy(byteData, 0, allBytes, readBytes.length, byteData.length);
		// readBytes = allBytes;

		int blockNoReceived = packetReceiveData.getBlockNo();

		packetSendData.ACK(blockNo);
		blockNo++;

		return packetSendData;
	}

	public PacketMessage receiveAck(PacketMessage packetReceiveData) {
		System.out.println("receiveAck");
		PacketMessage packetSendData = new PacketMessage();

		// check status

		int blockNoReceived = packetReceiveData.getBlockNo();

		byte[] data = new byte[512];

		for (int i = 0; i < 512; i++) {
			if (scannerCurrent.hasNextByte()) {
				data[i] = scannerCurrent.nextByte();
			}
		}

		blockNo++;
		packetSendData.DATA(blockNo, data);

		return packetSendData;
	}

	public PacketMessage receiveError(PacketMessage packetReceiveData) {
		System.out.println("receiveError");
		PacketMessage packetSendData = new PacketMessage();

		System.out.println(packetReceiveData.getData());

		socket.close();

		packetSendData.ERROR(1, "Error Response");

		return packetSendData;
	}

	public static void main(String[] args) throws IOException {
		File file = new File("test.txt");
		Scanner scanner = new Scanner(file);
		System.out.println(scanner.nextLine());

		TFTP_UDPServer server = new TFTP_UDPServer();
		server.start();
		scanner.close();
	}
}
