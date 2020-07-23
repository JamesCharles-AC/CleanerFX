package modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import application.Main;
import controlador.SettingsController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;

public class Analyzer implements Runnable{


	private Map<File, String> archivos;
	private int numArchivos;
	private double totalSize;
	private Label numTotal;
	private Label rutaFicheros;
	private Button a;
	private Button b;
	private Button c;
	private ArrayList<File> listaArchivos;
	private ArrayList<String> pathErrors;
	private ProgressActions indiProgreso;

	private boolean stop = false;




	public Analyzer(ArrayList<File> array, Label labelNumeros, Label labelRutas, Button analizar, Button cancelar, Button borrar, ProgressActions indicadores) {

		this.numTotal = labelNumeros;			 
		this.rutaFicheros = labelRutas;		

		this.a = analizar;
		this.b = borrar;				
		this.c = cancelar;

		this.listaArchivos = array;				
		this.indiProgreso = indicadores;		

		this.archivos = new TreeMap<File, String>();

		this.numArchivos = 0;
		this.totalSize = 0;
		
		pathErrors = new ArrayList<String>();
	}



	@Override
	public void run() {

		stop = false;
				
		this.indiProgreso.start(this.numTotal);				// Iniciamos la animación.

		this.a.setVisible(false);							// Escondemos el boton analizar
		if ( b != null ) {
			
			this.b.setDisable(true);			// Deshabilitamos el boton borrar
		}
		this.c.setVisible(true);							// Mostramos el botón cancelar

		String[] files;										// Array para hacer la iteración de rutas
		this.archivos = new TreeMap<File, String>();


		for (int i = 0; i < this.listaArchivos.size() && !stop; i++) {

			while (i < this.listaArchivos.size() && !this.listaArchivos.get( i ).exists()) {

				pathErrors.add( this.listaArchivos.get( i ).getAbsolutePath() + "    not found.");
				i++;
			}
			
			if(i >= this.listaArchivos.size()) {
				
				break;
			}
			
			
			File carpeta = this.listaArchivos.get( i );				// Recogemos una a una las rutas de la lista
			files = carpeta.list();									// Extraemos una lista de su contenido

			for( String s : files ) {				

				String ruta = carpeta.getAbsolutePath();			// Cogemos la ruta original
				File archivo = new File(ruta + "/" + s);			// La añadimos al nombre del archivo para formar la ruta



				new Thread (()->{

					Platform.runLater(()->{

						this.rutaFicheros.setText( archivo.getPath() );		// Cambiamos el label de archivo cada vez que se analiza uno
					});

				}).start();				


				if (!archivo.isDirectory()) {

					this.numArchivos++;												// Sumamos uno al numero total de archivos

					double calc = Math.ceil( (double)archivo.length() / 1024 );		// Recogemos el tamaño del archivo

					this.totalSize += calc;

					String unidad = "KB";
					if (calc > 1023) unidad = "MB";								// Si el tamaño del archivo es de 1MB o más cambiamos la unidad

					archivos.put(archivo, calc + " " + unidad);					// Incluimos los datos del archivo

				}

				else {

					/*
					 *  Si el archivo es un directorio, llamamos a la funcion recursiva. Le pasamos:
					 *  
					 *  - el directorio a analizar
					 *  
					 */
					scanDirectory( archivo );		
				}
			}
			
		}

		// Una vez acabe el analisis, ejecutamos las siguientes lineas

		Platform.runLater(()->{
			
			this.indiProgreso.stop();					// Paramos la animación y mostramos como se rellena el circulo


			this.a.setVisible(true);								// Mostramos el boton analisis
			if( b != null) {
				
				this.b.setDisable(false);							// Habilitamos el boton borrar
			}
			this.c.setVisible(false);								// Escondemos el boton cancelar
			this.numTotal.setVisible(true);						

			// Mostramos el número total de archivos
			this.numTotal.setText( Integer.toString( this.numArchivos ) );		// Mostramos el numero total de archivos analizados


			// Mostramos un mensaje de finalización

			this.rutaFicheros.setText("Scan completed!\nTotal elements size: " + String.format("%.2f", this.totalSize / 1024) + " MB");
			

			
			stopScan();			

			
			String[] data = Main.getXmlInfo();
			
			int first = Integer.parseInt( data[0] );
			first++;
			data[0] = Integer.toString( first );
			
			long sec = Long.parseLong( data[1] );
			sec += this.numArchivos;
			data[1] = Long.toString( sec );
			

			
			Main.setXmlInfo( data );
			Main.reloadXml();

			this.totalSize = 0;
			this.numArchivos = 0;
			
			SettingsController.setErrors( pathErrors );
			pathErrors.clear();
			

		});

	}


	
	private void scanDirectory(File directorio) {

		String[] files;						// Array que contendrá la lista de ficheros

		files = directorio.list();				// Extraemos una lista de su contenido

		if(files != null && !stop) {

			for(String s : files) {				

				String ruta = directorio.getAbsolutePath();			// Cogemos la ruta original

				File archivo = new File( ruta + "/" + s );			// La añadimos al nombre del archivo para formar la ruta



				new Thread (()->{

					Platform.runLater(()->{

						this.rutaFicheros.setText( archivo.getPath() );		// Cambiamos el label de archivo cada vez que se analiza uno
					});

				}).start();


				if (!archivo.isDirectory()) {

					this.numArchivos++;													// Sumamos uno al numero total de archivos

					double calc = Math.ceil( (double)archivo.length() / 1024 );			// Recogemos el tamaño del archivo

					this.totalSize += calc;

					String unidad = "KB";
					if (calc > 1023) unidad = "MB";								// Si el tamaño del archivo es de 1MB o más cambiamos la unidad

					archivos.put(archivo, calc + " " + unidad);				// Incluimos los datos del archivo

				}

				else {

					/*
					 *  Si el archivo es un directorio, volvemos a llamar a la función.
					 *  
					 */
					scanDirectory(archivo);		
				}
			}
		}
	}

	
	
	public void deleteFiles() {
				
		int count = 0;
		
		for( File f : this.archivos.keySet()) {
			
			f.delete();
			count++;
		}
		
		if(count > 0) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Archivos eliminados");
			alert.setHeaderText(null);
			alert.setContentText("Todos los archivos han sido eliminados.");
			alert.showAndWait();
		}
		
		else if(count == 0) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Sin archivos.");
			alert.setHeaderText(null);
			alert.setContentText("No hay archivos que eliminar.\nEjecuta un nuevo analisis o vuelve en unos dias.");
			alert.showAndWait();
		}
		
	}

	
	
	public void stopScan() {	stop = true;	}

}


