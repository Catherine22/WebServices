import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Main {
	private static List<Socket> sockets = new ArrayList<>();
	private final static int PORT1 = 11223;
	private final static int PORT2 = 11345;
	
	//NIO
	// 用于检测所有Channel状态的Selector
	private static Selector selector = null;
	// 定义实现编码、解码的字符集对象
	private static Charset charset = Charset.forName("UTF-8");
	private static String message = "N/A";


	public static void main(String[] args) throws IOException {
		// tcpSocketReceiver();
		nioSocketReceiver();
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
		ServerSocket serverSocket = new ServerSocket(PORT1);
		InetAddress address = InetAddress.getLocalHost();
		String ip = address.getHostAddress();
		Socket socket = null;
		// 2.调用accept()等待客户端连接
		System.out.println("Socket server is ready, ip is : " + ip);

		while (sockets.size() < 999) {
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
					pw.write(content + "\n");
					pw.flush();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void nioSocketReceiver() throws IOException {
		HandlerSelectionKey handler = new HandlerHandlerSelectionKeyImpl();

		// 创建 ServerSocketChannel
		ServerSocketChannel server = ServerSocketChannel.open();
		server.configureBlocking(false);
		server.bind(new InetSocketAddress(PORT2));
		// 创建 Selector
		Selector selector = Selector.open();
		server.register(selector, SelectionKey.OP_ACCEPT);
		// 死循环，持续接收 客户端连接
		while (true) {
			// selector.select(); 是阻塞方法
			int keys = selector.select();
			if (keys > 0) {
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					it.remove();
					// 处理 SelectionKey
					handler.handler(key, selector);
				}
			}
		}
	}

	/**
	 * SelectionKey 处理接口
	 *
	 */
	public static interface HandlerSelectionKey {

		public void handler(SelectionKey key, Selector selector) throws IOException;

	}

	/**
	 * SelectionKey 接口 实现类
	 *
	 */
	public static class HandlerHandlerSelectionKeyImpl implements HandlerSelectionKey {

		@Override
		public void handler(SelectionKey key, Selector selector) throws IOException {
			int keyState = selectionKeyState(key);
			switch (keyState) {
			case SelectionKey.OP_ACCEPT:
				System.out.println("OP_ACCEPT");
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
				accept(serverSocketChannel, selector);
				break;
			case SelectionKey.OP_READ:
				System.out.println("OP_READ");
				SocketChannel readSocketChannel = (SocketChannel) key.channel();
				read(readSocketChannel, selector);
				break;

			case SelectionKey.OP_WRITE:
				System.out.println("OP_WRITE");
				SocketChannel writeSocketChannel = (SocketChannel) key.channel();
				write(writeSocketChannel, selector, message);
				break;
			}
		}

		/**
		 * 获取 SelectionKey 是什么事件
		 * 
		 * @param key
		 * @return
		 */
		private int selectionKeyState(SelectionKey key) {
			if (key.isAcceptable()) {
				return SelectionKey.OP_ACCEPT;
			} else if (key.isReadable()) {
				return SelectionKey.OP_READ;
			} else if (key.isWritable()) {
				return SelectionKey.OP_WRITE;
			}
			return -1;
		}

		/**
		 * 接口客户端请求
		 * 
		 * @param serverSocketChannel
		 * @param selector
		 * @throws IOException
		 */
		private void accept(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
			SocketChannel socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
			// 将 channel 注册到 Selector
			socketChannel.register(selector, SelectionKey.OP_READ);
		}


		/**
		 * 读取客户端发送过来的信息
		 * 
		 * @param socketChannel
		 * @param selector
		 * @throws IOException
		 */
		private void read(SocketChannel socketChannel, Selector selector) throws IOException {
			ByteBuffer readBuffer = ByteBuffer.allocate(8192);
			int readBytes = socketChannel.read(readBuffer);
			if (readBytes > 0) {
				String info = new String(readBuffer.array(), 0, readBytes);
				System.out.println("You got the message: " + info);
				message = "Hi, I am server! I got your message: " + info;

				// disconnect
				if ("*#DISCONNECT11223#*".equals(info)) {
					System.out.println(socketChannel.getRemoteAddress() + " disconnected.");
					message = "*#DISCONNECT11223#*";
					// 将 channel 注册到 Selector
					socketChannel.register(selector, SelectionKey.OP_WRITE);
				} else {
					// 将 channel 注册到 Selector
					socketChannel.register(selector, SelectionKey.OP_WRITE);
				}

			}
		}

		/**
		 * 响应客户端请求
		 * 
		 * @param socketChannel
		 * @param selector
		 * @throws IOException
		 */
		private void write(SocketChannel socketChannel, Selector selector, String message) throws IOException {
			// 响应消息
			String responseMsg = message;
			byte[] responseByte = responseMsg.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(responseByte.length);
			writeBuffer.put(responseByte);
			writeBuffer.flip();
			// 响应客户端
			socketChannel.write(writeBuffer);
			socketChannel.finishConnect();
			socketChannel.close();
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
