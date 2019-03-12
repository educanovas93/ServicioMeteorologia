package distribucion;

public class Cliente {

	
	public static void main(String[] args) {
		
		ClienteControl control = new ClienteControl(Server.getIpFromInterface("en0"));
		ClienteReceptor receptor = new ClienteReceptor(4000);
		control.start();
		receptor.start();
	}
	
}
