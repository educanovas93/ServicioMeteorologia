package http.Response;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import http.HTTPRequest;
import http.HTTPResponse;
import http.Servidor;
import http.VirtualHost;

public class ResponseGET implements IResponse {

	private VirtualHost virtualHost;
	private HTTPRequest HTTPRequest;
	
	public ResponseGET(VirtualHost virtualHost,HTTPRequest HTTPRequest) {
		this.virtualHost = virtualHost;
		this.HTTPRequest = HTTPRequest;
	}
	
	@Override
	public HTTPResponse getHttpResponse() {
		
		 /*
		 Comprobar si el fichero existe o no existe
		 File f = new File(filePathString);
		 if(f.exists() && !f.isDirectory()) { 
    	 	// do something
		 }
		 
		 */
		
		//soporte para virtual host

		String path = this.HTTPRequest.getHTTPPath();
		
		if(path.equals("/")) {
			path = "/index.html";
		}
		
		if ((path.substring(path.length() - 1)).equals("/")) {
			path = path+"index.html";
		}
			
		String url = this.virtualHost.getServerDirectory()+path;
		File file = new File(url);
		if(file.exists()) {
			HTTPResponse resp = new HTTPResponse("HTTP/1.1", "200", "OK.");
			resp.setHeader("Content-Length", Long.toString(file.length()));	
			resp.setHeader("Content-Type", Servidor.getContentType(file.getName()));
			resp.setBodyFile(file.getAbsolutePath());
			return resp;
		}else {
			URL urlNotFound = Servidor.class.getResource("notfound.html");
			File notFound = new File(urlNotFound.getPath());
			HTTPResponse resp = new HTTPResponse("HTTP/1.1","404","Not Found.");
			resp.setHeader("Content-Length", Long.toString(notFound.length()));
			resp.setHeader("Content-Type", Servidor.getContentType(notFound.getName()));
			resp.setBodyFile(notFound.getAbsolutePath());
			return resp;
		}
		
		

	}

}
