package model;

import java.util.ArrayList;
import java.util.List;

import org.postgis.LineString;
import org.postgis.Point;

public class NodeEdgeCost {

	private long source;
	private long target;
	private double cost;
	private LineString lineString;

	public NodeEdgeCost(long source, long target, LineString lineString, double cost) {
		super();
		this.source = source;
		this.target = target;
		this.lineString = lineString;
		this.cost = cost;
	}

	public NodeEdgeCost() {
	}

	public long getSource() {
		return source;
	}

	public void setSource(long source) {
		this.source = source;
	}

	public long getTarget() {
		return target;
	}

	public void setTarget(long target) {
		this.target = target;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public LineString getLineString() {
		return lineString;
	}

	public void setLineString(LineString lineString) {
		this.lineString = lineString;
	}

	public List<Point> getPoints() {
		List<Point> points = new ArrayList<Point>();
		for (Point point : lineString.getPoints()) {
			points.add(point);
		}
		return points;
	}

}
