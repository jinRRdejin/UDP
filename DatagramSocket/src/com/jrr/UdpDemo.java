package com.jrr;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class UdpDemo {
	private static int PORT_A = 7777;
    private static int PORT_B = 9999;
    private static String MULTICAST_ADDRESS = "225.0.0.1";
    private static String EXIT = "exit";

    private enum SocketType {
        UNICAST,    // 单播
        MULTICAST,  // 多播（组播）
        BROADCAST,  // 广播
        ANYCAST     // 任播
    }
    private static SocketType type = SocketType.BROADCAST;

    static class UdpClient {
        protected DatagramSocket socket = null;
        private SocketType type;

        public UdpClient(int port, SocketType type) {
            this.type = type;
            byte[] recvBuffer = new byte[1024];
            final DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
            try {
                switch (type) {
                case UNICAST:
                case BROADCAST:
                    socket = new DatagramSocket(port, InetAddress.getLoopbackAddress());
                    break;
                case MULTICAST:
                    socket = new MulticastSocket(port);
                    ((MulticastSocket) socket).joinGroup(InetAddress.getByName(MULTICAST_ADDRESS));
                    break;
                case ANYCAST:
                    // 暂时未实现
                    return;
                }

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            while (true) {
                                socket.receive(recvPacket);
                                System.out.println("received packet from " + recvPacket.getAddress().getHostAddress() + " : " + recvPacket.getPort());
                                //注意由于DatagramPacket的缓冲区复用，本次收到的最后一个字符后并不会补'\0',而是使用一个长度标记
                                String msg = new String(recvPacket.getData(), recvPacket.getOffset(), recvPacket.getLength());
                                System.out.println("received " + msg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (socket != null) {
                                socket.close();
                            }
                        }
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
            byte[] buffer = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("send to " + addr.getHostAddress());
            try {
                switch (type) {
                case UNICAST:
                    packet.setAddress(addr);
                    break;
                case MULTICAST:
                    packet.setAddress(InetAddress.getByName(MULTICAST_ADDRESS));
                    break;
                case BROADCAST:
                    packet.setAddress(InetAddress.getByAddress(new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255 }));
                    break;
                case ANYCAST:
                    // 暂时未实现
                    return false;
                }
                packet.setPort(port);
                socket.send(packet);
                return true;
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    static class UdpClientA extends UdpClient {
        public UdpClientA() {
            super(PORT_A, type);
        }

        public void startScanner() {
            // scanner必须写在线程中，如果阻塞主线程，那么输出将无法打印出来
            new Thread(new Runnable() {

                public void run() {
                    Scanner scanner = new Scanner(System.in);
                    String line;
                    InetAddress addr = InetAddress.getLoopbackAddress();
                    while (scanner.hasNext()) {
                        line = scanner.nextLine();
                        if(!send(addr, PORT_B, line)) {
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

        public void doSchedule() {
            InetAddress addr;
            try {
                addr = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return;
            }
            final InetAddress thisAddr = addr;
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    if(!send(thisAddr, PORT_B, "hello world")) {
                        this.cancel();
                    }
                }
            }, 0, 5000);
        }
    }

    static class UdpClientB extends UdpClient {
        public UdpClientB() {
            super(PORT_B, type);
        }
    }

    public static void main(String[] args) {
        UdpClientA clientA = new UdpClientA();
        new UdpClientA();
        clientA.startScanner();
        clientA.doSchedule();
        new UdpClientB();
    }

}
