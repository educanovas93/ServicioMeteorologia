package http;

public class VirtualHost {

	
	private String serverName;
	private String serverDirectory;
	
	
	
	public VirtualHost(String serverName, String serverDirectory) {
		this.serverName = serverName;
		this.serverDirectory = serverDirectory;
	}
	
	public String getServerName() {
		return this.serverName;
	}
	
	
	public String getServerDirectory() {
		return this.serverDirectory;
	}
}
