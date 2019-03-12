package http;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.io.FilenameUtils;

public class Servidor extends Thread {

	private Map<String,VirtualHost> vhosts = new HashMap<>();
	private static Map<String,String> fileTypes = new HashMap<>();
	public static final long ONE_MINUTE_IN_MILLIS = 60000;
    private String mode;
    private boolean SSLEngine;
    private ServerSocket socket;
    private int port;
		
    public Servidor(int port,boolean SSLEngine,String mode) {
    	this.port = port;
    	this.SSLEngine = SSLEngine;
    	this.mode = mode;
    }
   
    public Map<String,VirtualHost> getVHosts(){
    	return Collections.unmodifiableMap(vhosts);
    }
    
    public Map<String, String> getFileTypes() {
    	return Collections.unmodifiableMap(fileTypes);
    }
    
	public void run() {

		Socket cliente = null;
		
		fileTypes.put("gif", "image/gif");
		fileTypes.put("jpg", "image/jpg");
		fileTypes.put("jpeg","image/jpeg");
		fileTypes.put("png", "image/png");
		fileTypes.put("ico", "image/ico");
		fileTypes.put("zip", "image/zip");
		fileTypes.put("gz",  "image/gz" );
		fileTypes.put("tar", "image/tar");
		fileTypes.put("htm", "text/html");
		fileTypes.put("html","text/html");
		fileTypes.put("css", "text/css");
		fileTypes.put("js", "application/javascript");
		fileTypes.put("woff", "font/woff");
		
		VirtualHost ppc1 = new VirtualHost("www.ppc1.es"+":"+port,"ppc1.es");
		VirtualHost ppc2 = new VirtualHost("www.ppc2.es"+":"+port,"ppc2.es");
		VirtualHost meteo = new VirtualHost("meteo.ppc.es"+":"+port,"distribucion");
		vhosts.put(ppc1.getServerName(), ppc1);
		vhosts.put(ppc2.getServerName(), ppc2);
		vhosts.put(meteo.getServerName(), meteo);

		socket = getSocket();
		
		if(mode.equalsIgnoreCase("web")) {
			System.out.println("Servidor WEB escuchando en el puerto "+port);
		}else {
			System.out.println("Servidor ECO escuchando en el puerto "+port);
		}
		

		while (true) {
			try {
				cliente = socket.accept();
				new GestorPeticion(cliente,mode,this).start();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
	
	
	public static String getContentType(String fileName) {
		return fileTypes.get(FilenameUtils.getExtension(fileName));
	}
	
	private ServerSocket getSocket() {
		if(!SSLEngine) {
			try {
				return new ServerSocket(port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				KeyStore keyStore = KeyStore.getInstance("JKS");
				InputStream is = new FileInputStream("certificados/meteo/meteo.jks");
				char[] password = "qwerty123".toCharArray();
				keyStore.load(is,password);
				KeyManagerFactory keyFactory = KeyManagerFactory.getInstance ("SunX509");
				keyFactory.init(keyStore, password);
				TrustManagerFactory trustFactory = TrustManagerFactory.getInstance("SunX509");
				trustFactory.init(keyStore);
				SSLContext ssl = SSLContext.getInstance("TLS");
				ssl.init(keyFactory.getKeyManagers(),trustFactory.getTrustManagers(),new SecureRandom());
				ServerSocketFactory socketFactory = ssl.getServerSocketFactory();
				SSLServerSocket SSLSocket = (SSLServerSocket) socketFactory.createServerSocket(port);
				SSLSocket.setNeedClientAuth(true);
				return SSLSocket;
				
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException | KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
 	
}
