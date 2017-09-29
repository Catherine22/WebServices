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
import java.net.SocketAddress;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Main {
	private static List<Socket> sockets = new ArrayList<>();
	private final static int PORT1 = 11223;
	public final static int PORT2 = 11345;

	public static void main(String[] args) throws IOException {
		// tcpSocketReceiver();

		InetAddress address = InetAddress.getLocalHost();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					new Main(address.getHostAddress(), PORT2).startServer();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(runnable, "Thread-1").start();
		
		
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

	private Selector selector;
	private Map<SocketChannel, List> dataMapper;
	private InetSocketAddress listenAddress;

	public Main(String address, int port) throws IOException {
		listenAddress = new InetSocketAddress(address, port);
		dataMapper = new HashMap<SocketChannel, List>();
	}

	// create server channel
	private void startServer() throws IOException {
		this.selector = Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// retrieve server socket and bind to port
		serverChannel.socket().bind(listenAddress);
		serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

		System.out.println("Server started...");

		while (true) {
			// wait for events
			this.selector.select();

			// work on selected keys
			Iterator keys = this.selector.selectedKeys().iterator();
			while (keys.hasNext()) {
				SelectionKey key = (SelectionKey) keys.next();

				// this is necessary to prevent the same key from coming up
				// again the next time around.
				keys.remove();

				if (!key.isValid()) {
					continue;
				}

				if (key.isAcceptable()) {
					this.accept(key);
				} else if (key.isReadable()) {
					this.read(key);
				}
			}
		}
	}

	// accept a connection made to this channel's socket
	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = serverChannel.accept();
		channel.configureBlocking(false);
		Socket socket = channel.socket();
		SocketAddress remoteAddr = socket.getRemoteSocketAddress();
		System.out.println("Connected to: " + remoteAddr);

		// register channel with selector for further IO
		dataMapper.put(channel, new ArrayList());
		channel.register(this.selector, SelectionKey.OP_READ);
	}

	// read from the socket channel
	private void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int numRead = -1;
		numRead = channel.read(buffer);

		if (numRead == -1) {
			this.dataMapper.remove(channel);
			Socket socket = channel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Connection closed by client: " + remoteAddr);
			channel.close();
			key.cancel();
			return;
		}

		byte[] data = new byte[numRead];
		System.arraycopy(buffer.array(), 0, data, 0, numRead);
		String content = new String(data);
		System.out.println("Got: " + content);

		if ("*#DISCONNECT11223#*".equals(content)) {
			InetAddress address = InetAddress.getLocalHost();
			System.out.println("Connection closed by server: " + address.getHostAddress());
			channel.close();
			key.cancel();
		} else {
			String response = "Hi, I am server, I got your message: " + content;
			channel.write(ByteBuffer.wrap(response.getBytes(Charset.forName("UTF-8"))));
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