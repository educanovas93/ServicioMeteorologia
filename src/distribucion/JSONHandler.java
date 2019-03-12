package distribucion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.google.gson.Gson;

public class JSONHandler implements Handler{

	Gson g = new Gson();
	
	@Override
	public String serializar(Object o) {
		String json = g.toJson(o);
		try {
			Files.write(Paths.get("JSON/mensajes.json"), json.getBytes(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "json&"+json;
	}

	@Override
	public <T extends Mensaje> T deserializar(String s,Class<T> classOfT) {
		return g.fromJson(s,classOfT);
	}
}
