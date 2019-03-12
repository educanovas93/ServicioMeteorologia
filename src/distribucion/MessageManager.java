package distribucion;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class MessageManager {

		
	private Map<String,Property> propiedades;
	private String serverName;
	private Handler handler;
	private boolean muted;

	public MessageManager(Handler handler,String serverName,Property...properties) {
		propiedades = new TreeMap<String,Property>(String.CASE_INSENSITIVE_ORDER);
		this.handler = handler;
		this.serverName = serverName;
		muted = false;
		for (Property p : properties) {
			this.propiedades.put(p.getName(), p);
		}
	}
	
	public String getServerName() {
		return this.serverName;
	}
	
	public Property getProperty(String propiedad) {
		return this.propiedades.get(propiedad);
	}
	
	private void changeValues() {
		if(!muted) {
			for (Property p : propiedades.values()) {
				if(!p.getName().equalsIgnoreCase("periodo")) {
					p.setValue(new Random().nextInt(p.getBound()));
				}
			}
		}
	}
	
	public String generateMessage(Handler h) {
		changeValues();
		MensajeDistribucion m = new MensajeDistribucion();
		m.setServerName(serverName);
		for (Property p : propiedades.values()) {
			m.addPropiedad(p);
		}
		return h.serializar(m);
	}
	
	
	public int control(String control) {
		String headers[] = control.split("&",2);
		if(headers[0].equalsIgnoreCase("json")) {
			handler = new JSONHandler();
		}else if(headers[0].equalsIgnoreCase("xml")) {
			handler = new XMLHandler();
		}
		MensajeControl m = handler.deserializar(headers[1], MensajeControl.class);
		
		if(m.getComando().split(" ")[0].equalsIgnoreCase("changeperiod")) {
			return changePeriod(m);
		}else{
			controlMessages(m);
			return -1;
		}
	}
	
	private void controlMessages(MensajeControl mensaje) {
		if(mensaje.getComando().split(" ")[0].equalsIgnoreCase("changeunit")) {
			changeUnit(mensaje);
		}else if(mensaje.getComando().split(" ")[0].equalsIgnoreCase("add")) {
			addProperty(mensaje);
		}else if(mensaje.getComando().split(" ")[0].equalsIgnoreCase("toggle")) {
			toggleProperty(mensaje);
		}else if(mensaje.getComando().equalsIgnoreCase("mute")) {
			this.muted = true;
		}else if(mensaje.getComando().equalsIgnoreCase("unmute")) {
			this.muted = false;
		}else {
			System.err.println("Malformed Control Message");
		}
	}
	
	//changeperiod 5000:5002
	public int changePeriod(MensajeControl mensaje) {
		this.propiedades.get("periodo").setValue(new Integer(mensaje.getComando().split(" ")[1].trim()));
		return new Integer(mensaje.getComando().split(" ")[1].trim());
	}
	
	//changeunit temperatura pe:4001
	public void changeUnit(MensajeControl mensaje) {
		Property p = this.propiedades.get(mensaje.getComando().split(" ")[1]);
		p.setUnit(mensaje.getComando().split(" ")[2]);
		this.propiedades.put(p.getName(),p);
	}
	
	//add radiadcion w*m2 200:4001
	public void addProperty(MensajeControl mensaje) {
		int bound = new Integer(mensaje.getComando().split(" ")[3]);
		Property aux = new Property(mensaje.getComando().split(" ")[1],mensaje.getComando().split(" ")[2],bound,new Random().nextInt(bound));
		this.propiedades.putIfAbsent(aux.getName(),aux);
	}
	
	//toggle viento:5002
	public void toggleProperty(MensajeControl mensaje) {
		System.err.println(mensaje.getComando());
		propiedades.get(mensaje.getComando().split(" ")[1]).toggleVisible();
	}
}
