package mergeedges;

import java.util.Comparator;

public class EdgeComparator implements Comparator<EdgeWithHausdorffDist>{

	@Override
	public int compare(EdgeWithHausdorffDist o1, EdgeWithHausdorffDist o2) {
		return ((o1.getHausdorffDistance().compareTo(o2.getHausdorffDistance())) * -1);
	}
}
