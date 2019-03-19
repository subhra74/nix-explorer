package nixexplorer.widgets.folderview;

public class FavouritePlaceEntry {
	private String fullPath, name;

	public FavouritePlaceEntry(String fullPath, String name) {
		super();
		this.fullPath = fullPath;
		this.name = name;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
