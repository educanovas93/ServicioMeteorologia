package distribucion;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClienteControl extends Thread{

	private DatagramSocket socket;
	//direccion donde enviará las tramas de control
	private InetAddress address;
	private Handler handler;
	private String modo;
	private static final int PORT = 4006;
	private static final int BUFF_SIZE = 1472;
	private static final int TIMEOUT = 4000;
	
	public ClienteControl(InetAddress address) {
		try {
			System.out.println("Como quieres enviar las tramas de control XML o JSON");
			Scanner sc = new Scanner(System.in);
			modo = sc.nextLine();
			//Por este puerto recibiremos las tramas ACK, podríamos poner uno aleatorio y seguiria funcionando
			socket = new DatagramSocket(4006);
			this.address = address;
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (true){
			try {
				this.socket.setSoTimeout(TIMEOUT);
				boolean ack = false;
				Scanner sc = new Scanner(System.in);
				String msg = sc.nextLine();
				String[] splitted = msg.split(":");
				int port = new Integer(splitted[1]);
				msg = splitted[0];
				
				if(modo.equalsIgnoreCase("json")) {
					this.handler = new JSONHandler();
				}else if (modo.equalsIgnoreCase("xml")) {
					this.handler = new XMLHandler();
				}
				MensajeControl m = new MensajeControl(msg);
				String enviar = this.handler.serializar(m).trim();
				
				
				System.err.println(enviar);
				DatagramPacket packet = new DatagramPacket(enviar.getBytes(), enviar.getBytes().length, address,port);
				
				
				//socket.send(packet);
				
				while(!ack) {
					socket.send(packet);
					try {
						DatagramPacket ackPacket;
						ackPacket = new DatagramPacket(new byte[BUFF_SIZE], BUFF_SIZE);	
						socket.receive(ackPacket);
						String received = new String(ackPacket.getData(), 0, ackPacket.getLength()).trim();
						String[] headers = received.split("&",2);
						System.err.println("PEINEEEE"+received);
						if(headers[0].equalsIgnoreCase("json")) {
							handler = new JSONHandler();
						}else if(headers[0].equalsIgnoreCase("xml")) {
							handler = new XMLHandler();
						}
						MensajeControl ackControl = handler.deserializar(headers[1], MensajeControl.class);
						System.err.println(ackControl.getClass());
							
						
						if(ackControl.getComando().equalsIgnoreCase("ack")) {
							System.out.println("******ACK Recibido******");
							ack = true;
						}
						
					} catch (SocketTimeoutException e) {
						System.err.println("Tiempo excedido volviendo a enviar datagrama de control...");
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void close() {
		socket.close();
	}
	
}
