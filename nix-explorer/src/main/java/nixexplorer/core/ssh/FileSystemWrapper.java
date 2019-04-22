package nixexplorer.core.ssh;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import nixexplorer.core.DataTransferProgress;
import nixexplorer.core.FileInfo;

public interface FileSystemWrapper extends AutoCloseable {
	public void connect() throws Exception;

	public List<FileInfo> list(String path) throws Exception;

	public String getHome() throws Exception;

	public String[] getRoots() throws Exception;

	public boolean isLocal();

	public FileInfo get(String path) throws Exception;

	public void copyTo(String source, String dest, DataTransferProgress prg,
			int mode) throws Exception;

	public void copyTo(String source, OutputStream dest,
			DataTransferProgress prg, int mode, long offset) throws Exception;

	public void chmod(int perm, String path) throws Exception;

	public void delete(FileInfo f) throws Exception;

	public void createLink(String src, String dst, boolean hardLink)
			throws Exception;

	public void deleteFile(String f) throws Exception;

	public long getAllFiles(String dir, String baseDir,
			Map<String, String> fileMap, Map<String, String> folderMap)
			throws Exception;

	public boolean mkdirs(String absPath) throws Exception;

	public void createFile(String path) throws AccessDeniedException, Exception;

	public void mkdir(String path) throws AccessDeniedException, Exception;

	public OutputStream getOutputStream(String file)
			throws FileNotFoundException, Exception;

	public void rename(String oldName, String newName)
			throws FileNotFoundException, Exception;

	public InputStream getInputStream(String file, long offset)
			throws FileNotFoundException, Exception;
}
