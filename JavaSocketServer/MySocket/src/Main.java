import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
	private static List<Socket> sockets = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		tcpSocketReceiver();
		// asyncTcpSocketReceiver();
	}

	/**
	 * java.net.BindException: Address already in use In MAC, input and run
	 * Terminal:<br>
	 * <br>
	 * lsof -i:<port> -> find PID <br>
	 * kill <PID> <br>
	 * <br>
	 * In this case, your command would be <br>
	 * lsof -i:11223 <br>
	 * kill ????
	 * 
	 * 
	 * @throws IOException
	 */
	private static void tcpSocketReceiver() throws IOException {
		// 1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
		ServerSocket serverSocket = new ServerSocket(11223);
		InetAddress address = InetAddress.getLocalHost();
		String ip = address.getHostAddress();
		Socket socket = null;
		// 2.调用accept()等待客户端连接
		System.out.println("Socket server is ready, ip is : " + ip);

		while (sockets.size() < 999) {
			System.out.println("accept()");
			socket = serverSocket.accept();
			sockets.add(socket);

			// 3.连接后获取输入流，读取客户端信息
			InputStream is = socket.getInputStream(); // 获取输入流
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String info = null;
			while ((info = br.readLine()) != null) {// 循环读取客户端的信息
				// System.out.println("Client numbers: " + sockets.size());
				System.out.println("You got the message: " + info);

				// disconnect
				if ("*#DISCONNECT11223#*".equals(info)) {
					System.out.println(socket.getInetAddress() + " disconnected.");
					System.out.println(String.format("There are %d connectiones now.", sockets.size()));
					sendMessage("*#DISCONNECT11223#*");
					sockets.remove(socket);
					socket.shutdownInput();// 关闭输入流
					socket.close();
				} else {
					sendMessage("I am server: " + info);
				}
			}
		}
	}

	private static void sendMessage(String content) {
		try {
			for (int i = 0; i < sockets.size(); i++) {
				Socket socket = sockets.get(i);
				PrintWriter pw;
				if (socket != null) {
					pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")));
					pw.write(content+"\n");
					pw.flush();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	private static void udpSocket() throws IOException {
		/*
		 * 接收客户端发送的数据
		 */
		// 1.创建服务器端DatagramSocket，指定端口
		DatagramSocket socket = new DatagramSocket(12345);
		// 2.创建数据报，用于接收客户端发送的数据
		byte[] data = new byte[1024];// 创建字节数组，指定接收的数据包的大小
		DatagramPacket packet = new DatagramPacket(data, data.length);
		// 3.接收客户端发送的数据
		System.out.println("****服务器端已经启动，等待客户端发送数据");
		socket.receive(packet);// 此方法在接收到数据报之前会一直阻塞
		// 4.读取数据
		String info = new String(data, 0, packet.getLength());
		System.out.println("我是服务器，客户端说：" + info);

		/*
		 * 向客户端响应数据
		 */
		// 1.定义客户端的地址、端口号、数据
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		byte[] data2 = "欢迎您!".getBytes();
		// 2.创建数据报，包含响应的数据信息
		DatagramPacket packet2 = new DatagramPacket(data2, data2.length, address, port);
		// 3.响应客户端
		socket.send(packet2);
		// 4.关闭资源
		socket.close();
	}

}
