package http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


public class Cliente {
	private static String valorCookie;
	private static Date cookieExpires = null;

	public static void main(String args[]) throws ParseException {
		Socket miCliente;
		DataInputStream entrada;
		DataOutputStream salida;
		try {

			while (true) {
				System.out.println("Introduce una URL o exit para salir : \n");
				Scanner scanner = new Scanner(System.in);
				String url = scanner.nextLine();

				if (url.equalsIgnoreCase("exit")) {
					break;
				}
				URL u;

				u = new URL(url);

				
				
				System.out.println(u.getProtocol());
				int port;
				if(u.getPort() == -1 && u.getProtocol().equalsIgnoreCase("https")) {
					port = 443;
					System.out.println("puerto 443");
				}else if (u.getPort() == -1 && u.getProtocol().equalsIgnoreCase("http")) {
					port = 80;
					System.out.println("puerto 80");
				}else {
					port = u.getPort();
				}
				String path = u.getPath();
				String domain = u.getHost();
				
				InetAddress address = InetAddress.getByName(domain);
				boolean secure;
				if(u.getProtocol().equalsIgnoreCase("https")) {
					secure = true;
				}else {
					secure = false;
				}
					
				
				//miCliente = new Socket(address.getHostAddress(), port);
				
				miCliente = getSocket(secure,address, port);
				entrada = new DataInputStream(miCliente.getInputStream());
				salida = new DataOutputStream(miCliente.getOutputStream());
				
				
				//Creamos request
				HTTPRequest req = new HTTPRequest("GET", "HTTP/1.1", "/" + path, "");
				req.setHeader("Host", domain + ":" + port);
			
				Date d = new Date();
				if(cookieExpires != null && cookieExpires.getTime() > d.getTime()) {
					req.setHeader("Cookie", valorCookie);
				}
				
				salida.writeBytes(req.getFormedRequest());

				
				//Analizamos la response
				HTTPResponse resp = new HTTPResponse(entrada);
				String cookieHeader = resp.getHeaderValue("Set-Cookie");
				if(cookieHeader != null) {
					valorCookie = getCookieFields(cookieHeader).get("Value");
					String cookieExpire = getCookieFields(cookieHeader).get("Expires");
					DateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz");
					df.setTimeZone(TimeZone.getTimeZone("GMT"));
					cookieExpires = df.parse(cookieExpire);
				}
				
				
				System.out.print(resp.getFormedResponse());
				System.out.println(new String(resp.getHTTPBody()));
				entrada.close();
				salida.close();
			}
			System.out.println("Saliendo del cliente...");
		} catch (MalformedURLException e) {
			System.out.println("La url esta malformada por favor vuelva a introducirla");
			main(args);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	private static Socket getSocket(boolean secure,InetAddress address,int port) {
		if(!secure) {
			try {
				return new Socket(address.getHostAddress(), port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			try {
				
				KeyStore keyStore = KeyStore.getInstance("JKS");
				InputStream is = new FileInputStream("certificados/cliente/cliente.jks");
				char[] password = "qwerty123".toCharArray();
				keyStore.load(is,password);
				KeyManagerFactory keyFactory = KeyManagerFactory.getInstance ("SunX509");
				keyFactory.init(keyStore, password);
				TrustManagerFactory trustFactory = TrustManagerFactory.getInstance("SunX509");
				trustFactory.init(keyStore);
				SSLContext ssl = SSLContext.getInstance("TLS");
				ssl.init(keyFactory.getKeyManagers(),trustFactory.getTrustManagers(),new SecureRandom());
				SSLSocketFactory socketFactory = ssl.getSocketFactory();
				Socket SSLSocket = socketFactory.createSocket(address.getHostAddress(),port);
				return SSLSocket;
				
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException | KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	private static Map<String,String> getCookieFields(String cValue) {

		String[] fields = cValue.split(";\\s*");
		String cookieValue = fields[0];
		String expires = "";
		String path = "";
		String domain = "";
		boolean secure = false;

		for (int j = 1; j < fields.length; j++) {
			if ("secure".equalsIgnoreCase(fields[j])) {
				secure = true;
			}

			else if (fields[j].indexOf('=') > 0) {
				String[] f = fields[j].split("=");
				if ("expires".equalsIgnoreCase(f[0])) {
					expires = f[1];
				}
				else if ("domain".equalsIgnoreCase(f[0])) {
					domain = f[1];
				}
				else if ("path".equalsIgnoreCase(f[0])) {
					path = f[1];
				}
			}

		}
		
		Map<String,String> cookieMap = new HashMap<>();
		cookieMap.put("Expires", expires);
		cookieMap.put("Value", cookieValue);
		cookieMap.put("Path", path);
		cookieMap.put("Domain", domain);
		return cookieMap;
	}

}
