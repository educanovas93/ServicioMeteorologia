package meteorologia;

import distribucion.ClienteControl;
import distribucion.ClienteReceptor;
import distribucion.Server;
import distribucion.Server2;
import http.Servidor;
import mail.ServidorMail;

public class launcher {

	
	public static void main(String[] args) throws InterruptedException {
		
		ClienteControl control = new ClienteControl(Server.getIpFromInterface("en0"));
		ClienteReceptor receptor = new ClienteReceptor(4000);
		Servidor server = new Servidor(8080, false, "web");
		Servidor secureServer = new Servidor(4430,true,"web");
		ServidorMail mailServer = new ServidorMail();
		Server dist = new Server("en0","SERVIDOR 1",4000,4001,1000);
		
		dist.start();
		mailServer.start();
		server.start();
		secureServer.start();
		control.start();
		receptor.start();

		
		
		
		for (int i = 2;i < 9;i++ ) {
			String aux = "500"+i;
			int port = Integer.parseInt(aux);
			
			Server2 s = new Server2("en0","SERVIDOR "+i,4000,port,1000);
			Thread.sleep(4000);
			s.start();
			
		}
		
		
	}
}
