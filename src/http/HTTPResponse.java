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

public class HTTPResponse {

	private String HTTPVersion;
	private String HTTPStatusCode;
	private String HTTPStatusText;
	private Map<String, String> HTTPHeaders;
	private List<String> splitedHTTP;
	private byte[] HTTPBody;
	private String bodyFile;
	
	
	public HTTPResponse(InputStream inputStream) {
		try {
			this.HTTPHeaders = new HashMap<>();
			this.splitedHTTP = new ArrayList<String>(splitHTTP(inputStream));
			setHeaders();
			setHTTPVersion(splitedHTTP.get(0).split(" ")[0]);
			setHTTPStatusCode(splitedHTTP.get(0).split(" ")[1]);	
			setHTTPStatusText(splitedHTTP.get(0).split(" ")[2]);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public HTTPResponse(String HTTPVersion,String HTTPStatusCode,String HTTPStatusText) {
		this.HTTPHeaders = new HashMap<>();
		this.HTTPVersion = HTTPVersion;
		this.HTTPStatusCode= HTTPStatusCode;
		this.HTTPStatusText = HTTPStatusText;
	}
	
	public void setHeader(String header,String value) {
		this.HTTPHeaders.put(header, value);
	}
	
	public void setHTTPStatusCode(String HTTPStatusCode) {
		this.HTTPStatusCode = HTTPStatusCode;
	}

	public void setHTTPVersion(String HTTPVersion) {
		this.HTTPVersion = HTTPVersion;
	}

	public void setHTTPStatusText(String HTTPStatusText) {
		this.HTTPStatusText = HTTPStatusText;
	}

	public void setHTTPBody(byte[] HTTPBody) {
		this.HTTPBody = HTTPBody;
	}
	public void setBodyFile(String bodyFile) {
		this.bodyFile = bodyFile;
	}

	public String getBodyFile() {
		return this.bodyFile;
	}
	
	public String getHTTPVersion() {
		return this.HTTPVersion;
	}

	public String getHTTPPath() {
		return this.getHTTPPath();
	}
	
	public byte[] getHTTPBody() {
		return this.HTTPBody;
	}
	public String getHeaderValue(String header) {
		return this.HTTPHeaders.get(header);
	}

	private ArrayList<String> splitHTTP(InputStream inputStream) throws IOException {
		BufferedReader d = new BufferedReader(new InputStreamReader(inputStream));
		
		ArrayList<String> ar = new ArrayList<String>();
		String line;
		while((line = d.readLine())!= null && line.length()!= 0 ) {
			ar.add(line);
		}
		
		char[] charArray = new char[8 * 1024];
	    StringBuilder builder = new StringBuilder();
	    int numCharsRead;
	    while ((numCharsRead = d.read(charArray, 0, charArray.length)) != -1) {
	        builder.append(charArray, 0, numCharsRead);
	    }
	    this.HTTPBody = builder.toString().getBytes();
	    
		return ar;
	}

	private void setHeaders() {
		ArrayList<String> headersArray = (ArrayList<String>) splitedHTTP;
		for (int i = 1; i < headersArray.size(); i++) {
			this.HTTPHeaders.put(headersArray.get(i).split(": ")[0], headersArray.get(i).split(": ")[1]);
		}
	}

	public Map<String, String> getHTTPHeaders() {
		return Collections.unmodifiableMap(this.HTTPHeaders);
	}
	
	public String getFormedResponse() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.HTTPVersion+" ");
		sb.append(this.HTTPStatusCode+" ");
		sb.append(this.HTTPStatusText+"\r\n");
		for(String header : this.HTTPHeaders.keySet()) {
	    	sb.append(header+": "+this.HTTPHeaders.get(header)+"\r\n");
	    }
		sb.append("\r\n");
		return sb.toString();
	}
	

}
