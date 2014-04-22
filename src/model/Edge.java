package model;

import java.util.ArrayList;
import java.util.List;

import org.postgis.LineString;
import org.postgis.Point;

public class Edge {

	double cost;
	Vertex targetVertex;
	long id;
	double edge_score;
	
	Vertex sourceVertex;
	LineString lineString;
	int old_id;
	int sub_id;

	public Edge() {

	}

	public Edge(long id, Vertex source, Vertex target, double cost, double edge_score) {
		this.cost = cost;
		this.sourceVertex = source;
		this.targetVertex = target;
		this.id = id;
		this.edge_score = edge_score;
		
		lineString = null;
		old_id = -1;
		sub_id = -1;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	//	public Vertex getVertex() {
	//	return targetVertex;
	//}

	public Vertex getTargetVertex() {
		return targetVertex;
	}

	public Vertex getSourceVertex() {
		return sourceVertex;
	}

	public void setVertex(Vertex vertex) {
		this.targetVertex = vertex;
	}

	public long getId() {
		return id;
	}
	
	public LineString getLineString() {
		return lineString;
	}

	public void setLineString(LineString lineString) {
		this.lineString = lineString;
	}
	
	public double getEdge_score() {
		return edge_score;
	}

	public void setEdge_score(double edge_score) {
		this.edge_score = edge_score;
	}


	public List<Point> getPoints() {
		List<Point> points = new ArrayList<Point>();
		for (Point point : lineString.getPoints()) {
			points.add(point);
		}
		return points;
	}
	
	public void setOldId(int oldId){
		old_id = oldId;
	}
	
	public void setSubId(int subId){
		sub_id = subId;
	}	
	
	public int getOldId(){
		return old_id;
	}
	
	public int getSubId(){
		return sub_id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((targetVertex == null) ? 0 : targetVertex.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (Double.doubleToLongBits(cost) != Double.doubleToLongBits(other.cost))
			return false;
		if (targetVertex == null) {
			if (other.targetVertex != null)
				return false;
		} else if (!targetVertex.equals(other.targetVertex))
			return false;
		return true;
	}

}
