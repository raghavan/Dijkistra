package softmin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import dijkistra.Dijkistra;
import dijkistra.VertexComparator;

import model.Graph;
import model.Vertex;

public class VertexSorter {
	
	
	private Graph graph;
	private String sourceId;
	private String targetId;
	
	// storage of source/target distance
	HashMap<Vertex, Double> sourceDistance;
	HashMap<Vertex, Double> targetDistance;
	
	// used as the ratio scaling factor for 
	// alpha * dist_s(target) >= dist_s(x) + dist_t(x)
	private double multiplier = 3;
	
	// the sorted List based on that above rule of distance ratio
	List<Vertex> sortedVertices;
	
	/**
	 * 
	 * @param g
	 * @param sourceId
	 * @param targetId
	 */
	public VertexSorter(Graph g, String sourceId, String targetId)
	{
		this.graph = g;
		this.sourceId = sourceId;
		this.targetId = targetId;
		
		sourceDistance = new HashMap<Vertex, Double>();
		targetDistance = new HashMap<Vertex, Double>();
	 
		sortedVertices = new ArrayList<Vertex>();
	}

	public void computeDistance()
	{
		// create the Dijkstra object
		Dijkistra dijkstra = new Dijkistra(graph);
		
		// reset the costs of the graph
		List<Vertex> vertices = graph.getAllVertices();
		for (Vertex v : vertices)
		{
			if (v.getId().equals(sourceId))
				v.setCostFromSource(0);
			else v.setCostFromSource(Double.MAX_VALUE); // infinity
		}
		
		// null should terminate the shortest path algo only when path to all vertices are found
		dijkstra.findShortestPath(sourceId, null); 
		
		// now store the distances
		// and reset for target distance too
		for (Vertex v: vertices)
		{
			// first save
			sourceDistance.put(v, v.getCostFromSource());
			
			// now reset
			if (v.getId().equals(targetId))
				v.setCostFromSource(0);
			else v.setCostFromSource(Double.MAX_VALUE);
		}
	
		// compute shortest paths
		dijkstra.findShortestPath(targetId, null);
		
		// save
		for (Vertex v: vertices)
		{
			// first save
			targetDistance.put(v, v.getCostFromSource());
		}
	}
	
	/**
	 * Sorts the vertices according to 
	 *  dist_s(x) / dist_s(x) + dist_t(x), 
	 * satisfying: factor * dist_s(target) >= dist_s(x) + dist_t(x)
	 */
	public void sort()
	{
		if (targetDistance.size() == 0
				|| sourceDistance.size() == 0)
		{
			computeDistance();
		}
		
		double source_target_distance = 
				sourceDistance.get(
						graph.getVertex(targetId)
						);
		List<Vertex> vertices = graph.getAllVertices();
		
		for (Vertex v: vertices)
		{
			double s_d = sourceDistance.get(v);
			double t_d = targetDistance.get(v);
			
			if (s_d >= Double.MAX_VALUE 
					|| t_d >= Double.MAX_VALUE
					|| multiplier * source_target_distance < (s_d + t_d)
					) // skip this vertices from computing soft-min
			{
				continue;
			}
			
			// exploit the cost of the vertex, it uses the vertex sorter.
			v.setCostFromSource(s_d / (s_d + t_d));
			sortedVertices.add(v);
		}
	}
	
	public Vertex[] getSortedVertices()
	{
		if (sortedVertices.isEmpty())
			sort();
		
		Comparator<Vertex> vertexComparator = new VertexComparator();
		Collections.sort(sortedVertices, vertexComparator);
		
		return sortedVertices.toArray(new Vertex[0]);
	}
	
	/**
	 * used as the ratio scaling factor for alpha * dist_s(x) >= dist_s(x) + dist_t(x)
	 * @param factor
	 */
	public void setThresholdFactor(double factor)
	{
		multiplier = factor;
	}
	
}
