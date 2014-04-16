package mergeedges;

public class SourceTarget {

	private long sourceId;
	private long targetId;
	
	public SourceTarget(long sourceId, long targetId) {
		super();
		this.sourceId = sourceId;
		this.targetId = targetId;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public long getTargetId() {
		return targetId;
	}

	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (sourceId ^ (sourceId >>> 32));
		result = prime * result + (int) (targetId ^ (targetId >>> 32));
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
		SourceTarget other = (SourceTarget) obj;
		if (sourceId != other.sourceId)
			return false;
		if (targetId != other.targetId)
			return false;
		return true;
	}
	
}
