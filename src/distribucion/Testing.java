package distribucion;

import java.util.Random;

import com.google.gson.Gson;

public class Testing {

	public static void main(String[] args) {
		Handler h = new XMLHandler();
		MensajeControl m = new MensajeControl("peine");
		System.out.println(h.serializar(m));
		
		Random r = new Random();
		Property temperatura = new Property("Temperatura","º",r.nextInt(50),50);
		Property humedad = new Property("Humedad","%",r.nextInt(100),100);
		Property presion = new Property("Presion","mm HG",r.nextInt(760),760);
		Property frecuencia = new Property("Periodo","ms",3000,15000);
		MensajeDistribucion md = new MensajeDistribucion();
		md.addPropiedad(temperatura);
		md.addPropiedad(humedad);
		md.addPropiedad(presion);
		md.addPropiedad(frecuencia);
		
		String serializado = h.serializar(md);
		System.out.println(serializado);
		MensajeDistribucion des = h.deserializar(serializado,MensajeDistribucion.class);
		
		System.out.println(des.toString());
		
		System.out.println(h.deserializar(h.serializar(m), MensajeControl.class).getComando());
	}
}
