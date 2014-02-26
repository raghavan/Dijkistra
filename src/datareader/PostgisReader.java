package datareader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Edge;
import model.Graph;
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

		String query = "select source,target,cost_length from activity_linestrings_edge_table_noded where source != target;";
		try {
			getResultForQueryAndLoadValuesInGraph(graph, query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return graph;
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

	public List<Point> getResultForQuery(String query) {
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

	private void getResultForQueryAndLoadValuesInGraph(Graph graph, String query) throws SQLException {
		Statement stmt = PostGisDBConnect.getConnection().createStatement();
		ResultSet r = stmt.executeQuery(query);
		while (r.next()) {
			long source = (long) r.getObject(1);
			long target = (long) r.getObject(2);
			double cost = (double) r.getObject(3);
			loadValuesIntoGraph(graph, String.valueOf(source), String.valueOf(target), cost);
		}
	}

	private void loadValuesIntoGraph(Graph graph, String sourceId, String targetId, double cost) {
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
		Edge edge = new Edge(target, cost);
		source.addEgde(edge);
	}
}
