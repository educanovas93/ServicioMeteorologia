package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class HTTPRequest {

	private Map<String, String> HTTPHeaders;
	private Map<String, String> HTTPArguments;
	private Map<String,String> cookies;
	private String rawArgs = "";
	private String HTTPMethod = "";
	private String HTTPVersion = "";
	private String HTTPPath = "";
	private String HTTPBody = "";
	private List<String> splitedHTTP;
	private boolean empty;

	public HTTPRequest(InputStream inputStream) {
		try {
			this.HTTPHeaders = new HashMap<>();
			this.cookies = new HashMap<>();
			this.splitedHTTP = new ArrayList<String>(splitHTTP(inputStream));	
			setHeaders();
			setCookies();
			if(!splitedHTTP.isEmpty()) {
			this.HTTPMethod = splitedHTTP.get(0).split(" ")[0];
			this.HTTPVersion = splitedHTTP.get(0).split(" ")[2];
			String[] aux = splitedHTTP.get(0).split(" ")[1].split("\\?");
			this.HTTPPath = aux[0];
			if(aux.length > 1) {
				this.rawArgs = aux[1];
				fillArgs();
			}
			//tratar argumentos de la url
			this.empty = false;
			}else {
				empty = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public HTTPRequest(String HTTPMethod,String HTTPVersion,String HTTPPath,String HTTPBody) {
		this.HTTPHeaders = new HashMap<>();
		this.HTTPMethod = HTTPMethod;
		this.HTTPVersion = HTTPVersion;
		this.HTTPPath = HTTPPath;
		this.HTTPBody = HTTPBody;
		this.empty = false;
	}
	
	public Map<String,String> getHTTPArguments(){
		return Collections.unmodifiableMap(this.HTTPArguments);
	}
	
	public Map<String,String> getCookies(){
		return Collections.unmodifiableMap(this.cookies);
	}
	
	public void fillArgs() {
		
	}
	
	public boolean isEmpty() {
		return this.empty;
	}
	
	public String getExtension() {
		return this.HTTPPath.split(".")[1];
	}
	
	public void setHeader(String header,String value) {
		this.HTTPHeaders.put(header, value);
	}
	
	public void setHTTPMethod(String HTTPMethod) {
		this.HTTPMethod = HTTPMethod;
	}

	public void setHTTPVersion(String HTTPVersion) {
		this.HTTPVersion = HTTPVersion;
	}

	public void setHTTPPath(String HTTPPath) {
		this.HTTPPath = HTTPPath;
	}

	public void setHTTPBody(String HTTPBody) {
		this.HTTPBody = HTTPBody;
	}

	public String getHTTPMethod() {
		return this.HTTPMethod;
	}

	public String getHTTPVersion() {
		return this.HTTPVersion;
	}

	public String getHTTPPath() {
		return this.HTTPPath;
	}
	
	public String getHTTPBody() {
		return this.HTTPBody;
	}
	public String getHeaderValue(String header) {
		return this.HTTPHeaders.get(header);
	}

	private List<String> splitHTTP(InputStream inputStream) throws IOException {
		BufferedReader d = new BufferedReader(new InputStreamReader(inputStream));
			
		ArrayList<String> ar = new ArrayList<String>();
		String line;
		while((line = d.readLine()) != null && line.length()!= 0 ) {
			ar.add(line);
		}
		
		return ar;		
	}

	private void setHeaders() {
		ArrayList<String> headersArray = (ArrayList<String>)splitedHTTP;
		for (int i = 1; i < headersArray.size(); i++) {
			this.HTTPHeaders.put(headersArray.get(i).split(": ")[0],headersArray.get(i).split(": ")[1]);		
		}
	}

	private void setCookies() {
		if(HTTPHeaders.get("Cookie") != null) {
			StringTokenizer st = new StringTokenizer(HTTPHeaders.get("Cookie").replaceAll(" ", ""),";");
			while(st.hasMoreTokens()) {
				String cookie = st.nextToken();
				String[] aux = cookie.split("=");	
				cookies.put(aux[0], aux[1]);
			}
		}
		
	}
	
	public Map<String, String> getHTTPHeaders() {
		return Collections.unmodifiableMap(this.HTTPHeaders);
	}
	
	public String getFormedRequest() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.HTTPMethod+" ");
		sb.append(this.HTTPPath+" ");
		sb.append(this.HTTPVersion+"\r\n");
		for(String header : this.HTTPHeaders.keySet()) {
	    	sb.append(header+": "+this.HTTPHeaders.get(header)+"\r\n");
	    }
		sb.append("\r\n");
		//sb.append(HTTPBody);
		return sb.toString();
	}
}
