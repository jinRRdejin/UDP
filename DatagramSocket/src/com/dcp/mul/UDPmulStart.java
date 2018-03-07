package com.dcp.mul;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPmulStart {

	private static DatagramSocket socket = null;
	static int port = 1212;
	public static String MULTICAST_ADDRESS = "225.0.0.2";
	public static void main(String[] args) {
			try {
				socket = new MulticastSocket(port);
				((MulticastSocket) socket).joinGroup(InetAddress
						.getByName(MULTICAST_ADDRESS));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
		new Thread(new Runnable() {

			public void run() {
				new UDPmulClient(socket).sendMessage();

			}
		}).start();
		new Thread(new Runnable() {

			public void run() {
				new UDPmulServer(socket).acceptMessage();

			}
		}).start();

	}

}
