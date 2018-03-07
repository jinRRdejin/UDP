import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UDPdemo {

	public static int port_A = 6666;
	public static int port_B = 8888;
	public static String EXIT = "exit";
	public static String MULTICAST_ADDRESS = "225.0.0.1";
	
	/*
	 * �������A�˿�ͨ������һ��ģʽ���͵�B�˿ڣ�new��B���� ���ù��췽��ȥ����B�˿ڽ�����Ϣ
	 * 1�������鲥��datagramSocket InetAddress��Ҫ�����鲥��
	 * 2��3�в��ĵ�ַҲ��ͬ
	 */

	private enum SocketTpye {
		UNICAST, // ����
		MULTICASE, // �ಥ���鲥��
		BROADCAST, // �㲥
	}
 

	public static void main(String[] args) {
		try {
			UDPClientA clienta = new UDPClientA();
			clienta.startScanner();
			new UDPClientB();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 static SocketTpye type = SocketTpye.UNICAST;
	static class UDPClient {

		DatagramSocket socket = null;
	    SocketTpye type;

		public UDPClient(int port, SocketTpye type){
			this.type = type;
			byte[] bytes = new byte[1024];
			final DatagramPacket recdPacket = new DatagramPacket(bytes,
					bytes.length);

			/*
			 * 1��������ַ InetAddress addr = InetAddress.getLocalHost();
			 * (ע�Ȿ����ַ�ͻ��ص�ַ�ǲ�һ���ġ��󶨵�������ַ�Ļ����������͵����ص�ַ�ղ�������֮��Ȼ)
			 * 
			 * 2�����ص�ַ InetAddress addr = InetAddress.getLoopbackAddress();
			 * InetAddress addr = InetAddress.getByName("127.0.0.1");
			 * InetAddress addr = InetAddress.getByAddress(new byte[] { 127, 0,
			 * 0, 1 });
			 * 
			 * 3���㲥��ַ InetAddress addr = InetAddress.getByAddress(new byte[] {
			 * 255, 255, 255, 255 });
			 */
			try {
				switch (type) {
				case UNICAST:
				case BROADCAST:
					socket = new DatagramSocket(port,
							InetAddress.getLoopbackAddress());
					break;
				case MULTICASE:

					socket = new MulticastSocket(port);
					((MulticastSocket) socket).joinGroup(InetAddress
							.getByName(MULTICAST_ADDRESS));
					break;
				}

				new Thread(new Runnable() {

					public void run() {
						try {
							while (true) {
								
								socket.receive(recdPacket);
								// ע������DatagramPacket�Ļ��������ã������յ������һ���ַ��󲢲��Ჹ'\0',����ʹ��һ�����ȱ��
								String msg = new String(recdPacket.getData(),
										recdPacket.getOffset(),
										recdPacket.getLength());
								System.out.println("received :" + msg);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						socket.close();
					}
				}).start();
			} catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
		}

		public boolean send(InetAddress addr, int port, String msg) {
			byte[] bytes = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

			try {
				// ���ֲ��ĵ�ַ����ͬ
				switch (type) {
				case UNICAST:
					packet.setAddress(addr);
					break;
				case MULTICASE:
					packet.setAddress(InetAddress.getByName(MULTICAST_ADDRESS));
					break;
				case BROADCAST:
					packet.setAddress(InetAddress.getByAddress(new byte[] {
							(byte) 255, (byte) 255, (byte) 255, (byte) 255 }));
					break;
				}
				packet.setPort(port);
				socket.send(packet);
				return true;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;

		}
	}


	static class UDPClientA extends UDPClient {

		public UDPClientA() {
			super(port_A,SocketTpye.BROADCAST);
		}

		public void startScanner() {
			new Thread(new Runnable() {
				public void run() {
					Scanner scanner = new Scanner(System.in);
					String line;
					InetAddress addr = InetAddress.getLoopbackAddress();
					while (scanner.hasNext()) {
						line = scanner.nextLine();
						if (!send(addr, port_B, line)) {
							break;
						}
						if (EXIT.equals(line)) {
							break;
						}

					}
					scanner.close();
				}
			}).start();
		}

	}

	static class UDPClientB extends UDPClient {

		public UDPClientB(){
			super(port_B,SocketTpye.BROADCAST);
		}

	   }
	
}
