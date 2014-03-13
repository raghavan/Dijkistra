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
		
		
		VertexSorter vSorter = new VertexSorter(g, sourceId, targetId);
		
		Vertex[] vertexList = vSorter.getSortedVertices();
		
		System.out.println("---");
		for (int i=0; i<vertexList.length; i++)
			System.out.println(vertexList[i].getId() + " " + vertexList[i].getCostFromSource());
		
		System.out.println(vertexList.length);
		
		vSorter.setThresholdFactor(70000);
		vSorter.sort();
		vertexList = vSorter.getSortedVertices();
		
//		System.out.println("---");
//		for (int i=0; i<vertexList.length; i++)
//			System.out.println(vertexList[i].getId() + " " + vertexList[i].getCostFromSource());
//		
		System.out.println(vertexList.length);
	}
	
	public void computeSoftmin(Graph g, Vertex[] vertices, String sourceId, String targetId)
	{
		// reset costs
		for (Vertex v: vertices)
		{
			if (v.getId().equals(sourceId))
				v.setCostFromSource(0);
			else v.setCostFromSource(Double.MAX_VALUE);
		}
		
		for (Vertex v: vertices)
		{
			for (Edge e: v.getEgdes())
			{
				
			}
		}
	}

}
