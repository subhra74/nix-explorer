package nixexplorer.core;

public interface FileSystem {
	public String getRoot();

	public FileSystemProvider get(String file);
}
