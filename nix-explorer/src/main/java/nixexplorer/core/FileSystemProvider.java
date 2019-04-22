package nixexplorer.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface FileSystemProvider {
	public void connect() throws Exception;

	public FileInfo getInfo(String path) throws Exception;

	public List<FileInfo> list(String path) throws Exception;

	public String getHome() throws Exception;

	public boolean isLocal();

	public InputStream getInputStream(String file, long offset)
			throws FileNotFoundException, Exception;

	public OutputStream getOutputStream(String file)
			throws FileNotFoundException, Exception;

	public void rename(String oldName, String newName)
			throws FileNotFoundException, Exception;

	public void delete(FileInfo f) throws Exception;

	public void deleteFile(String f) throws Exception;

	public void mkdir(String path) throws Exception;

	public void close() throws Exception;

	public boolean isConnected();

	public void copyTo(String source, String dest, DataTransferProgress prg,
			int mode) throws Exception;
	
	public void copyTo(String source, OutputStream dest,
			DataTransferProgress prg, int mode, long offset) throws Exception;

	public void chmod(int perm, String path) throws Exception;

	public boolean mkdirs(String absPath) throws Exception;

	public long getAllFiles(String dir, String baseDir,
			Map<String, String> fileMap, Map<String, String> folderMap)
			throws Exception;

	public String getProtocol();

	public void createFile(String path) throws Exception;

	public String[] getRoots() throws Exception;

	public void createLink(String src, String dst, boolean hardLink)
			throws Exception;
}
