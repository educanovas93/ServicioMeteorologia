package distribucion;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLHandler implements Handler {
	
	
	private static final String rutaSchemaControl = "XML/control.xsd";
	private static final String rutaSchemaDistribucion = "XML/distribucion.xsd";
	private final XMLValidator validator = new XMLValidator();


	@Override
	public String serializar(Object o) {
		try {
			// TODO Auto-generated method stub
			if(o instanceof MensajeDistribucion) {
				String xml = serializarMensajeDistribucion((MensajeDistribucion)o);
				File f = File.createTempFile("temporal", ".xml");
				Files.write(Paths.get(f.getAbsolutePath()), xml.getBytes(), StandardOpenOption.CREATE);
				if(validator.validate(f.getAbsolutePath(), rutaSchemaDistribucion)) {
					Files.write(Paths.get("XML/mensajedistribucion.xml"), xml.getBytes(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
					return "xml&"+xml;
				}else { 
					return null;
				}
			}else {
				String xml = serializarMensajeControl((MensajeControl)o);
				File f = File.createTempFile("temporal", ".xml");
				Files.write(Paths.get(f.getAbsolutePath()), xml.getBytes(), StandardOpenOption.CREATE);
				if(validator.validate(f.getAbsolutePath(), rutaSchemaControl)) {
						Files.write(Paths.get("XML/mensajecontrol.xml"), xml.getBytes(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
					return "xml&"+xml; 
				}else {
					return null;
				}
			}
		}catch (Exception e) {
			return null;
		}
			
	}

	
	@Override
	public <T extends Mensaje> T  deserializar(String s,Class<T> classOfT) {
		if(classOfT.equals(MensajeDistribucion.class)) {
			return deserializarMensajeDistribucion(s);
		}else {
			return deserializarMensajeControl(s);
		}
	}
	
	
	


	private <T extends Mensaje> T deserializarMensajeControl(String s) {
		DocumentBuilder builder;
		MensajeControl m = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    InputSource in = new InputSource();
		    in.setCharacterStream(new StringReader(s));
		    Document doc = builder.parse(in);
		    doc.getDocumentElement().normalize();
		    
		    //recorremos las propiedades
		    String root = doc.getDocumentElement().getNodeName();
		    NodeList comandoList = doc.getElementsByTagName("comando");
		    String comando = comandoList.item(0).getTextContent();
		    
		    m = new MensajeControl(comando); 
	        
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (T)m;
	
	}


	private <T extends Mensaje> T deserializarMensajeDistribucion(String s) {
		DocumentBuilder builder;
		MensajeDistribucion m = new MensajeDistribucion();
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    InputSource in = new InputSource();
		    in.setCharacterStream(new StringReader(s));
		    Document doc = builder.parse(in);
		    doc.getDocumentElement().normalize();
		    //recorremos las propiedades
		    String root = doc.getDocumentElement().getNodeName();
		    m.setServerName(doc.getElementsByTagName("servername").item(0).getTextContent());
		    NodeList propiedades = doc.getElementsByTagName("property");
		    for (int i = 0; i < propiedades.getLength(); i++) {
		    	Node propiedad = propiedades.item(i);
		    	if(propiedad.getNodeType() == Node.ELEMENT_NODE) {
		    		Element e = (Element) propiedad;
		    		String name = e.getElementsByTagName("name").item(0).getTextContent();
		    		String unit = e.getElementsByTagName("unit").item(0).getTextContent();
		    		String bound = e.getElementsByTagName("bound").item(0).getTextContent();
		    		String value = e.getElementsByTagName("value").item(0).getTextContent();
		    		String visible = e.getElementsByTagName("visible").item(0).getTextContent();
		    		
		    		Property p = new Property(name,unit,Integer.parseInt(value),Integer.parseInt(bound));
		    		p.setVisible(Boolean.parseBoolean(visible));
		    		m.addPropiedad(p);
		    	}
			}
		    
		   
		        
	        
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (T)m;
	}


	private String serializarMensajeDistribucion(MensajeDistribucion m) {
		DocumentBuilder builder;
		String xml = "";
		try {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        DOMImplementation di = builder.getDOMImplementation();
        Document doc = di.createDocument("", "mensajedistribucion", null);
        doc.setXmlStandalone(true);
        
        Element root = doc.getDocumentElement();
        root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance","xsi:noNamespaceSchemaLocation",rutaSchemaDistribucion);
        
        //root.setAttribute("version","1.0");
        
        Element serverName = doc.createElement("servername");
        serverName.setTextContent(m.getServerName());
        root.appendChild(serverName);
        
        Element propiedades = doc.createElement("propiedades");
        
        for (Property p : m.getPropiedades()) {
			Element property = doc.createElement("property");
			propiedades.appendChild(property);
			
				Element nombre = doc.createElement("name");
				nombre.setTextContent(p.getName());
				property.appendChild(nombre);
				
				Element unit = doc.createElement("unit");
				unit.setTextContent(p.getUnit());
				property.appendChild(unit);
				
				Element bound = doc.createElement("bound");
				bound.setTextContent(String.valueOf(p.getBound()));
				property.appendChild(bound);
				
				Element value = doc.createElement("value");
				value.setTextContent(String.valueOf(p.getValue()));
				property.appendChild(value);
				
				Element visible = doc.createElement("visible");
				visible.setTextContent(String.valueOf(p.isVisilbe()));
				property.appendChild(visible);
			
			
		}
        
        
        
        root.appendChild(propiedades);
        
      //transformar
        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","4");
        
        
        
        //escribimos en fichero
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        t.transform(domSource, streamResult);
        xml = stringWriter.toString();
        //System.out.println(xml); // Optionally output to standard output.
               
        
        
        
		} catch (ParserConfigurationException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xml;
	}
	private String serializarMensajeControl(MensajeControl m) {
		DocumentBuilder builder;
		String xml = "";
		try {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        DOMImplementation di = builder.getDOMImplementation();
        Document doc = di.createDocument("", "mensajecontrol", null);
        doc.setXmlStandalone(true);
        
        Element root = doc.getDocumentElement();
        root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance","xsi:noNamespaceSchemaLocation",rutaSchemaControl);

        
        Element comandoKey = doc.createElement("comando");
        comandoKey.setTextContent(m.getComando());
        root.appendChild(comandoKey);
       
        
      //transformar
        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","4");
          
        
        //escribimos en fichero
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        t.transform(domSource, streamResult);
        xml = stringWriter.toString();
               
        
        
        
		} catch (ParserConfigurationException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xml;
	}
	
	
	
	
}
