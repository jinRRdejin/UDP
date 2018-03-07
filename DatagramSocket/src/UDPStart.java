import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPStart {

	/**
	 * 通过但广播实现客户端发 服务端收的功能
	 * datagramPacket 将要发送的消息打包。传给datagramSocket send
	 * datagramPacket 将收到的消息打包。传给datagramSocket，receive 
	 */
	static DatagramSocket socket = null;

	public static void main(String[] args) throws SocketException {
		socket = new DatagramSocket(UDPClient.port_B,
				InetAddress.getLoopbackAddress());

		new Thread(new Runnable() {
			public void run() {
				new UDPClient(socket).sendMessage();
			}
		}).start();
		new Thread(new Runnable() {

			public void run() {
				new UDPServer(socket).acceptMessage();
			}
		}).start();

	}

}
