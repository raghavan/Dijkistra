package dijkistra;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import model.Edge;
import model.Graph;
import model.Vertex;

import org.postgis.Point;

import datareader.DataReader;
import datareader.PostgisReader;

public class Dijkistra {

	static DataReader dataReader = new PostgisReader();
	static Graph graph = dataReader.readDataAndLoadGraph();

	public static void main(String args[]) {

		Dijkistra dijkistra = new Dijkistra();
		String sourceId = dataReader.findNearestVertexIdFromGPS("-87.62379438629151","41.86817599193286");
		String targetId = dataReader.findNearestVertexIdFromGPS("-87.61808664550782","41.8825875604996");
		// sourceId = "7";
		// targetId = "595";
		List<Vertex> shortestPath = dijkistra.findShortestPath(sourceId, targetId);
		for (Vertex vertex : shortestPath) {
//			String query = "select the_geom from activity_linestrings_edge_table_noded where id = "
//					+ vertex.getRecordId();
			String query = "select the_geom from activity_linestrings_edge_table_noded_vertices_pgr where id = "
					+ vertex.getId();
			
			List<Point> points = dataReader.getResultForQueryFromPoint(query);
			dijkistra.printPathForBingMap(points);
		}
	}

	private void printPathForBingMap(List<Point> points) {
		for (Point point : points) {
			 System.out.println("points.push(\"" + point.getY() + "," +
			 point.getX() + "\");");
		}
	}

	private List<Vertex> findShortestPath(String sourceId, String targetId) {

		List<Vertex> visitedVertices = new ArrayList<Vertex>();
		Vertex source = graph.getVertex(sourceId);
		Vertex target = graph.getVertex(targetId);

		Comparator<Vertex> vertexComparator = new VertexComparator();
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>(1, vertexComparator);
		vertexQueue.add(source);

		Vertex currentNode = vertexQueue.poll();
		while (currentNode != null && currentNode != target) {
			for (Edge edge : currentNode.getEgdes()) {
				Vertex child = edge.getVertex();
				double edgeCost = edge.getCost();
				if (!visitedVertices.contains(child)) {
					if (vertexQueue.contains(child)) {
						if (child.getCostFromSource() > (edgeCost + currentNode.getCostFromSource())) {
							child.setParentVertexToSource(currentNode);
							child.setCostFromSource(edgeCost + currentNode.getCostFromSource());
						}
					} else {
						child.setParentVertexToSource(currentNode);
						child.setCostFromSource(edgeCost);
						vertexQueue.add(child);
					}

				}
			}
			visitedVertices.add(currentNode);
			currentNode = vertexQueue.poll();
		}

		List<Vertex> result = new ArrayList<Vertex>();
		Vertex currentVertex = target;
		while (currentVertex != null) {
			result.add(currentVertex);
			System.out.print(currentVertex.getId() + " -- (" + String.valueOf(currentVertex.getCostFromSource())
					+ ")-->");
			currentVertex = currentVertex.getParentVertexToSource();
		}
		System.out.println("\n");
		return result;
	}
}
