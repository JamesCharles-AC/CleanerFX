package modelo;

import java.util.Random;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class ProgressActions {
	
	/**
	 * Indicador que mostrará la animación final.
	 */
	ProgressIndicator inicial;
	
	/**
	 * Indicador con animación de progreso.
	 */
	ProgressIndicator cargando;
	
	
	
	public ProgressActions(ProgressIndicator off, ProgressIndicator on) {
		
		this.inicial = off;
		this.cargando = on;
	}
	
	
	

	public void start(Label total) {
		
		cargando(total); 
	}

	
	private void cargando(Label total) {

		if (total != null) total.setVisible(false);
		
		reiniciar();
		
		this.inicial.setVisible(false);
		this.cargando.setVisible(true);
	}
	

	private void reiniciar() {
		
		this.inicial.setProgress(0);
	}
	
	
	private void finalizado() {

		this.inicial.setVisible(true);
		this.cargando.setVisible(false);
		
		new Thread(() -> {
			
	         for(int i = 0; i <=100; i++){
	        	 
	            final int position = i;
	            
	            Platform.runLater(() -> {
	            	
	                this.inicial.setProgress(position/100.0);
	            });
	            
	            Random r = new Random();
	            int num = r.nextInt(3) + 1;
	            
	            try{
	            	
	                Thread.sleep(5 * num);
	                
	            } catch(Exception e){ 
	            	
	            	System.err.println(e); 
	            }
	         }
	         
	    }).start();
		
	}
	

	public void stop() {
		
		finalizado();
	}
	
	
	/**
	 * Inicializa el Indicador principal.
	 * 
	 * @param i Indicador cedido por el Controlador
	 */
	public void setInicial(ProgressIndicator i) {
		this.inicial = i;
	}
	
	/**
	 * Inicializa el Indicador animado.
	 * 
	 * @param i Indicador secundario cedido por el Controlador
	 */
	public void setCarga(ProgressIndicator i) {
		this.cargando = i;
	}
}






