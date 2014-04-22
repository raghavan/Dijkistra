package visualize;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.postgis.Point;

import model.Edge;
import model.Graph;
import model.Vertex;
import datareader.DataReader;
import datareader.PostgisReader;
import util.Util;

/**
 * Creates a .ps file to use postscript file to visualize the graph
 * @author kaiser
 *
 */
public class PostscriptCreator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		PostscriptCreator psc = new PostscriptCreator("alledges_01.ps");
		
		psc.scale = 1000;
		psc.writePreamble("-88000 41600 -87500 42100");

		Graph g = psc.pgdr.loadAllEdgesIntoGraph();
		psc.plotGraph(g, false);
		
	}

	
	String fileName="output.ps";
	PostgisReader pgdr;
	int scale = 1;
	
	public PostscriptCreator(String outputfile)
	{
		this.fileName = outputfile;
		pgdr = new PostgisReader();
	}
	
	private void writePreamble(String boudingBox) {
		Util.appendDataToFile(fileName, 
				"%!PS-Adobe-3.0 EPSF-3.0 \n"
				+ (boudingBox.length() > 0 ? "%%BoundingBox: " + boudingBox + "\n\n" : "\n")
				+ scale + " " + scale + " scale		% use scaled up bounding box, then scale points here \n"
				+ (0.1 / scale) + " setlinewidth	% inverse scaling factor to keep linewidth to 1 \n\n"

				+ "/DrawLine { \n"
				+ "		1 sub	% decrement the number of point \n"
				+ "		/n exch def	% pop the number of points \n"
				+ "		moveto \n"
				+ "		1 1 n { \n"
				+ "			pop 	% for loop's counter \n"
				+ "			lineto \n"
				+ "		} for \n"
				+ "		stroke \n"
				+ "} def"
				);
	}
	
	public void createFileWithAllEdges()
	{
		writePreamble("-880000 416000 -875000 421000");
		
		// load the graph 
		Graph g = pgdr.loadAllEdgesIntoGraph();  
		
		// count loops 
		int nLoops = 0;
		for (Vertex v: g.getAllVertices())
		{
			for (Edge e: v.getEgdes())
			{
				if (e.getSourceVertex() == e.getTargetVertex())
					nLoops++;
			}
		}
		
		// colors are at least one 1.0 and at most two 1.0's
		colorStep = (float) ( 1.0 
					/ Math.ceil((nLoops + 6) / 6.0) // +6 is for ease of skipping duplicate. +3 would do.
				);
		
		System.out.println("DEBUG: #vertex " + g.getAllVertices().size()
				+ ", #loops " + nLoops + ", colorStep " + colorStep);
		
		boolean grayMode = false;
		// now the main job
		for (Vertex v: g.getAllVertices())
		{
			for (Edge e: v.getEgdes())
			{
				if (v != e.getSourceVertex()) // printed by source vertex
					continue;
				
				// print the color command
				if (e.getSourceVertex() == e.getTargetVertex()) // self-loop
				{
					//continue;
					grayMode = false;
					Util.appendDataToFile(fileName, getNextRGB() + " setrgbcolor");
				} 
				else if (!grayMode) // in graymode, no need to set everytime.
				{
					Util.appendDataToFile(fileName, "0.5 setgray");
					grayMode = true;
				}
				
				
				// print line command
				
				String cmd = "";
				for (Point p: e.getPoints())
				{
					cmd += p.getX() + " " + p.getY() + " ";
				}
				
				// format is: x1 y1 x2 y2 ... xn yn n DrawLine
				cmd += e.getPoints().size() + " DrawLine";
				
				Util.appendDataToFile(fileName, cmd);
//				}
			}
		}
	}
	
	
	public void plotPathWithOldId(int oldId) {
				
		// for edge 3565
		int scale = 1000;
		writePreamble("-74500 40600 -73800 41000");
		
		// load the graph 
		Graph g = new Graph();
		pgdr.loadEdgeRecordsIntoGraph(g, 
				"SELECT id, old_id, sub_id, source, target, the_geom, cost_length "
				+ " FROM activity_linestrings_edge_table_noded " 
				+ " WHERE old_id = " + oldId
				+ " order by sub_id"
				);
		
		plotGraph(g, true);
	}
		
	/**
	 * creates a post script file with vertices and edges connecting the vertices
	 * @param g
	 * @param withTrace, if true the gps traces of the edges will be plotted
	 */
	public void plotGraph(Graph g, boolean withTrace) {
		
		System.out.println("DEBUG: #vertex " + g.getAllVertices().size());
		
		// store vertex locations for edge end points
		HashMap<Vertex, Point> vertexLoc = new HashMap<Vertex, Point>();
		int edgeCount = 0;
		
		Util.appendDataToFile(fileName, "0.09 setgray"); // black color vertex
		
		for (Vertex v: g.getAllVertices())
		{
			List<Point> points = pgdr.getResultForQueryFromPoint("SELECT the_geom " +
					" FROM activity_linestrings_edge_table_noded_vertices_pgr" +
					" WHERE id = " + v.getId());
			
			if (points.size() != 1) {
				System.err.println("#points for vertex " + v.getId() + " = " + points.size());
			}
			
			// draw the vertex and save the locations to draw the edges
			Util.appendDataToFile(fileName, points.get(0).getX() + " " 
					+ points.get(0).getY() + " "
					+ (0.4/scale) + " " // 0.4 unit radius
					+ "0 360 arc stroke");
			
			// store for edges' end-points
			vertexLoc.put(v, points.get(0));
			
			edgeCount++;
		}
		
		edgeCount /= 2; // counted twice for bidirectional storing
		colorStep = 1.0 / Math.ceil((edgeCount + 3) / 6.0);
//		// skip red
//		rgb[0] = 0;
//		rgb[1] = 1;
//		fullIdx = 1;
//		incIdx = 2;
			
		// draw the edges
		for (Vertex v: g.getAllVertices()) 
		{
			for (Edge e: v.getEgdes())
			{
				if (v != e.getSourceVertex()) // printed by source vertex
					continue;

				// print the color command
				
				if (e.getSourceVertex() == e.getTargetVertex()) // self-loop
				{
					//Util.appendDataToFile(fileName, "1.0 0 0 setrgbcolor");
				} 
				else 
				{
					Util.appendDataToFile(fileName, getNextRGB() + " setrgbcolor");
				}

				// original edge
				
				if (withTrace)
				{
					// print line command
					String cmd = "";
					for (Point p: e.getPoints())
					{
						cmd += p.getX() + " " + p.getY() + " ";
					}
					// format is: x1 y1 x2 y2 ... xn yn n DrawLine
					cmd += e.getPoints().size() + " DrawLine";
					Util.appendDataToFile(fileName, cmd);
				}
				
				// node connecting edge of the graph
				
				if (e.getSourceVertex() == e.getTargetVertex())
					continue;
				
				Util.appendDataToFile(fileName, "[" +0.4/scale+ "] 0 setdash"); // dotted line
				Util.appendDataToFile(fileName, 
							vertexLoc.get(v).getX()
							+ " " + vertexLoc.get(v).getY()
							+ " " + vertexLoc.get(e.getTargetVertex()).getX()
							+ " " + vertexLoc.get(e.getTargetVertex()).getY()
							+ " 2 DrawLine"
						);
				Util.appendDataToFile(fileName, "[] 0 setdash"); // solid line
			}
		}
		
	}
	
	/// RGB Color tracker 
	// colors are at least one 1.0 and at most two 1.0's
	double colorStep = 1;
	double rgb[] = {1, 0, 0};
	int fullIdx = 0; 		// allways 1.0
	int incIdx = 1;			// increases by colorstep up to 1.0
	int decIdx = -1;		// decreases to 0.0 from 1.0
	private String getNextRGB()
	{
		String color = "0.0 0.0 0.0";	// default black
		
		color = Arrays.toString(rgb);
		color = color.replaceAll(",", "").replaceAll("\\[", "").replaceAll("\\]", "");
		
		if (incIdx != -1) {
			rgb[incIdx] += colorStep;
			if (rgb[incIdx] >= 1.0) // two colors became full. time to switch
			{
				decIdx = fullIdx;
				fullIdx = incIdx;
				incIdx = -1;
				
				rgb[fullIdx] = 1; // just in case, if overflows
				rgb[decIdx] -= colorStep;
			}
		} else if (decIdx != -1) {
			rgb[decIdx] -= colorStep;
			if (rgb[decIdx] <= 0.0)
			{
				rgb[decIdx] = 0;
				
				incIdx = (decIdx+2) % 3;
				decIdx = -1;
			}
		}
		
		return color;
	}
}
