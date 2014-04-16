package mergeedges;

import java.math.BigDecimal;

public class EdgeWithHausdorffDist {

	private long id;
	private BigDecimal hausdorffDistance;

	public EdgeWithHausdorffDist(long id, BigDecimal hausdorffDistance) {
		this.id = id;
		this.hausdorffDistance = hausdorffDistance;
	}

	public EdgeWithHausdorffDist(long edgeId) {
		this.id = edgeId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getHausdorffDistance() {
		return hausdorffDistance;
	}

	public void setHausdorffDistance(BigDecimal hausdorffDistance) {
		this.hausdorffDistance = hausdorffDistance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EdgeWithHausdorffDist other = (EdgeWithHausdorffDist) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
