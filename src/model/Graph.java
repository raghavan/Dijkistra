package model;

import java.util.ArrayList;
import java.util.List;

public class Graph {

	List<Vertex> vertices = new ArrayList<Vertex>();

	public void addVertex(Vertex vertex) {
		vertices.add(vertex);
	}
	
	public Vertex getVertex(String id){
		for(Vertex vertex : vertices){
			if(vertex.getId().equalsIgnoreCase(id)){
				return vertex;
			}
		}
		return null;
	}
		
	public List<Vertex> getAllVertices(){
		return vertices;
	}
	
}
