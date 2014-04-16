package mergeedges;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Edge {

	private long id;
	private long similarEdgeId = -1; // -1 means none is similar
	private List<EdgeWithHausdorffDist> edgesWithHausdorffDistance = new ArrayList<EdgeWithHausdorffDist>();

	public Edge(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSimilarEdgeId() {
		return similarEdgeId;
	}

	public void setSimilarEdgeId(long similarEdgeId) {
		this.similarEdgeId = similarEdgeId;
	}

	public List<EdgeWithHausdorffDist> getEdgesWithHausdorffDistance() {
		Collections.sort(edgesWithHausdorffDistance, new EdgeComparator());
		return edgesWithHausdorffDistance;
	}

	public void addEdgeWithHausdorffDistance(EdgeWithHausdorffDist edgeWithHausdorffDistance) {
		this.edgesWithHausdorffDistance.add(edgeWithHausdorffDistance);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		if (id != other.id)
			return false;
		return true;
	}

	public BigDecimal getHausdorffDistanceFromEdge(long edgeId) {
		EdgeWithHausdorffDist edgeWithHausdorffDist = new EdgeWithHausdorffDist(edgeId);
		int index = this.edgesWithHausdorffDistance.indexOf(edgeWithHausdorffDist);
		if (index != -1) {
			return this.edgesWithHausdorffDistance.get(index).getHausdorffDistance();
		}
		return null;
	}

}
