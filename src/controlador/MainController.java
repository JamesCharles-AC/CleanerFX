package controlador;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert.AlertType;
import modelo.Analyzer;
import modelo.ProgressActions;
import modelo.ReaderWriter;


/**
 * MainController.java maneja los eventos principales de la aplicación.
 * 
 * @author Juan Carlos Aguirre Cortés
 * @version 1.0
 *
 */

public class MainController{


	@FXML private Button analizar;
	@FXML private Button cancelar;
	@FXML private Button limpiar;
	@FXML private Button addPath;
	@FXML private TextField routeName;
	@FXML private Label infoAnalisis;
	@FXML private Label fileCount;
	@FXML private ProgressIndicator indAnalisis;
	@FXML private ProgressIndicator loadAnalisis;
	@FXML private Button options;
    @FXML private Label warningNumber;    
    private static Label warningNumberPlayable;
    
	@FXML private Button about;
	@FXML private Button search;

	@FXML private Pane blend;
	@FXML private Pane contentSearch;
	@FXML private Pane contentOptions;
	@FXML private SearchController searchControllerController;
	@FXML private SettingsController settingsControllerController;


	private Analyzer analyzer;
	private ReaderWriter rw;
	private ProgressActions indicadores;


	/**
	 * Inicializa los atributos al llamar al controlador
	 */
	@FXML
	void initialize() {

		Main.setRutas();

		// Añado los indicadores a la clase que los muestra o no
		indicadores = new ProgressActions(this.indAnalisis, this.loadAnalisis);		

		rw = new ReaderWriter(this.indicadores, this.analizar, this.cancelar, this.fileCount, this.infoAnalisis);
		rw.inicializarLista();					

		sendOptionsPaths();
		
		setWarning();

	}

	
	// Creamos el menu de rutas del panel de Opciones
	public void sendOptionsPaths() {

		this.settingsControllerController.setSettings( Main.getPathList(), Main.getXmlInfo() );
	}


	private void setAnalyzer() {

		ArrayList<String> lista = Main.getPathList();
		ArrayList<File> archivos = new ArrayList<File>();		// Array que contendrá los directorios dadas las rutas


		if( !lista.isEmpty() ) {

			for (String s : lista) {

				File f = new File( s );			// Proceso cada ruta y las añado al array indicado

				archivos.add( f );
			}


			// El Analyzer necesita todos los elementos para poder activar y desactivar los elementos durante su ejecución
			this.analyzer = new Analyzer( archivos, this.fileCount, this.infoAnalisis, this.analizar, 
										  this.cancelar, this.limpiar, this.indicadores );

		}
		
		else {
			
			this.analyzer = new Analyzer( null, this.fileCount, this.infoAnalisis, this.analizar, 
					  this.cancelar, this.limpiar, this.indicadores );
		}

	}


	@FXML
	void analizar(ActionEvent event) {

		analizar();
	}


	private void analizar() {
		
		setAnalyzer();

		
		// Inicio el hilo de Analisis si la lista no está vacía

		if(!Main.getPathList().isEmpty()) {
			
			Thread t = new Thread( this.analyzer );
			t.start();			
		}
		
		
		// Se advierte al usuario si no hay rutas en la lista
		else {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Analisis de directorios");
			alert.setHeaderText(null);
			alert.setContentText("Ahora mismo no hay ninguna ruta por analizar.\nIntroduce una ruta en el campo de texto.");
			alert.showAndWait();
		}
	}
	

	@FXML
	void stopScan(ActionEvent event) {

		this.analyzer.stopScan();
	}

	
	
	
	@FXML
	void limpiar(ActionEvent event) {

		this.analyzer.deleteFiles();
	}


	@FXML
	private void addPathKey(KeyEvent event) {
		
		if(event.getCode() == KeyCode.ENTER) {

			this.routeName.setDisable( true );
			addPath( new ActionEvent() );
		}

		this.routeName.setDisable( false );
		
	}
	

	@FXML
	private void addPath(ActionEvent event) {

		String s = this.routeName.getText();						// Recogemos la ruta del campo de texto
		String s2 = "";
		char[] path = s.toCharArray();								// Separamos los directorios en un Array de caracteres
		
		if (s.length() != 0) {

			for (int i = 0; i < path.length; i++) {

				if(i == 0) {

					s2 += Character.toString( path[i] ).toUpperCase();			// El primer elemento es la letra de Unidad. La trato para tener una presentación limpia.
				}

				else {
										
					s2 += Character.toString( path[i] );			
				}
			}
			

			if (s2.length() < 3) {

				if( (int)s2.charAt( 0 ) > 64 && (int)s2.charAt( 0 ) < 91 ) {
									
					s2 = s2.charAt( 0 ) + ":/";
				}
				
				else {
					
					s2 = null;
				}
			}

			if( s2 != null ) {

				File del = new File( s2 );
				s2 = del.getAbsolutePath();
			}

			this.routeName.setText( s2 );


			rw.anyadirRuta( s2 );

			sendOptionsPaths();
			
			setAnalyzer();

		}
	}



	
	@FXML
	private void showSearch(ActionEvent event) {

		
		this.contentSearch.setVisible(  this.contentSearch.isVisible() ? false:true   );

		blendBackground();

	}

	
	@FXML
	private void showOptions(ActionEvent event) {
		
		
		this.contentOptions.setVisible(  this.contentOptions.isVisible() ? false:true   );

		blendBackground();

	}

	
	@FXML
	private void openURI(ActionEvent event) {

		if(Desktop.isDesktopSupported()){

			try {
				Desktop.getDesktop().browse(new URI("https://jamescharles-ac.github.io"));

			} catch (Exception e1) {
				e1.printStackTrace();
			} 
		}

	}

	
	private void blendBackground() {

		if(this.contentSearch.isVisible() || this.contentOptions.isVisible()) {

			this.blend.setVisible(true);
		}

		else if ( !this.contentSearch.isVisible() && !this.contentOptions.isVisible()) {

			this.blend.setVisible(false);
		}
	}


	
	
	public void setWarning() {
		
		warningNumberPlayable = this.warningNumber;
	}
	
	
	public static void showWarnings() {
		
		warningNumberPlayable.setVisible( true );
	}

	
	public static void hideWarnings() {
		
		warningNumberPlayable.setVisible( false );
	}


}
