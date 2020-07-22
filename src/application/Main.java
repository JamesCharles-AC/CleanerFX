package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import controlador.SettingsController;
import javafx.stage.Stage;
import modelo.ReaderWriter;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;


public class Main extends Application {

	private static File rutas;
	private static ArrayList<String> pathList;
	private static String[] infoAnalisis;


	@Override
	public void start(Stage primaryStage) {

		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("/vista/main.fxml"));

			Pane ventana = (Pane) loader.load();								// Cargo la informacion de la ventana en el panel


			// Cargo el scene
			Scene scene = new Scene(ventana);									// Monto la escena

			scene.getStylesheets().add("/vista/application.css");				// Aplico CSS

			// Seteo la scene y la muestro
			primaryStage.setScene(scene);
			primaryStage.setResizable( false );
			primaryStage.sizeToScene();
			primaryStage.show();												// Muestro el escenario

		} catch (IOException e) {

			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}



	public static void setRutas() {

		try {

			File p = new File("bin/config.xml");					// Preparamos el archivo de rutas.

			if( !p.exists() ) {									// Si no existe lo creamos.

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Para empezar..");
				alert.setHeaderText(null);
				alert.setContentText("Cierra esta ventana, mueve la aplicación a un directorio propio "
						+ "y ejecutala. Pulsa Aceptar en ese momento y crearemos los archivos necesarios.");

				Optional<ButtonType> res = alert.showAndWait();


				if (res.get() == ButtonType.OK) {				// Si pulsa OK se confirma la creación del archivo

					new File("bin").mkdir();

					pathList = new ArrayList<String>();

					pathList.add("C:\\Users\\Public\\Downloads");

					String[] data = {"0", "0"};

					ReaderWriter.setData( data );
					ReaderWriter.generateXML( pathList );

				}

			}

			Main.setRutas( p );					// Si existe o ha sido creado el archivo de rutas lo asignamos al atributo de clase.


		} catch (Exception e) {

			System.exit(0);
		}
	}

	// Asignamos el archivo de rutas al archivo de la clase
	public static void setRutas(File p) {	rutas = p;	}

	// Devolvemos el archivo de rutas
	public static File getRutas() {			return rutas;	}


	
	
	// Lista de rutas del archivo en string
	public static void setPathList(ArrayList<String> list) {	pathList = list;	}

	// Devolvemos las rutas del archivo almacenadas
	public static ArrayList<String> getPathList() {				return pathList;	}


	
	
	// Lista de rutas del archivo en string
	public static void setXmlInfo(String[] data) {	infoAnalisis = data;	}

	// Lista de rutas del archivo en string
	public static String[] getXmlInfo() {			 return infoAnalisis;	}



	
	public static ArrayList<String> removePaths( ArrayList<String> excluded ) throws Exception {

		rutas.delete();							// Borramos el archivo del sistema

		for (int j = 0; j < pathList.size(); j++) {

			for (int j2 = 0; j2 < excluded.size(); j2++) {

				if( pathList.get(j).equals( excluded.get(j2) ) ) {

					pathList.remove(j);			// Eliminamos las rutas que coinciden
				}
			}
		}


		reloadXml();

		return pathList;

	}


	public static void reloadXml() {

		try {

			File p = new File("bin/config.xml");					// Preparamos el archivo de rutas.

			ReaderWriter.setData( infoAnalisis );
			ReaderWriter.generateXML( pathList );
			
			SettingsController.setLabels( infoAnalisis );
			
			Main.setRutas( p );					// Si existe o ha sido creado el archivo de rutas lo asignamos al atributo de clase.


		} catch (Exception e) {

			System.exit(0);
		}
	}



	public static boolean alerts(String s, String dir) {

		Alert alert;
		boolean check = false;
		Optional<ButtonType> res;

		switch ( s ) {
		case "Ruta no valida":

			alert = new Alert(AlertType.WARNING);
			alert.setTitle("Ruta no valida");
			alert.setHeaderText(null);
			alert.setContentText("Por favor, introduce una ruta válida (Unidad:\\Directorio\\deseado)");
			alert.showAndWait();

			break;

		case "Unidad no encontrada":

			alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Unidad no encontrada");
			alert.setHeaderText(null);
			alert.setContentText("La ruta (" + dir + ") no existe. Indica una unidad válida.");
			alert.showAndWait();
			res = alert.showAndWait();

			if( res.get().equals( ButtonType.OK )) {

				check = true;
			}

			break;

		case "Ruta listada":

			alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Ruta listada");
			alert.setHeaderText(null);
			alert.setContentText("La ruta (" + dir + ") ya forma parte de la lista");
			alert.showAndWait();

			break;

		case "Analizar ruta":


			alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Nueva ruta añadida");
			alert.setHeaderText(null);
			alert.setContentText("Quieres analizar la ruta ahora?\n " + dir);
			res = alert.showAndWait();

			if( res.get().equals( ButtonType.OK )) {

				check = true;
			}

			break;
		}

		return check;


	}


}
