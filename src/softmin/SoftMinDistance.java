package softmin;

import model.Edge;
import model.Graph;
import model.Vertex;
import datareader.DataReader;
import datareader.PostgisReader;

public class SoftMinDistance {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 
		DataReader dr = new PostgisReader();
		Graph g = dr.readDataAndLoadGraph();
		
		String sourceId = dr.findNearestVertexIdFromGPS("-87.65658170928955", "41.8676646570851");
		String targetId = dr.findNearestVertexIdFromGPS("-87.60971815338135", "41.86187989679139");
		
		
		// sort the vertex list to perform softmin in order
		VertexSorter vSorter = new VertexSorter(g, sourceId, targetId);
		Vertex[] vertexList = vSorter.getSortedVertices();
		
		
		// normalize to edge costs so that softmin does not go to -infinity
		double minEdgeCost = Double.MAX_VALUE;
		for (Vertex v: vertexList)
		{
			for (Edge e: v.getEgdes())
			{
				if (minEdgeCost > e.getCost())
					minEdgeCost = e.getCost();
			}
		}
		
		for (Vertex v: vertexList)
		{
			for (Edge e: v.getEgdes())
			{
				e.setCost(e.getCost() / minEdgeCost);
			}
		}
		
		
		// compute softmin
		SoftMinDistance smd = new SoftMinDistance();
		smd.computeSoftmin(g, vertexList, sourceId, targetId);
		
		
		System.out.println("---");
		for (int i=0; i<vertexList.length; i++)
			System.out.println(vertexList[i].getId() + " " + vertexList[i].getCostFromSource());
		
		System.out.println(vertexList.length + " \n" + (1/minEdgeCost)
				+ "\n" + vSorter.getDijkstrasDistanceFromSouce(targetId)
				+ "\n" + vSorter.getDijkstrasDistanceFromSouce(sourceId)
				);
		
		
	}
	
	public void computeSoftmin(Graph g, Vertex[] vertices, String sourceId, String targetId)
	{
		
		// reset vertex weights
		for (Vertex v: vertices)
			if (v.getId().equals(sourceId))
				v.setCostFromSource(0);
			else v.setCostFromSource(Double.MAX_VALUE);

		int w = 100;
		while (w-- > 0)
		for (Vertex v: vertices)
		{
			if (v.getId().equals(sourceId))
				continue;
			// edges are outgoing, 
			// but since they are duplicated for undirected graph, 
			// we can use this
			for (Edge e: v.getEgdes()) 
			{
				if (e.getCost() < 0 || e.getVertex().getCostFromSource() < 0)
					System.out.println(v.getId() + " : " + e.getId() + " - " + e.getCost() + " : " + e.getVertex().getId() + " - " + e.getVertex().getCostFromSource() );

				v.setCostFromSource(
						softmin(
								v.getCostFromSource(), 
								e.getCost() + e.getVertex().getCostFromSource()
								)
						);
			}
		}
	}
	
	/**
	 * Computes the softmin value of the two arguments
	 * @param x
	 * @param y
	 * @return softmin of x and y
	 */
	public double softmin(double x, double y)
	{
		if (x >= Double.MAX_VALUE) return y;
		if (y >= Double.MAX_VALUE || x == y) return x;
		
		// softmin(x,y) = min(x,y) - log (1 + exp(min - max) )
		if (x < y)
			return x - Math.log(1.0 + Math.exp(x - y));
		else return 
				y - Math.log(1.0 + Math.exp(y - x));
	}

}
