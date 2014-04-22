package datareader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Edge;
import model.Graph;
import model.NodeEdgeCost;
import model.Vertex;

import org.postgis.LineString;
import org.postgis.PGgeometry;
import org.postgis.Point;

public class PostgisReader implements DataReader {

	@Override
	public Graph readDataAndLoadGraph() {
		return postGisDbReader();
	}

	public static void main(String args[]) {
		PostgisReader postgisReader = new PostgisReader();
		postgisReader.postGisDbReader();
	}

	public Graph postGisDbReader() {
		Graph graph = new Graph();

		String query = "select id,source,target,cost_length_meters,edge_score from activity_linestrings_edge_table_noded where source != target;";
		try {
			getResultForQueryAndLoadValuesInGraph(graph, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return graph;
	}

	
	/**
	 * Loads the complete activity_linestrings_edge_table_noded
	 * @return
	 */
	public Graph loadAllEdgesIntoGraph() {
		Graph graph = new Graph();

		String query = "select id, old_id, sub_id, source, target, the_geom, cost_length_meters, edge_score" 
				+ " from activity_linestrings_edge_table_noded" 
				//+ " WHERE similar_to_edge = 0;"
				;
		
		loadEdgeRecordsIntoGraph(graph, query);
		
		return graph;
	}
	
	
	
	/**
	 * loads all columns of edge table into the supplied graph
	 * @param graph
	 * @param query to get the edges
	 */
	public void loadEdgeRecordsIntoGraph(Graph graph, String query) {
		try {
			Statement stmt = PostGisDBConnect.getConnection().createStatement();
			ResultSet r = stmt.executeQuery(query);
			while (r.next()) {
				
				long id = (long) r.getLong(1);
				int oldId = (int) r.getInt(2);
				int subId = (int) r.getInt(3);
				long source = (long) r.getLong(4);
				long target = (long) r.getLong(5);
				LineString lineString = (LineString) ((PGgeometry) r.getObject(6)).getGeometry();
				double cost = (double) r.getDouble(7);
				double edge_score = (double) r.getDouble(8);
				
				loadValuesIntoGraph(graph, id, String.valueOf(source), String.valueOf(target), cost,
						oldId, subId, lineString, edge_score);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public String findNearestVertexIdFromGPS(String longitude, String latitude) {

		String vertexId = null;
		try {
			Statement stmt = PostGisDBConnect.getConnection().createStatement();
			String query = "SELECT id FROM activity_linestrings_edge_table_noded_vertices_pgr ORDER BY the_geom <-> ST_GeometryFromText('POINT("
					+ longitude + " " + latitude + ")',4326)  LIMIT 1";
			ResultSet r = stmt.executeQuery(query);
			if (r != null) {
				while (r.next()) {
					vertexId = String.valueOf(r.getObject(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(vertexId);
		return vertexId;
	}

	public List<Point> getResultForQueryFromLineString(String query) {
		List<Point> points = new ArrayList<Point>();
		try {
			Statement stmt = PostGisDBConnect.getConnection().createStatement();
			ResultSet r = stmt.executeQuery(query);
			if (r != null) {
				while (r.next()) {
					PGgeometry geom = (PGgeometry) r.getObject(1);
					LineString lineString = (LineString) geom.getGeometry();
					for (Point point : lineString.getPoints()) {
						points.add(point);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return points;
	}

	public List<Point> getResultForQueryFromPoint(String query) {
		List<Point> points = new ArrayList<Point>();
		Point point = null;
		try {
			Statement stmt = PostGisDBConnect.getConnection().createStatement();
			ResultSet r = stmt.executeQuery(query);
			if (r != null) {
				while (r.next()) {
					PGgeometry geom = (PGgeometry) r.getObject(1);
					point = (Point) geom.getGeometry();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		points.add(point);

		return points;
	}

	public NodeEdgeCost getNodeEdgeCostForQuery(String query) {
		NodeEdgeCost nodeEdgeCost = new NodeEdgeCost();
		try {
			Statement stmt = PostGisDBConnect.getConnection().createStatement();
			ResultSet r = stmt.executeQuery(query);
			if (r != null) {
				while (r.next()) {
					PGgeometry geom = (PGgeometry) r.getObject(2);
					nodeEdgeCost.setSource(r.getLong(1));
					nodeEdgeCost.setLineString((LineString) geom.getGeometry());
					nodeEdgeCost.setTarget(r.getLong(3));

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nodeEdgeCost;
	}

	private void getResultForQueryAndLoadValuesInGraph(Graph graph, String query) throws SQLException {
		Statement stmt = PostGisDBConnect.getConnection().createStatement();
		ResultSet r = stmt.executeQuery(query);
		while (r.next()) {
			long id = (long) r.getLong(1);
			long source = (long) r.getLong(2);
			long target = (long) r.getLong(3);
			double cost = (double) r.getDouble(4);
			double edge_score = (double) r.getDouble(5);
			loadValuesIntoGraph(graph, id, String.valueOf(source), String.valueOf(target), cost,edge_score);
		}
	}

//	private void loadValuesIntoGraph(Graph graph, long edgeId, String sourceId, String targetId, double cost, double edge_score) {
//		Vertex source = graph.getVertex(sourceId);
//		Vertex target = graph.getVertex(targetId);
//		if (source == null) {
//			source = new Vertex(sourceId);
//			graph.addVertex(source);
//		}
//		if (target == null) {
//			target = new Vertex(targetId);
//			graph.addVertex(target);
//		}
//		Edge edge = new Edge(edgeId, target, cost, edge_score);
//		source.addEgde(edge);
//
//		// bidirectional / undirected graph
//		edge = new Edge(edgeId, source, cost, edge_score);
//		target.addEgde(edge);
//	}

	private void loadValuesIntoGraph(Graph graph, long edgeId, String sourceId, String targetId, double cost, double edge_score) {
		loadValuesIntoGraph(graph, edgeId, sourceId, targetId, cost, -1, -1, null, edge_score);
	}
	
	private void loadValuesIntoGraph(Graph graph, long edgeId, String sourceId, String targetId, double cost,
			int oldId, int subId, LineString lineString, double edge_score) {
		Vertex source = graph.getVertex(sourceId);
		Vertex target = graph.getVertex(targetId);
		if (source == null) {
			source = new Vertex(sourceId);
			graph.addVertex(source);
		}
		if (target == null) {
			target = new Vertex(targetId);
			graph.addVertex(target);
		}
		Edge edge = new Edge(edgeId, source, target, cost, edge_score);
		source.addEgde(edge);
		if (source != target) // loops 
			target.addEgde(edge);
		
		edge.setOldId(oldId);
		edge.setSubId(subId);
		edge.setLineString(lineString);
	}
	
	
	public void printVertexPoints(List<Vertex> vertices) {
		for (Vertex vertex : vertices) {
			String query = "select the_geom from activity_linestrings_edge_table_noded_vertices_pgr where id = "
					+ vertex.getId();
			List<Point> points = getResultForQueryFromPoint(query);
			for (Point point: points )
				System.out.println("points.push(\"" + point.getY() + "," +
						point.getX() + "\");");
			if (points.size() > 1)
				System.err.println("INFO: multiple gps for vertex-id: " + vertex.getId());
		}
	}

}
