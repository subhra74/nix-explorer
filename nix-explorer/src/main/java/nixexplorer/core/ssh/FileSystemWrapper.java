package nixexplorer.core.ssh;

import java.io.OutputStream;
import java.util.List;

import nixexplorer.core.DataTransferProgress;
import nixexplorer.core.FileInfo;

public interface FileSystemWrapper extends AutoCloseable {
	public void connect() throws Exception;

	public List<FileInfo> list(String path) throws Exception;

	public String getHome() throws Exception;

	public String[] getRoots() throws Exception;

	public boolean isLocal();

	public FileInfo get(String path) throws Exception;

	public void copyTo(String source, String dest, DataTransferProgress prg, int mode) throws Exception;
	
	public void copyTo(String source, OutputStream dest, DataTransferProgress prg, int mode,long offset) throws Exception;
}
