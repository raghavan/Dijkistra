package modifieddijkistra;

import java.util.Comparator;

import model.Vertex;

public class VertexComparator implements Comparator<Vertex> {

	@Override
	public int compare(Vertex v1, Vertex v2) {
		if (v1 == null || v2 == null) {
			return 0;
		}	
		if (v1.getCostFromSource() == v2.getCostFromSource()) {
			return 0;
		}
		if (v1.getCostFromSource() < v2.getCostFromSource()) {
			return -1;
		}

		return 1;
	}

}
