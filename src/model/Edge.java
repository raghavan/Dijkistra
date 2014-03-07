package model;

public class Edge {

	double cost;
	Vertex targetVertex;
	long id;

	public Edge() {

	}

	public Edge(long id, Vertex vertex, double cost) {
		this.cost = cost;
		this.targetVertex = vertex;
		this.id = id;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public Vertex getVertex() {
		return targetVertex;
	}

	public void setVertex(Vertex vertex) {
		this.targetVertex = vertex;
	}
	
	public long getId(){
		return id;
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
