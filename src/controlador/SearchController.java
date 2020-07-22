package controlador;

import java.io.File;
import java.util.ArrayList;

import application.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import modelo.FileFinder;
import modelo.ProgressActions;

public class SearchController {

    @FXML private TextField fileName;
    @FXML private TextField fileExtension;
    @FXML private TextField customDisk;
    @FXML private Button startSearch;
    @FXML private Button cancelSearch;
    @FXML private Button showResults;
    @FXML private CheckBox isFolder;
    @FXML private CheckBox cDisk;
    @FXML private CheckBox dDisk;
    @FXML private Label resultados;
    @FXML private ProgressIndicator analysisOff;
    @FXML private ProgressIndicator analysisOn;
    @FXML private VBox list;
    @FXML private ScrollPane resultList;

    private ArrayList<String> paths;
    private ArrayList<File> rutas;
    private FileFinder fileFinder;
    private ProgressActions pa;
    
    private Thread t;
    
    
    
    @FXML
    void initialize() {
    	
    	this.pa = new ProgressActions(this.analysisOff, this.analysisOn);
    }

    
    
    
    @FXML
    void searchFile(ActionEvent event) throws InterruptedException {

    	if( !this.list.getChildren().isEmpty() ) {
    		
    		this.list.getChildren().clear();
    	}
    	
    	this.resultados.setVisible(false);
    	this.showResults.setDisable(true);
    	
    	this.rutas = new ArrayList<File>();
    	
    	String file;
    	
    	String name = this.fileName.getText();
    	String ext = this.fileExtension.getText();
    	String custom = this.customDisk.getText();
    	
    	if (!ext.isEmpty()) {
    		
    		if (!ext.contains(".")) ext = "." + ext;
    	}
    	
    	if (cDisk.isSelected()) {
    		
    		File ruta = new File("C:/");
    		this.rutas.add(ruta);
    	}
    	
    	if (dDisk.isSelected()) {
    		
    		File ruta = new File("D:/");
    		this.rutas.add(ruta);
    	}
    	
    	if (!custom.isEmpty()) {
    		
    		if( (int)custom.charAt( 0 ) > 64 && (int)custom.charAt( 0 ) < 91 ) {
				
				custom = custom.charAt( 0 ) + ":/";
			}
    		
    		
    		File ruta = new File( custom );
    		
    		if( ruta.exists() ) this.rutas.add(ruta);
    		else {
    			
    			Main.alerts("Unidad no encontrada", custom);
    		}
    		
    	}
    	
    	if(rutas.isEmpty()) {
    		
    		File ruta = new File("C:/");
    		this.rutas.add( ruta );
    	}
    	
    	
    	file = (ext == "") ? name : name + ext;
    	
    	boolean folder = this.isFolder.isSelected();

    	if(!file.isEmpty()) {
    		
    		fileFinder = new FileFinder(file, folder, this.rutas, this.pa);
        	
        	t = new Thread( fileFinder );  
        	
        	t.start();
        	        	
        	new Thread(()->{    
        		
        		try {
        			
					t.join();

		        	Platform.runLater(()->{  

			        	this.paths = fileFinder.getResultados();
			        	
			        	this.resultados.setVisible(true);
			        	this.resultados.setText( Integer.toString( this.paths.size() ) );
			        	this.showResults.setDisable(false);

			        	setResults();						
						
					});
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}).start();
        	
    	}
    }
    
    
    private void setResults() {
    	
    	double max = 0;
    	
    	for (String s : this.paths) {
    		
    		max = ( s.length() > max) ? s.length() : max;
    	}
    	
    	max *= 6;
    	
    	for (int i = 0; i < this.paths.size(); i++) {
			
    		Label label = new Label();
    		label.setMinWidth( 337 );
    		label.setMinHeight( 35 );
    		
    		if(max > 337) label.setPrefWidth( max );
    		
    		label.setPadding( new Insets(5));
    		
    		label.setText( this.paths.get( i ) );
    		
    		if( i % 2 == 0) label.setStyle( "-fx-background-color: #eee" );
    		this.list.getChildren().add( label );
		}
    }
    
    
    @FXML
    private void showResults() {
    	
    	if(!this.resultList.isVisible()) {
    		
	    	this.resultList.setVisible(true);
    	}
    	
    	else  this.resultList.setVisible(false);

    }
    
    
    
    
    @FXML
    void cancelSearch(ActionEvent event){
    	
    	try {
    		
    		fileFinder.stopScan();
    	}
    	catch (Exception e) {
			// TODO: handle exception
		}
    	
    }

}
