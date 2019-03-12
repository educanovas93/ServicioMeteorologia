package distribucion;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Random;
import java.util.Scanner;

public class Server2 extends Thread{

	private DatagramSocket socket;
	private InetAddress ipAddress;
	private MessageManager messageManager;
	
	private static int sendPort;
	private static int receivePort;
	private static final int BUFF_SIZE = 1472;
	private int timeout;
	private int periodo;
	private final String iface;
	private InetAddress broadCastAddress;
	private boolean running;
	private String name;
	private String modo;
	private Handler handler;
	
	public Server2(String iface,String name,int sendPort,int receivePort,int periodo) {
		this.iface = iface;
		this.periodo = periodo;
		this.name = name;
		this.ipAddress = getIpFromInterface(iface);
		this.broadCastAddress = getBroadcastFromInterface(iface);
		initMessageManager();
		this.timeout = 1500;
		Server2.sendPort = sendPort;
		Server2.receivePort = receivePort;
		try {
			socket = new DatagramSocket(receivePort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	/*
	public static void main(String[] args) {
		Server2 s = new Server2("en0","SERVIDOR 3",4000,4003,2000);
		s.listen();
	}
	*/
	
	@Override
	public void run() {
		listen();
	}

	public static int getSendPort() {
		return sendPort;
	}
	public int getPeriodo() {
		return this.periodo;
	}
	
	public static int getReceivePort() {
		return receivePort;
	}
	public InetAddress getIpAdress() {
		return this.ipAddress;
	}

	public InetAddress getBroadcastAddress() {
		return this.broadCastAddress;
	}

	public String getIface() {
		return this.iface;
	}
	
	private void initMessageManager() {
		Random r = new Random();
		Property precipitaciones = new Property("Precipitaciones","%",r.nextInt(100),100);
		Property viento = new Property("Viento","km/h",r.nextInt(300),300);
		Property ocupacion = new Property("Ocupacion","per/m^2",r.nextInt(200),200);
		Property periodo = new Property("Periodo","ms",this.periodo,15000);
		System.out.println("Como quieres trabajar en XML O JSON");
		/*
		Scanner sc = new Scanner(System.in);
		if(sc.nextLine().equals("json")) {
			this.modo = "json";
		}else {
			this.modo = "xml";
		}
		*/
		this.modo = "json";
		this.messageManager = new MessageManager(handler,name,precipitaciones,viento,ocupacion,periodo);
	}

	//Metodo para obtener la ipv4 de una interfaz dada, lo hacemos estatico para reutilizarlo en otras clases por comodidad
	public static InetAddress getIpFromInterface(String interfaceName) {
		NetworkInterface networkInterface;
		try {
			networkInterface = NetworkInterface.getByName(interfaceName);
			Enumeration<InetAddress> inetAddress = networkInterface.getInetAddresses();
			InetAddress currentAddress;
			currentAddress = inetAddress.nextElement();
			while (inetAddress.hasMoreElements()) {
				currentAddress = inetAddress.nextElement();
				if (currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress()) {
					return currentAddress;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	//Metodo para obtener la direccion de broadcast de una interfaz dada
	public InetAddress getBroadcastFromInterface(String interfaceName) {
		NetworkInterface networkInterface;
		try {
			networkInterface = NetworkInterface.getByName(interfaceName);
			for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
				InetAddress broadcast = interfaceAddress.getBroadcast();
				if (broadcast != null) {
					return broadcast;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void acknowledgement(InetAddress address,int port) {
		MensajeControl ack = new MensajeControl("ack");
		String msg = getHandler(modo).serializar(ack);
		System.err.println(msg);
		DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, address,port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Handler getHandler(String mode) {
		if(mode.equalsIgnoreCase("json")) {
			return new JSONHandler();
		}else return new XMLHandler();
	}
	
	
	public void listen() {

		try {
			this.socket.setBroadcast(true);
			this.socket.setSoTimeout(timeout);
			running = true;
			while (running) {
				String msg = messageManager.generateMessage(getHandler(modo)).trim();
				DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, this.broadCastAddress,sendPort);
				this.socket.send(packet);
				//System.out.println(new String(packet.getData()));
				//System.out.println("Mensaje de distribucion enviado");
				try {
					Thread.sleep((long)periodo);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					DatagramPacket control = new DatagramPacket(new byte[BUFF_SIZE], BUFF_SIZE);
					socket.receive(control);
					String controlContent = new String(control.getData()).trim();
					if (controlContent.length() > 0) {					
						System.out.println("NUEVO MENSAJE DE CONTROL : "+controlContent);
						int aux = messageManager.control(controlContent);
						if(aux != -1) {
							this.periodo = aux;
						}
						acknowledgement(control.getAddress(),control.getPort());
					}
				} catch (SocketTimeoutException e) {
					//System.err.println("No hay trama de control");
				}
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		socket.close();
	}

}
