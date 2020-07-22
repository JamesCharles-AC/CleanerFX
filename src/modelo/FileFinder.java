package modelo;

import java.io.File;
import java.util.ArrayList;

import javafx.application.Platform;

public class FileFinder implements Runnable{

	private ArrayList<File> listaRutas;
	private ArrayList<String> archivos;
	private String comprobar;
	private ProgressActions pa;
	
	private static boolean isFolder;
	private static boolean stop;


	// Constructores


	public FileFinder(String file, boolean isFolder, ArrayList<File> rutas, ProgressActions pa) {

		FileFinder.isFolder = isFolder;
		FileFinder.stop = false;
		
		this.comprobar = file;
		this.listaRutas = rutas;
		this.pa = pa;

		this.archivos = new ArrayList<String>();
	}



	// Métodos	


	/**
	 * Método principal para escanear rutas.
	 */

	@Override
	public void run() {

		if (!this.comprobar.isEmpty()) {

			this.pa.start( null );

			String[] files;

			for (int i = 0; i < this.listaRutas.size() && !stop; i++) {

				File directorio = this.listaRutas.get(i);
				files = directorio.list();

				for (String s : files) {

					String ruta = directorio.getAbsolutePath();
					File archivo = new File(ruta + "/" + s);

					check(archivo);
				}
			}

			Platform.runLater(()->{

				this.pa.stop();
				
				stop = false;
			});

		}
	}


	private void buscarArchivo(File directorio) {

		String[] files;
		files = directorio.list();

		if(files != null && !stop) {

			for(String s : files) {

				String ruta = directorio.getAbsolutePath();
				File archivo = new File(ruta + "/" + s);

				check(archivo);
			}

		}
	}
	

	private void check(File archivo) {

		// Si no marcamos que buscamos un directorio, no los tendremos en cuenta
		if(!isFolder) {

			if(!archivo.isDirectory()) {

				String elem1 = archivo.getName();
				String elem2 = this.comprobar;

				if(elem1.equals(elem2)) {

					this.archivos.add(archivo.getAbsolutePath());
				}
			}

			else { 

				buscarArchivo(archivo);		// Función recursiva
			}
		}

		// Tenemos en cuenta el nombre de los directorios
		else {

			String elem1 = archivo.getName();
			String elem2 = this.comprobar;

			if(elem1.equals(elem2)) {

				this.archivos.add(archivo.getAbsolutePath());
			}

			else { 

				buscarArchivo(archivo);		// Función recursiva
			}
		}
	}

	
	public void stopScan() {
		
		stop = true;
	}
	
	
	public ArrayList<String> getResultados(){
		
		return this.archivos;
	}


}


