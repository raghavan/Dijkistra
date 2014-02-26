package model;

import java.util.HashSet;
import java.util.Set;

public class Vertex {

	public Vertex() {

	}

	public Vertex(long recordId,String id) {
		super();
		this.recordId = recordId;
		this.id = id;
	}

	long recordId;
	String id;
	Set<Edge> edges = new HashSet<Edge>();
	double costFromSource = 0.0;
	Vertex parentVertexToSource;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		id = id.toLowerCase();
		this.id = id;
	}

	public Set<Edge> getEgdes() {
		return edges;
	}

	public void setEgdes(Set<Edge> edges) {
		this.edges = edges;
	}

	public void addEgde(Edge edge) {
		this.edges.add(edge);
	}

	public double getCostFromSource() {
		return costFromSource;
	}

	public void setCostFromSource(double costFromSource) {
		this.costFromSource = costFromSource;
	}

	public Vertex getParentVertexToSource() {
		return parentVertexToSource;
	}

	public void setParentVertexToSource(Vertex parentVertexToSource) {
		this.parentVertexToSource = parentVertexToSource;
	}
	
	
	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Vertex other = (Vertex) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equalsIgnoreCase(other.id))
			return false;
		return true;
	}

}
