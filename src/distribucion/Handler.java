package distribucion;

public interface Handler {

	
	public <T extends Mensaje> T deserializar(String s,Class<T> classOfT);
	public String serializar(Object o);
}
