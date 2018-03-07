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
	 * 整体就是A端口通过任意一种模式发送到B端口，new出B对象 调用构造方法去监听B端口接收消息
	 * 1、对于组播的datagramSocket InetAddress需要加入组播组
	 * 2、3中播的地址也不同
	 */

	private enum SocketTpye {
		UNICAST, // 单播
		MULTICASE, // 多播（组播）
		BROADCAST, // 广播
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
			 * 1、本机地址 InetAddress addr = InetAddress.getLocalHost();
			 * (注意本机地址和环回地址是不一样的。绑定到本机地址的话，本机发送到环回地址收不到，反之亦然)
			 * 
			 * 2、环回地址 InetAddress addr = InetAddress.getLoopbackAddress();
			 * InetAddress addr = InetAddress.getByName("127.0.0.1");
			 * InetAddress addr = InetAddress.getByAddress(new byte[] { 127, 0,
			 * 0, 1 });
			 * 
			 * 3、广播地址 InetAddress addr = InetAddress.getByAddress(new byte[] {
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
								// 注意由于DatagramPacket的缓冲区复用，本次收到的最后一个字符后并不会补'\0',而是使用一个长度标记
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
				// 各种播的地址还不同
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
