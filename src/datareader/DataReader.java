package datareader;

import java.util.List;

import model.Graph;
import model.NodeEdgeCost;

import org.postgis.Point;

public interface DataReader {

	public Graph readDataAndLoadGraph();

	public List<Point> getResultForQueryFromLineString(String query);
	public List<Point> getResultForQueryFromPoint(String query);

	public String findNearestVertexIdFromGPS(String longitude, String latitude);

	public NodeEdgeCost getNodeEdgeCostForQuery(String query);
}
