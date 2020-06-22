package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private ArtsmiaDAO dao;
	private Map<Integer, Artist> idMapArtists;
	private Graph<Artist, DefaultWeightedEdge> grafo;
	private List<Coppia> listaCoppieArchi;
	private List<Artist> bestPercorso;
	private Integer bestNum;
	
	public Model () {
		dao = new ArtsmiaDAO();
		idMapArtists = new HashMap<>();
	}
	
	public List<String> getRoles(){
		return dao.getRoles();
	}
	
	public void creaGrafo(String role) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, dao.getArtistsRole(role, idMapArtists));
		listaCoppieArchi = dao.getCoppieArtist(role, idMapArtists);
		for(Coppia c : listaCoppieArchi)
			Graphs.addEdge(grafo, c.getA1(), c.getA2(), c.getPeso());
	}
	
	public Graph<Artist, DefaultWeightedEdge> getGrafo(){
		return grafo;
	}
	
	public List<Coppia> getCoppieArchi(){
		Collections.sort(listaCoppieArchi);
		return listaCoppieArchi;
	}
	
	public List<Artist> percorso(Artist source){
		if(grafo.edgesOf(source).size()==0)
			return null;
		else {
			bestPercorso = null;
			List<Artist> parziale = new ArrayList<>();
			parziale.add(source);
			for(DefaultWeightedEdge e : grafo.edgesOf(source)) {
				parziale.add(Graphs.getOppositeVertex(grafo, e, source));
				cerca(parziale, 1, (int) grafo.getEdgeWeight(e));
				parziale.remove(Graphs.getOppositeVertex(grafo, e, source));
			}
			return bestPercorso;
		}
	}
	
	public Integer getBestPeso() {
		return this.bestNum;
	}

	private void cerca(List<Artist> parziale, int l, int pesoCorrente) {
		List<DefaultWeightedEdge> percorribili = new ArrayList<>();
		for(DefaultWeightedEdge e : grafo.edgesOf(parziale.get(parziale.size()-1)))
			if((int)grafo.getEdgeWeight(e)==pesoCorrente &&
					!parziale.contains(Graphs.getOppositeVertex(grafo, e, parziale.get(parziale.size()-1)))) 
				percorribili.add(e);
		if(percorribili.size()==0) {
			if(bestPercorso==null || parziale.size()>bestPercorso.size()) {
				bestPercorso = new ArrayList<>(parziale);
				bestNum = pesoCorrente;
			}
		return;
		} else {
			for(DefaultWeightedEdge e : percorribili) {
				Artist a = Graphs.getOppositeVertex(grafo, e, parziale.get(parziale.size()-1));
				parziale.add(a);
				cerca(parziale, l+1, pesoCorrente);
				parziale.remove(a);
			}
		}
	}
}
