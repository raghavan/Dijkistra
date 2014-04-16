package mergeedges;

import java.util.Comparator;
import java.util.Map;

public class MapValueComparator implements Comparator<Long> {

	Map<Long, Long> base;

	public MapValueComparator(Map<Long, Long> base) {
		this.base = base;
	}

	public int compare(Long a, Long b) {
		if (base.get(a) > base.get(b)) {
			return -1;
		} else if (base.get(a) < base.get(b)) {
			return 1;
		} else {
			return 0;
		}
	}
}
