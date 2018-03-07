package com.dcp.mul;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UDPmulClient {
    static int port = 1212;
	Scanner scanner;
	DatagramSocket socket = null;

	public UDPmulClient(DatagramSocket socket) {
		this.socket = socket;
	}

	public void sendMessage() {
		String line;
		scanner = new Scanner(System.in);
		
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			byte[] bytes = line.getBytes();
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

			// 重点设置多播组地址
			try {
				packet.setAddress(InetAddress.getByName(UDPmulStart.MULTICAST_ADDRESS));
				packet.setPort(port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		scanner.close();
		socket.close();
	}

}
