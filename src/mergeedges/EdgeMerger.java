package mergeedges;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class EdgeMerger {

	private static final String HAUSDORFF_DATA_FILE = "HausdorffDistance.txt";
	private static final BigDecimal HAUSDORFF_SELECTION_THRESHOLD = new BigDecimal(0.0003);

	public static void main(String[] args) {

		EdgeMerger edgeMerger = new EdgeMerger();
		Map<SourceTarget, Map<Long, Edge>> sourceTargetWithEdges = edgeMerger.readAndLoadEdges(HAUSDORFF_DATA_FILE);
		for (Map.Entry<SourceTarget, Map<Long, Edge>> sourceTargetWithEdge : sourceTargetWithEdges.entrySet()) {
			edgeMerger.findSimilarEdge(sourceTargetWithEdge);
			edgeMerger.printUpdateStatement(sourceTargetWithEdge);
		}
	}

	private void printUpdateStatement(Entry<SourceTarget, Map<Long, Edge>> sourceTargetWithEdge) {
		Map<Long, Edge> edgesMap = sourceTargetWithEdge.getValue();
		for (Edge edge : edgesMap.values()) {
			if (edge.getSimilarEdgeId() != -1) {
				System.out.println("Update activity_linestrings_edge_table_noded set similar_to_edge = "
						+ edge.getSimilarEdgeId() + " where id =" + edge.getId()+";");
				
				//System.out.println("Select id,source,target,cost_length from activity_linestrings_edge_table_noded where id in("+edge.getId()+","+edge.getSimilarEdgeId()+");");
			}
		}

	}

	private void findSimilarEdge(Entry<SourceTarget, Map<Long, Edge>> sourceTargetWithEdge) {
		Map<Long, Edge> edgesMap = sourceTargetWithEdge.getValue();

		Map<Long, Long> comparingEdgesScoreMap = new HashMap<Long, Long>();
		for (Edge edge : edgesMap.values()) {
			List<EdgeWithHausdorffDist> edgesWithHausdorffDist = edge.getEdgesWithHausdorffDistance();
			if (!edgesWithHausdorffDist.isEmpty()) {
				for (EdgeWithHausdorffDist edgeWithHausdorffDist : edgesWithHausdorffDist) {
					long score = edgesWithHausdorffDist.indexOf(edgeWithHausdorffDist) + 1;
					if (comparingEdgesScoreMap.containsKey(edgeWithHausdorffDist.getId())) {
						score += comparingEdgesScoreMap.get(edgeWithHausdorffDist.getId());
					}
					comparingEdgesScoreMap.put(edgeWithHausdorffDist.getId(), score);
				}
			}
		}

		MapValueComparator bvc = new MapValueComparator(comparingEdgesScoreMap);
		TreeMap<Long, Long> sortedMap = new TreeMap<Long, Long>(bvc);
		sortedMap.putAll(comparingEdgesScoreMap);

		long comparingEdgeId = sortedMap.firstKey();
		for (Edge edge : edgesMap.values()) {
			BigDecimal comparingEdgeHausdorffDist = edge.getHausdorffDistanceFromEdge(comparingEdgeId);
			if (comparingEdgeHausdorffDist != null && comparingEdgeHausdorffDist.doubleValue() > 0
					&& comparingEdgeHausdorffDist.compareTo(HAUSDORFF_SELECTION_THRESHOLD) == -1) {
				edge.setSimilarEdgeId(comparingEdgeId);
			}
		}

	}

	// 100819 100666 1 2 0.000299999999995748
	// edge comparingedge src target hausdorff-dist
	public Map<SourceTarget, Map<Long, Edge>> readAndLoadEdges(String fileName) {

		Map<SourceTarget, Map<Long, Edge>> sourceTargetWithEdgeMap = new HashMap<SourceTarget, Map<Long, Edge>>();
		FileInputStream fstream = null;

		try {
			fstream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		try {
			while ((strLine = br.readLine()) != null) {
				strLine = strLine.trim();
				String data[] = strLine.split(" ");
				if (Long.parseLong(data[0]) != Long.parseLong(data[1])) {
					SourceTarget sourceTarget = new SourceTarget(Long.parseLong(data[2]), Long.parseLong(data[3]));

					// From the main hashmap get the edgemap values if present
					Map<Long, Edge> edgeMap = new HashMap<Long, Edge>();
					if (sourceTargetWithEdgeMap.containsKey(sourceTarget)) {
						edgeMap = sourceTargetWithEdgeMap.get(sourceTarget);
					}

					// From the edgemap get the corresponding edge if present
					long primaryEdgeId = Long.parseLong(data[0]);
					Edge edge = new Edge(primaryEdgeId);
					if (edgeMap.containsKey(primaryEdgeId)) {
						edge = edgeMap.get(primaryEdgeId);
					}

					// Load the comparingEdge with hausdorffDist in the edge
					// object
					BigDecimal hausdorffDist = new BigDecimal(data[4]);
					EdgeWithHausdorffDist edgeWithHausdorffDist = new EdgeWithHausdorffDist(Long.parseLong(data[1]),
							hausdorffDist);
					edge.addEdgeWithHausdorffDistance(edgeWithHausdorffDist);
					edgeMap.put(primaryEdgeId, edge);

					sourceTargetWithEdgeMap.put(sourceTarget, edgeMap);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sourceTargetWithEdgeMap;
	}

}
