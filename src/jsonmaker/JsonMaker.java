package jsonmaker;

import model.Edge;
import model.Graph;
import model.Vertex;
import util.Util;

import com.google.gson.Gson;

import datareader.DataReader;
import datareader.PostgisReader;

public class JsonMaker {



	public static void main(String args[]){
		makeJson();
	}
	
	public static void makeJson() {
		DataReader dataReader = new PostgisReader();
		Graph graph = dataReader.readDataAndLoadGraph();

		Util.deleteFile("map.txt");
		for(Vertex vertex : graph.getAllVertices()){			
			Adjacency adjacency = new Adjacency(vertex.getId(),vertex.getId());
			for(Edge edge : vertex.getEgdes()){
				NodeDetail nodeDetail = new NodeDetail();
				nodeDetail.setNodeFrom(vertex.getId());
				nodeDetail.setNodeTo(edge.getTargetVertex().getId());	
				adjacency.addNodeDetails(nodeDetail);
			}
			Gson gson = new Gson();
			String str = gson.toJson(adjacency);
			Util.appendDataToFile("map.txt", str+",");
		}		
	}


}

/*
"adjacencies": [
{
  "nodeTo": "graphnode1", 
  "nodeFrom": "graphnode0", 
  "data": {}
}, 
{
  "nodeTo": "graphnode3", 
  "nodeFrom": "graphnode0", 
  "data": {}
}, 
{
  "nodeTo": "graphnode2", 
  "nodeFrom": "graphnode0", 
  "data": {}
}, 
], 
"data": {
"$color": "#83548B", 
"$type": "circle"
}, 
"id": "graphnode0", 
"name": "graphnode0"
}
];

*/