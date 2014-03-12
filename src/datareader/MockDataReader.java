package datareader;

import java.util.List;

import model.Edge;
import model.Graph;
import model.NodeEdgeCost;
import model.Vertex;

import org.postgis.Point;

public class MockDataReader implements DataReader {

	public Graph readDataAndLoadGraph() {

		Vertex v1 = loadVertex("V1");
		Vertex v2 = loadVertex("V2");
		Vertex v3 = loadVertex("V3");
		Vertex v4 = loadVertex("V4");
		Vertex v5 = loadVertex("V5");

		Edge edgeFromV1ToV2 = loadEdge(5, v2);
		Edge edgeFromV1ToV4 = loadEdge(2, v4);

		Edge edgeFromV2ToV1 = loadEdge(5, v1);
		Edge edgeFromV2ToV3 = loadEdge(3, v3);
		Edge edgeFromV2ToV5 = loadEdge(80, v5);

		Edge edgeFromV3ToV2 = loadEdge(3, v2);
		Edge edgeFromV3ToV4 = loadEdge(1, v4);
		Edge edgeFromV3ToV5 = loadEdge(6, v5);

		Edge edgeFromV4ToV1 = loadEdge(2, v1);
		Edge edgeFromV4ToV3 = loadEdge(1, v3);
		Edge edgeFromV4ToV5 = loadEdge(2, v5);

		Edge edgeFromV5ToV2 = loadEdge(80, v2);
		Edge edgeFromV5ToV3 = loadEdge(6, v3);
		Edge edgeFromV5ToV4 = loadEdge(2, v4);

		setAllEdges(v1, edgeFromV1ToV2, edgeFromV1ToV4);
		setAllEdges(v2, edgeFromV2ToV1, edgeFromV2ToV3, edgeFromV2ToV5);
		setAllEdges(v3, edgeFromV3ToV2, edgeFromV3ToV4, edgeFromV3ToV5);
		setAllEdges(v4, edgeFromV4ToV1, edgeFromV4ToV3, edgeFromV4ToV5);
		setAllEdges(v5, edgeFromV5ToV2, edgeFromV5ToV3, edgeFromV5ToV4);

		Graph graph = new Graph();
		graph.addVertex(v1);
		graph.addVertex(v2);
		graph.addVertex(v3);
		graph.addVertex(v4);
		graph.addVertex(v5);

		return graph;

	}

	public List<Point> getResultForQueryFromLineString(String query) {
		return null;
	}

	public List<Point> getResultForQueryFromPoint(String query) {
		return null;
	}

	public String findNearestVertexIdFromGPS(String longitude, String latitude) {
		return null;
	}

	public NodeEdgeCost getNodeEdgeCostForQuery(String query) {
		return null;
	}

	private Edge loadEdge(double i, Vertex v) {
		Edge edge = new Edge();
		edge.setCost(i);
		edge.setVertex(v);
		return edge;
	}

	private Vertex loadVertex(String id) {

		Vertex vertex = new Vertex();
		vertex.setId(id);
		return vertex;
	}

	private Vertex setAllEdges(Vertex vertex, Edge... edges) {
		for (int i = 0; i < edges.length; i++) {
			vertex.addEgde(edges[i]);
		}
		return vertex;
	}
}
