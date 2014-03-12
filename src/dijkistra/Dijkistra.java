package dijkistra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import model.Edge;
import model.Graph;
import model.NodeEdgeCost;
import model.Vertex;

import org.postgis.Point;

import datareader.DataReader;
import datareader.PostgisReader;

public class Dijkistra {

	static DataReader dataReader = new PostgisReader();
	static Graph graph = dataReader.readDataAndLoadGraph();

	public static void main(String args[]) {

		Dijkistra dijkistra = new Dijkistra();
		String sourceId = dataReader.findNearestVertexIdFromGPS("-87.65658170928955", "41.8676646570851");
		String targetId = dataReader.findNearestVertexIdFromGPS("-87.60971815338135", "41.86187989679139");
		// sourceId = "7";
		// targetId = "595";
		
		List<Vertex> shortestPath = dijkistra.findShortestPath(sourceId, targetId);
		
		for (Vertex vertex : shortestPath) {
//			String query = "select the_geom from activity_linestrings_edge_table_noded where id = "
//					+ vertex.getRecordId();
			
			if (vertex.getId().equals(sourceId)) // parent-edge based retrival, source don't have parent
				continue;
			
			String query = "select source,the_geom,target from activity_linestrings_edge_table_noded where id = "
					+ vertex.getParentEdge().getId();
			
			NodeEdgeCost nodeEdgeCost = dataReader.getNodeEdgeCostForQuery(query);
			
			List<Point> points = nodeEdgeCost.getPoints();
			if (nodeEdgeCost.getTarget() != Long.parseLong(vertex.getId())) {
				Collections.reverse(points);
			}

			dijkistra.printPathForBingMap(points);
		}
	}
	

	private void printFromPoints(List<Vertex> shortestPath) {
		for (Vertex vertex : shortestPath) {
			String query = "select the_geom from activity_linestrings_edge_table_noded_vertices_pgr where id = "
					+ vertex.getId();
			List<Point> points = dataReader.getResultForQueryFromPoint(query);
			printPathForBingMap(points);
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
		
		source.setCostFromSource(0);
		vertexQueue.add(source);

		Vertex currentNode = vertexQueue.poll();
		while (currentNode != null && currentNode != target) {
			for (Edge edge : currentNode.getEgdes()) {
				Vertex child = edge.getVertex();
				double edgeCost = edge.getCost();
				if (!visitedVertices.contains(child)) {

					// undiscovered should have infinity cost
					if (child.getCostFromSource() > (edgeCost + currentNode.getCostFromSource())) {
						child.setParentVertexToSource(currentNode);
						child.setParentEdge(edge);
						child.setCostFromSource(edgeCost + currentNode.getCostFromSource());
					}
					
					// old vertex, remove and then add for sorting
					if (vertexQueue.contains(child)) { 
						vertexQueue.remove(child);
					}
					vertexQueue.add(child);
					
				}
			}
			visitedVertices.add(currentNode);
			currentNode = vertexQueue.poll();
		}

		List<Vertex> result = new ArrayList<Vertex>();
		Vertex currentVertex = target;
		while (currentVertex != null) {
			result.add(0, currentVertex); // tracking back, so nodes inserted at the beginning
			System.out.print(currentVertex.getId() + " -- (" + String.valueOf(currentVertex.getCostFromSource())
					+ ")-->");
			currentVertex = currentVertex.getParentVertexToSource();
		}
		System.out.println("\n");
		return result;
	}
}
