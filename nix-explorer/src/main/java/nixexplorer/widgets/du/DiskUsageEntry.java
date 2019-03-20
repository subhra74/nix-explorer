package nixexplorer.widgets.du;

public class DiskUsageEntry {
	private String path, name;
	private long size;
	private double usagePercent;

	public DiskUsageEntry(String name, String path, long size,
			double usagePercent) {
		super();
		this.name = name;
		this.path = path;
		this.size = size;
		this.usagePercent = usagePercent;
	}

	public synchronized String getPath() {
		return path;
	}

	public synchronized void setPath(String path) {
		this.path = path;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized long getSize() {
		return size;
	}

	public synchronized void setSize(long size) {
		this.size = size;
	}

	public synchronized double getUsagePercent() {
		return usagePercent;
	}

	public synchronized void setUsagePercent(double usagePercent) {
		this.usagePercent = usagePercent;
	}

	@Override
	public String toString() {
		return "DiskUsageEntry [path=" + path + ", name=" + name + ", size="
				+ size + ", usagePercent=" + usagePercent + "]";
	}
}
