package jsonmaker;

import java.util.ArrayList;
import java.util.List;

public class Adjacency {

	List<NodeDetail> adjacencies = new ArrayList<NodeDetail>();
	Data data = new Data();
	String name;
	String id;

	public Adjacency(String id, String name) {
		this.name = name;
		this.id = id;
	}

	public void addNodeDetails(NodeDetail nodeDetail) {
		adjacencies.add(nodeDetail);
	}
}
