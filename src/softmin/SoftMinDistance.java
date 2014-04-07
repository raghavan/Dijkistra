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
		
		// load the graph 
		DataReader dr = new PostgisReader();
		Graph g = dr.readDataAndLoadGraph();
		
		System.out.println("DEBUG: database loaded. #vertices " + g.getAllVertices().size());
		
		// pick source and target
		String sourceId = dr.findNearestVertexIdFromGPS("-87.65658170928955", "41.8676646570851");
		String targetId = dr.findNearestVertexIdFromGPS("-87.60971815338135", "41.86187989679139");
		
		double mc = Double.MAX_VALUE;
		double xc = 0;
		// add slacks to edge costs so that softmin converge
		for (Vertex v: g.getAllVertices())
		{
			for (Edge e: v.getEgdes())
			{
				/**
				 * Learn these constants!!!!
				 */
				e.setCost(0.10 + 10000000.0 * e.getCost());
				if (e.getCost() < mc) mc = e.getCost();
				if (e.getCost() > xc) xc = e.getCost();
			}
		}
		
		System.out.println("DEBUG: edge weight adjusted. new costs: min " 
				+ mc + ", max " + xc);
		
		// sort the vertex list to perform softmin in order
		VertexSorter vSorter = new VertexSorter(g, sourceId, targetId);
		//vSorter.setThresholdFactor(Double.MAX_VALUE); // i.e. no cut off
		vSorter.setThresholdFactor(5); 
		Vertex[] vertexList = vSorter.getSortedVertices();

		System.out.println("DEBUG: vertex list sorted. #vertices: " + vertexList.length);
		
		// compute softmin
		SoftMinDistance smd = new SoftMinDistance();
		smd.computeSoftmin(g, vertexList, sourceId, targetId);
		
		
		System.out.println("---");
		for (int i=0; i<vertexList.length; i++)
			System.out.println(vertexList[i].getId() + " " + vertexList[i].getCostFromSource());
		
		System.out.println("vertex list length: " + vertexList.length + " \n" 
				+ "dijkstra's distance s-to-t: " + vSorter.getDijkstrasDistanceFromSouce(targetId) + "\n"
				+ "minimum edge weight of the graph: " + mc +  "\n"
				+ "maximum edge weight of the graph: " + xc);
		
	}
	
	public void computeSoftmin(Graph g, Vertex[] vertices, String sourceId, String targetId)
	{
		
		// reset vertex weights
		for (Vertex v: vertices)
			if (v.getId().equals(sourceId))
				v.setCostFromSource(0);
			else v.setCostFromSource(Double.MAX_VALUE);

		int w = 0;
		double prevTargetCost = g.getVertex(targetId).getCostFromSource();
		while (w++ < 15)
		{
			System.out.println("DEBUG: iteration " + w);
			
			for (Vertex v: vertices)
			{
				// in each new iteration a vertex excludes itself
				if (v.getId().equals(sourceId))
					v.setCostFromSource(0);
				else 
					v.setCostFromSource(Double.MAX_VALUE);

				// edges are outgoing, 
				// but since they are duplicated for undirected graph, 
				// we can use this
				for (Edge e: v.getEgdes()) 
				{
					v.setCostFromSource(
							softmin(
									v.getCostFromSource(), 
									e.getCost() + e.getVertex().getCostFromSource()
									)
							);
				}
				
			}
			
			// when to break;
			if (Math.abs(
					g.getVertex(targetId).getCostFromSource() - prevTargetCost 
					) < 0.0001 )
					break;
			prevTargetCost =g.getVertex(targetId).getCostFromSource();
		}
		
		System.out.println("number of iterations to converge: " + w);
		
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
