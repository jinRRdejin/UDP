import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
	public static int port_B = 3535;
	public static int port_A = 4545;
	DatagramPacket recePacket = null;

	DatagramSocket socket;

	public UDPServer(DatagramSocket socket) {
		this.socket = socket;
	}

	public void acceptMessage() {

		byte[] bytes = new byte[1024];
		recePacket = new DatagramPacket(bytes, bytes.length);

		/*
		 * try { socket = new DatagramSocket(port_B,
		 * InetAddress.getLoopbackAddress()); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		new Thread(new Runnable() {

			public void run() {
				try {
					while (true) {
						socket.receive(recePacket);
						String msg = new String(recePacket.getData(),
								recePacket.getOffset(), recePacket.getLength());
						System.out.println("接收到消息 ： " + msg);
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
