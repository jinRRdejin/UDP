package com.dcp.mul;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class UDPmulServer {

	private DatagramSocket socket;
    private DatagramPacket recv = null;
	public UDPmulServer(DatagramSocket socket) {
		this.socket = socket;
	}
	public void acceptMessage(){
		
		byte[] bytes= new byte[1024];
		recv = new DatagramPacket(bytes, bytes.length);
		new Thread(new Runnable() {
			
			public void run() {
				try {
					while(true){
					socket.receive(recv);
					String msg = new String(recv.getData(), recv.getOffset(), recv.getLength());
					System.out.println("接收到的消息为：" + msg);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				socket.close();
			}
		}).start();
		
	}

}
