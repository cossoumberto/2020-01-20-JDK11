package it.polito.tdp.artsmia;

import java.net.URL;
import java.util.ResourceBundle;

import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.artsmia.model.Artist;
import it.polito.tdp.artsmia.model.Coppia;
import it.polito.tdp.artsmia.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ArtsmiaController {
	
	private Model model ;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnArtistiConnessi;

    @FXML
    private Button btnCalcolaPercorso;

    @FXML
    private ComboBox<String> boxRuolo;

    @FXML
    private TextField txtArtista;

    @FXML
    private TextArea txtResult;

    @FXML
    void doArtistiConnessi(ActionEvent event) {
    	txtResult.clear();
    	if(model.getGrafo()==null)
    		txtResult.setText("Crea un grafo");
    	else {
    		if(model.getCoppieArchi().size()==0)
    			txtResult.setText("Non sono presenti archi");
    		else
    			for(Coppia c : model.getCoppieArchi())
    				txtResult.appendText(c.getA1() + " " + c.getA2() + " " + c.getPeso() + "\n");
    	}
    }

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	txtResult.clear();
    	String id = txtArtista.getText().trim();
    	Artist source = null;
    	for(Artist a : model.getGrafo().vertexSet()) 
    		if(a.getId().toString().equals(id)) 
    			source = a;
    	if(source==null)
    		txtResult.setText("Id non non presente");
    	else if(model.percorso(source).size()==0)
    		txtResult.setText("Vertice non connesso");
    	else {
    		for(Artist a : model.percorso(source))
    			txtResult.appendText(a + "\n");
    		txtResult.appendText("Il peso degli archi è " + model.getBestPeso());
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	String role = boxRuolo.getValue();
    	if(role!=null) {
    		model.creaGrafo(role);
    		txtResult.setText("GRAFO CREATO\n#VERTICI: " + model.getGrafo().vertexSet().size()
    				+ " #ARCHI: " + model.getGrafo().edgeSet().size());
    	} else 
    		txtResult.setText("Seleziona un ruolo");	
    }

    public void setModel(Model model) {
    	this.model = model;
    	boxRuolo.getItems().addAll(model.getRoles());
    }

    
    @FXML
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert btnArtistiConnessi != null : "fx:id=\"btnArtistiConnessi\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert btnCalcolaPercorso != null : "fx:id=\"btnCalcolaPercorso\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert boxRuolo != null : "fx:id=\"boxRuolo\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert txtArtista != null : "fx:id=\"txtArtista\" was not injected: check your FXML file 'Artsmia.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Artsmia.fxml'.";

    }
}
