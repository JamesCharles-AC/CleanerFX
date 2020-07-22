package controlador;


import java.util.ArrayList;

import application.Main;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class SettingsController {


    @FXML
    private Label warningNumber;    
    public static Label warningNumberPlayable;

    @FXML
    private FontAwesomeIconView warnings;

    @FXML
    private ScrollPane warningPane;

    @FXML
    private VBox warningList;
    private static VBox warningListPlayable;

	@FXML
	private VBox pathList;
	@FXML
	private Button remove;
	@FXML
	private Label totalTests;
	private static Label totalTestsPlayable;

	@FXML
	private Label totalFiles;
	private static Label totalFilesPlayable;

	@FXML
	private Button cleanHistory;
	
	private ArrayList<String> paths;
	
	
	public void setSettings(ArrayList<String> rutas, String[] info) {
		
		setElements( info );
		
		setPaths(rutas);
		createList( paths );
	}
	
	
	private void setElements(String[] info) {

		totalFilesPlayable = this.totalFiles;
		totalTestsPlayable = this.totalTests;
		
		setLabels( info );
		setWarningElements();
		
	}

	
	public static void setLabels( String[] data ) {
		
		
		String tests = data[0];
		String files = data[1];
		long check = Long.parseLong( files );
		
		if ( check > 99999) {
			
			files = Long.toString( check ).substring(0, 3) + " K";
		}
		
		if ( check > 999999) {
			
			files = Long.toString( check ).substring(0, 2) + " M";
		}
		
		totalTestsPlayable.setText( tests );
		totalFilesPlayable.setText( files );
	}


	
	
	private void createList(ArrayList<String> rutas) {
		
		
		double width = setBoxesLength();				// Recogemos la longitud de la ruta más larga de la lista

		for (int i = 0; i < paths.size(); i++) {

			CheckBox path = new CheckBox();			// Creamos un nuevo checkbox por cada ruta
			path.setText( paths.get( i ));			// Le incluimos el texto correspondiente

			
			path.setMinWidth( 338 );					// Asignamos estilos en común
			path.setMinHeight( 35 );		
			path.setPadding( new Insets( 10 ));
			

			if(width > 338) path.setPrefWidth( width );

			if(i % 2 == 0) path.setStyle("-fx-background-color: #fff");
			else path.setStyle("-fx-background-color: #eee");


			this.pathList.getChildren().add(path);			// Lo añadimos al VBox
		}
		
	}

	// Asignamos las rutas al atributo de la clase
	private void setPaths(ArrayList<String> rutas) {	paths = rutas;		}
	
	// Extraemos la longitud en comun para todas las rutas
	private double setBoxesLength() {

		this.pathList.getChildren().clear();

		double max = 0;

		for(String s : paths) {								// Extraemos las rutas una a una

			max = ( s.length() > max) ? s.length() : max;	// Comprobamos su longitud y nos quedamos la más larga	
		}

		max *= 6;							// la modificamos para que encajen completamente
		
		return max;
	}

	
	
	
	@FXML
	void removePaths(ActionEvent event) throws Exception {

		ArrayList<String> rutas = new ArrayList<String>();					// Array que contendrá las rutas a eliminar
		
		for (int i = 0; i < pathList.getChildren().size(); i++) {

			CheckBox c = (CheckBox) this.pathList.getChildren().get(i);		// Extraemos los checkBox uno a uno

			if(c.isSelected()) {						// Comprobamos si estan seleccionados para añadirlos al Array o no

				rutas.add( c.getText() );
			}
		}
		
		rutas = Main.removePaths( rutas );				// Enviamos el Array al Main para ser procesado
				
		createList( rutas );							// Reiniciamos la lista
	}

	
	
	
	@FXML
	void cleanHistory(ActionEvent event) {
		
		String[] clear = {"0", "0"};
		
		Main.setXmlInfo( clear );
		Main.reloadXml();
	}
	
	
	
	
	private void setWarningElements() {
		
		warningListPlayable = this.warningList;
		warningNumberPlayable = this.warningNumber;
	}
	
	
	@FXML
	void showWarnings(MouseEvent event) {
		
		this.warningPane.setVisible(   this.warningPane.isVisible() ? false:true     );
		warningNumberPlayable.setVisible( false );
		MainController.hideWarnings();
	}
	
	
	public static void setErrors(ArrayList<String> errors) {
		
		warningListPlayable.getChildren().clear();

		if(errors.size() != 0) {
			
			MainController.showWarnings();
			warningNumberPlayable.setVisible( true );
			warningNumberPlayable.setText( Integer.toString( errors.size() ) );
			
			
			warningListPlayable.getChildren().clear();
	
			double max = 0;
	
			for(String s : errors) {								// Extraemos las rutas una a una
	
				max = ( s.length() > max) ? s.length() : max;	// Comprobamos su longitud y nos quedamos la más larga	
			}
	
			max *= 6;							// la modificamos para que encajen completamente
			
			for (int i = 0; i < errors.size(); i++) {
	
				Label path = new Label();			// Creamos un nuevo checkbox por cada ruta
				path.setText( errors.get( i ));			// Le incluimos el texto correspondiente
	
	
				path.setMinWidth( 338 );					// Asignamos estilos en común
				path.setMinHeight( 35 );		
				path.setPadding( new Insets( 10 ) );
	
	
				if(max > 338) path.setPrefWidth( max );
	
				if(i % 2 == 0) path.setStyle("-fx-background-color: #fff");
				else path.setStyle("-fx-background-color: #eee");
	
	
				warningListPlayable.getChildren().add(path);			// Lo añadimos al VBox
			}
		}

	}

	
	

	
	
}
