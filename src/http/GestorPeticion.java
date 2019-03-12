package http;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import http.Response.IResponse;
import http.Response.ResponseGET;
import http.Response.ResponseHEAD;
import http.Response.ResponseMethodNotAllowed;



public class GestorPeticion extends Thread {
	private String mode;
	private Socket s ;
	private Servidor server;
	
	public GestorPeticion (Socket s,String mode,Servidor server) {
		this.s = s;
		this.mode = mode;
		this.server = server;
	}
	
	public void handle(Socket s) throws Exception {
		DataInputStream in;
		PrintStream out;
		in = new DataInputStream(s.getInputStream());
		out = new PrintStream(s.getOutputStream());
		
		HTTPResponse httpResponse;
		if(mode.equalsIgnoreCase("web")) {
			httpResponse = getHttpResponse(in);
		}else {
			httpResponse = getEcoResponse(in);
		}
		
		
		if(httpResponse != null) {
			out.write(httpResponse.getFormedResponse().getBytes());
			if(httpResponse.getBodyFile() != null) {
				File file = new File(httpResponse.getBodyFile());
				BufferedInputStream inbuf = new BufferedInputStream(new FileInputStream(file));
				
		        byte[] buff = new byte[32 * 1024]; 
		        int len = 0;
		        while ((len = inbuf.read(buff)) > 0) {
		         out.write(buff, 0, len);
		        }
		        inbuf.close();
			}
		}

		in.close();
		out.flush();
		out.close();
		s.close();
	}
	
	private HTTPResponse getEcoResponse(InputStream httpRequest) throws Exception {
		HTTPRequest req = new HTTPRequest(httpRequest);
		System.out.println("Ha llegado una petición al servidor :\n");
		System.out.println(req.getFormedRequest());
		
		if(req.isEmpty()) {
			httpRequest.close();
			return null;
		}
		
		
		String cookie = req.getCookies().get("webppc");
		int cookieValue;

		if(cookie != null) {
			cookieValue = new Integer(cookie);
			cookieValue++;
		}else {
			cookieValue = 2;
		}
		
		

		Date expDate= new Date();
		expDate.setTime (expDate.getTime() + (1 * Servidor.ONE_MINUTE_IN_MILLIS));
		DateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String cookieExpire = "Expires=" + df.format(expDate);
		
		
		if(cookieValue <= 10) {
			File temp = File.createTempFile("eco", ".html");
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
			bw.write("<!DOCTYPE html>\n<html>\n<body>\n<pre>\n");
			if(req.getHTTPMethod().equals("HEAD")) {
				bw.write("Este es el método HEAD");
			}
			bw.write("El recurso pedido es "+req.getHTTPPath()+"\n");
			String nPeticiones = req.getCookies().get("webppc");
			String es = "es";
			if(nPeticiones == null) {
				nPeticiones = "1";
				es = "";
			}
			
			bw.write("Llevas "+nPeticiones+" peticion"+es+"\n\n");
		    bw.write(req.getFormedRequest());
		    bw.write("</pre>\n</body>\n</html>");
		    bw.close();
	
			HTTPResponse resp = new HTTPResponse("HTTP/1.1", "200", "OK.");
			resp.setHeader("Content-Length", Long.toString(temp.length()));
			resp.setHeader("Content-Type", server.getContentType(temp.getName()));
			resp.setHeader("Set-Cookie", "webppc="+cookieValue+";"+cookieExpire);
			resp.setBodyFile(temp.getAbsolutePath());
			return resp;
		}else {		
			URL urlMaxCookies = Servidor.class.getResource("maxcookies.html");
			File file = new File(urlMaxCookies.getPath());
			HTTPResponse resp = new HTTPResponse("HTTP/1.1", "403", "Forbidden");
			resp.setHeader("Content-Length", Long.toString(file.length()));
			resp.setHeader("Content-Type", server.getContentType(file.getName()));
			resp.setBodyFile(file.getAbsolutePath());
			return resp;		
		}
	}
	

	//Funcion por si se quiere utilizar el servidor web
	public HTTPResponse getHttpResponse(InputStream httpRequest) {
		
		HTTPRequest req = new HTTPRequest(httpRequest);
		IResponse response;
		//Seleccionamos virtualhost
		VirtualHost v = server.getVHosts().get(req.getHeaderValue("Host"));
		if(v == null) {
			v = server.getVHosts().entrySet().iterator().next().getValue();
		}
		
		switch (req.getHTTPMethod()) {
		case "GET":
			response = new ResponseGET(v,req);
			break;
			
		case "HEAD":
			response = new ResponseHEAD(v,req);		
			break;
			
		default:
			response = new ResponseMethodNotAllowed();
			break;
		}
			
		return response.getHttpResponse();
	}
	
	public void run() {
		try {
			handle(this.s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
