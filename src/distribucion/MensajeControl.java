package distribucion;

public class MensajeControl extends Mensaje {

	
	
	String comando;
	
	
	public MensajeControl(String comando) {
		this.comando = comando;
	}
	
	public String getComando() {
		return this.comando;
	}
	
	
}
