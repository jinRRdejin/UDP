import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class UDPClient {

	public static int port_B = 3535;
	public static int port_A = 4545;
	DatagramSocket socket = null;
	Scanner scanner;

	public UDPClient(DatagramSocket socket) {
		this.socket = socket;
	}
	
	public void sendMessage() {
		// new Thread(new Runnable() {
		// public void run() {
		scanner = new Scanner(System.in);
		String line = null;
		/*
		 * try { socket = new DatagramSocket(port_B,
		 * InetAddress.getLoopbackAddress()); } catch (SocketException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); }
		 */
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			byte[] bytess = line.getBytes();
			DatagramPacket packet = new DatagramPacket(bytess, bytess.length);
			try {
				InetAddress addr = InetAddress.getLoopbackAddress();
				packet.setAddress(addr);
				packet.setPort(port_B);
				socket.send(packet);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		scanner.close();
		socket.close();
		// }
		// }).start();

	}

}
