package distribucion;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MensajeDistribucion extends Mensaje{

	
	
	List<Property> propiedades;
	public String serverName;
	
	
	public MensajeDistribucion() {
		propiedades = new LinkedList<>();
	}
	
	public String getServerName(){
		return this.serverName;
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	
	public void addPropiedad(Property propiedad) {
		propiedades.add(propiedad);
	}
	
	public void imprimir() {
		for (Property property : propiedades) {
			System.out.println(property.getName());
		}
	}
	
	public List<Property> getPropiedades(){
		return Collections.unmodifiableList(this.propiedades);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		//sb.append(this.serverName+"\n");
		//if(!muted) {
			for (Property p : propiedades) {
				if(p.isVisilbe()) {
					sb.append(p.getName()+" ("+p.getUnit()+")"+" : "+p.getValue()+"\n");
				}
			}
			sb.append("\n");
		//}
		return sb.toString();	
	}
	
}
