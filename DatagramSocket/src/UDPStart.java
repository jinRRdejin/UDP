import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPStart {

	/**
	 * ͨ�����㲥ʵ�ֿͻ��˷� ������յĹ���
	 * datagramPacket ��Ҫ���͵���Ϣ���������datagramSocket send
	 * datagramPacket ���յ�����Ϣ���������datagramSocket��receive 
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
