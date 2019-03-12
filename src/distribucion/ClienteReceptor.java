package distribucion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections4.queue.CircularFifoQueue;

public class ClienteReceptor extends Thread {

	
	private DatagramSocket socket;
	private InetAddress address;
	private Handler handler;
	private static final int BUFF_SIZE = 1472;
	//private static List<MensajeDistribucion> mensajes = new LinkedList<MensajeDistribucion>();
	private static volatile Map<String,CircularFifoQueue<MensajeDistribucion>> mensajes = new HashMap<String,CircularFifoQueue<MensajeDistribucion>>();
	private static int n = 0;
	private int port;
	
	
	public ClienteReceptor(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
			address = Server.getIpFromInterface("en0");
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public InetAddress getAddress() {
		return this.address;
	}
	
	public DatagramSocket getSocket() {
		return this.socket;
	}
	
	@Override
	public void run() {
		while(true) {
					
			try {
				DatagramPacket packet;
				packet = new DatagramPacket(new byte[BUFF_SIZE], BUFF_SIZE);
				socket.receive(packet);
				String received = new String(packet.getData()).trim();
				String[] headers = received.split("&",2);
				
				if(headers[0].equalsIgnoreCase("json")) {
					handler = new JSONHandler();
				}else if(headers[0].equalsIgnoreCase("xml")) {
					handler = new XMLHandler();
				}
				MensajeDistribucion m = handler.deserializar(headers[1],MensajeDistribucion.class);

				CircularFifoQueue<MensajeDistribucion> aux = mensajes.get(m.getServerName());
				if(aux != null ){
					aux.add(m);
					mensajes.put(m.getServerName(),aux);
				}else {
					CircularFifoQueue<MensajeDistribucion> otra = new CircularFifoQueue<>(2);
					mensajes.put(m.getServerName(),otra);
				}	
			
				File f = new File("distribucion/dist.html");
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.write("<!DOCTYPE html>\n<html>\n<body>\n");
				//bw.write("<link rel=\"stylesheet\" href=\"/style.css\">\n");
				bw.write("<style>*{box-sizing:border-box;}.column{float:left;width:25%;padding:10px;height:300px;}.row:after{content:\"\";display:table;clear:both;}</style>");
				//bw.write("<meta http-equiv=\"refresh\" content=\"3; URL=/index.html\">\n");
				bw.write("<div class=\"row\">");
				
				boolean par = true;
				for (Map.Entry<String, CircularFifoQueue<MensajeDistribucion>> entry : mensajes.entrySet()){
				    if(par) {
				    	bw.write("<div class=\"column\" style=\"background-color:#aaa;\">");
				    }else {
				    	bw.write("<div class=\"column\" style=\"background-color:#bbb;\">");
				    }
				    bw.write("<h2>"+"DATOS "+entry.getKey()+"</h2>");
					    for(MensajeDistribucion p : entry.getValue()) {
					    	bw.write("<pre>"+p.toString()+"</pre>");
					    }
					bw.write("</div>");
					par = !par;
				    
				}
				
				bw.write("</div>");		
			    bw.write("</body>\n</html>");
			    bw.close();
					
			} catch (IOException e) {
				
			}
		}
	}
	
	
	public void close() {
		socket.close();
	}
	
}
