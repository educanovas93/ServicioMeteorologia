package http.Response;

import http.HTTPRequest;
import http.HTTPResponse;
import http.VirtualHost;

public class ResponseHEAD extends ResponseGET{

	public ResponseHEAD(VirtualHost virtualHost,HTTPRequest HTTPRequest) {
		super(virtualHost,HTTPRequest);
	}

	@Override
	public HTTPResponse getHttpResponse() {
		HTTPResponse respAux = super.getHttpResponse();
		respAux.setBodyFile(null);
		return respAux;
	}
}
