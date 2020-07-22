package modelo;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import application.Main;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ReaderWriter{


	private ArrayList<String> pathList;
	private File comprobar;
	private Label numArchivos;
	private Label rutaArchivos;
	private Button analisis;
	private Button cancelar;

	private ProgressActions indicadores;
	private static String[] data = new String[2];


	public ReaderWriter(ProgressActions progress, Button analisis, Button cancelar, Label numArchivos, Label rutaArchivos) {

		this.indicadores = progress;
		this.analisis = analisis;
		this.cancelar = cancelar;

		this.numArchivos = numArchivos;
		this.rutaArchivos = rutaArchivos;
		
		this.pathList = new ArrayList<String>();			// Nuevo Array de rutas
	}


	
	
	public void inicializarLista(){

		getXmlInfo();										// Nuevo Array de rutas
		
		Main.setXmlInfo( data );
		Main.setPathList( pathList );							// Enviamos la lista de rutas al Main
	}



	
	public static void generateXML(ArrayList<String> rutas) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation implementation = builder.getDOMImplementation();
		Document document = implementation.createDocument(null, "config", null);
		document.setXmlVersion("1.0");


		Element raiz = document.getDocumentElement();


		Element listNode = document.createElement("pathList"); 

		//Por cada ruta creamos una entrada en el xml
		for(int i = 0; i < rutas.size(); i++){


			Element itemNode = document.createElement("item"); 


			Element pathNode = document.createElement("path"); 
			Text nodePathValue = document.createTextNode(rutas.get(i));
			pathNode.appendChild(nodePathValue);

			itemNode.appendChild( pathNode );
			
			listNode.appendChild( itemNode );

		}                
		
		Element regNode = document.createElement("history");
		
		
		Element countNode = document.createElement("tests");
		Text countNodeValue = document.createTextNode( data[0] );
		countNode.appendChild( countNodeValue );
		
		
		Element filesNode = document.createElement("files");
		Text filesNodeValue = document.createTextNode( data[1] );
		filesNode.appendChild( filesNodeValue );
		

		regNode.appendChild( countNode );
		regNode.appendChild( filesNode );
		
		raiz.appendChild( listNode );
		raiz.appendChild( regNode ); 


		//Generate XML
		Source source = new DOMSource(document);

		//Indicamos donde lo queremos almacenar

		File xml = new File( "bin/config.xml" );

		Result result = new StreamResult( xml ); 		//nombre del archivo
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		tf.setOutputProperty(OutputKeys.METHOD, "xml");
		tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		tf.transform(source, result);

	}


	private void getXmlInfo() {

		try {

			File xmlFile = Main.getRutas();			

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			//Recuperar una lista con los elementos DIA
			NodeList listaItems = doc.getElementsByTagName("item");

			//Recorrer lista de los elementos DIA
			for (int i = 0; i < listaItems.getLength(); i++) {

				Node item = listaItems.item(i);


				if ( item.getNodeType() == Node.ELEMENT_NODE ) {

					Element e = (Element) item;

					String path = e.getElementsByTagName("path").item(0).getTextContent();					

					this.pathList.add( path );

				}
			}


			String tests = doc.getElementsByTagName("tests").item(0).getTextContent();
			String files = doc.getElementsByTagName("files").item(0).getTextContent();					

			data[0] = tests;
			data[1] = files;


		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	public void anyadirRuta(String s) {

		if( s == null) {

			Main.alerts( "Ruta no valida", null );
			return;

		}

		File rutas = Main.getRutas();		// Archivo de propiedades de la App

		boolean apto = true;

		if(!pathList.isEmpty()) {				// Si la lista no está vacía, la comprobamos

			String[] listed;
			String[] notListed;

			notListed = s.split("\\\\");			// Rellenamos el array con los directorios de la nueva ruta

			for(String e : pathList) {

				int compared = 0;

				listed = e.split("\\\\");			// Rellenamos cada vez este array con las rutas de la lista una a una

				int min = ( listed.length < notListed.length ) ? listed.length : notListed.length;

				for (int i = 0; i < min; i++) {

					if( listed[ i ].equalsIgnoreCase( notListed[ i ]) ) {

						compared++;				// Si coinciden las posiciones se suma uno al contador
					}
				}

				apto = (compared == listed.length) ? false : true;		// Si el contador es igual a la longitud de la ruta actualmente 
				// listada se rechaza la nueva ruta y no comprobamos más rutas.
				if (!apto) break;
			}
		}

		if(apto) {

			try{

				//Escritura
				File directorio = new File( s );

				if (directorio.exists() && directorio.isDirectory() ) {			// Si el directorio por añadir existe


					pathList.add( s );								// Añadimos la ruta a la lista

					Main.setPathList( this.pathList );					// Enviamos la lista al Main


					generateXML( this.pathList );		// Generamos el xml de nuevo


					boolean an = Main.alerts( "Analizar ruta", directorio.getAbsolutePath());


					if ( an ) {			// Si se confirma, añadimos la ruta al archivo y analizamos el directorio


						this.comprobar = directorio;			// Guardamos la ruta para el siguiente método

						analizarRutaNueva();
					}
				}

				else {

					Main.alerts( "Ruta no valida", null );
				}
			}

			catch (Exception e) {

				e.printStackTrace();
				System.err.println("Problemas con el archivo " + rutas.getName());
			}
		}

		else {

			Main.alerts( "Ruta listada", s );
		}
	}


	private void analizarRutaNueva() {

		ArrayList<File> dir = new ArrayList<File>();

		dir.add( this.comprobar );

		Analyzer test = new Analyzer(dir, this.numArchivos, this.rutaArchivos, this.analisis, this.cancelar, null, this.indicadores);

		Thread t = new Thread( test );
		t.start();

	}



	
	public static void setData(String[] newData) {   data = newData;	}
	


}
