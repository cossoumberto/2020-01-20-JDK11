package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.ArtObject;
import it.polito.tdp.artsmia.model.Artist;
import it.polito.tdp.artsmia.model.Coppia;
import it.polito.tdp.artsmia.model.Exhibition;

public class ArtsmiaDAO {

	public List<ArtObject> listObjects() {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Exhibition> listExhibitions() {
		
		String sql = "SELECT * from exhibitions";
		List<Exhibition> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Exhibition exObj = new Exhibition(res.getInt("exhibition_id"), res.getString("exhibition_department"), res.getString("exhibition_title"), 
						res.getInt("begin"), res.getInt("end"));
				
				result.add(exObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Artist> getArtistsRole (String role, Map<Integer, Artist> idMapArtists) {
		String sql = "SELECT * FROM artists WHERE artist_id IN (SELECT DISTINCT artist_id FROM authorship WHERE role = ?)";
		List<Artist> list = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, role);
			ResultSet res = st.executeQuery();
			while(res.next()) {
				if(idMapArtists.containsKey(res.getInt("artist_id")))
					list.add(idMapArtists.get(res.getInt("artist_id")));
				else {
					Artist a = new Artist(res.getInt("artist_id"), res.getString("name"));
					idMapArtists.put(a.getId(), a);
					list.add(a);
				}
			}
			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getRoles() {
		String sql = "SELECT DISTINCT role FROM authorship";
		List<String> list = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while(res.next()) {
				list.add(res.getString("role"));
			}
			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Coppia> getCoppieArtist(String role, Map<Integer, Artist> idMapArtists){
		String sql = "SELECT A1.artist_id AS ART1 , A2.artist_id AS ART2, COUNT(*) AS C " +
					"FROM authorship AS A1, authorship AS A2, exhibition_objects AS E1, exhibition_objects AS E2 " +
					"WHERE E1.exhibition_id=E2.exhibition_id AND E1.object_id>E2.object_id AND E1.object_id=A1.object_id " +
					"AND E2.object_id=A2.object_id AND A1.artist_id>A2.artist_id AND A1.role=? AND A2.role=? " +
					"GROUP BY A1.artist_id, A2.artist_id";
		List<Coppia> list = new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, role);
			st.setString(2, role);
			ResultSet res = st.executeQuery();
			while(res.next()) {
				Coppia c = new Coppia(idMapArtists.get(res.getInt("ART1")), idMapArtists.get(res.getInt("ART2")), res.getInt("C"));
				list.add(c);
			}
			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
