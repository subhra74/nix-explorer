//package nixexplorer.core.ftp;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTPFile;
//
//import nixexplorer.PathUtils;
//import nixexplorer.core.FileInfo;
//import nixexplorer.core.FileSystemProvider;
//import nixexplorer.core.FileType;
//import nixexplorer.widgets.util.Utility;
//
//public class FtpFileSystemProvider implements FileSystemProvider {
//	private FtpWrapper wrapper;
//	private static final String PROTO_FTP = "ftp";
//
//	public FtpFileSystemProvider(FtpWrapper wrapper) {
//		super();
//		this.wrapper = wrapper;
//	}
//
//	@Override
//	public FileInfo getInfo(String path)
//			throws FileNotFoundException, IOException {
//		if (path.equals("/")) {
//			return new FileInfo("", path, 0, FileType.Directory, 0, -1,
//					PROTO_FTP, "");
//		}
//		String folder = PathUtils.getParent(path);
//		String file = PathUtils.getFileName(path);
////		FTPFile[] dirs = wrapper.getFtp().listDirectories(folder);
////		if (dirs != null && dirs.length > 0) {
////			for (FTPFile f : dirs) {
////				if (f.getName().equals(file)) {
////					return new FileInfo(f.getName(),
////							PathUtils.combineUnix(path, f.getName()), 0,
////							FileType.Directory,
////							f.getTimestamp().getTimeInMillis(), -1, PROTO_FTP,
////							"");
////				}
////			}
////		}
//
//		FTPFile[] files = wrapper.getFtp().listFiles(folder);
//		if (files != null && files.length > 0) {
//			for (FTPFile f : files) {
//				if (f.getName().equals(file)) {
//					return new FileInfo(f.getName(), path, f.getSize(),
//							f.isDirectory() ? FileType.Directory
//									: FileType.File,
//							f.getTimestamp().getTimeInMillis(), -1, PROTO_FTP,
//							"");
//				}
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public List<FileInfo> ll(String path, boolean dirOnly)
//			throws FileNotFoundException, IOException {
//		return ll(path, dirOnly, true);
//	}
//
//	public List<FileInfo> ll(String path, boolean dirOnly,
//			boolean followSymlink) throws FileNotFoundException, IOException {
//		List<FileInfo> list = new ArrayList<>();
////		FTPFile[] dirs = wrapper.getFtp().listDirectories(path);
////		if (dirs != null && dirs.length > 0) {
////			for (FTPFile f : dirs) {
////				list.add(new FileInfo(f.getName(),
////						PathUtils.combineUnix(path, f.getName()), 0,
////						FileType.Directory, f.getTimestamp().getTimeInMillis(),
////						-1, PROTO_FTP, ""));
////			}
////		}
//
//		FTPFile[] files = wrapper.getFtp().listFiles(path);
//		if (files != null && files.length > 0) {
//			for (FTPFile f : files) {
//				if (f.isSymbolicLink()) {
//					if (!followSymlink) {
//						list.add(new FileInfo(f.getName(),
//								PathUtils.combineUnix(path, f.getName()),
//								f.getSize(),
//								f.isDirectory() ? FileType.Directory
//										: FileType.File,
//								f.getTimestamp().getTimeInMillis(), -1,
//								PROTO_FTP, ""));
//					} else {
//						String link = f.getLink();
//						if (!link.startsWith("/")) {
//							link = PathUtils.combineUnix(path, link);
//						}
//						System.out.println("parsign symlink: " + link);
//
//						FileInfo finfo = getInfo(link);
//						list.add(new FileInfo(f.getName(), finfo.getPath(),
//								f.getSize(),
//								finfo.getType() == FileType.Directory
//										? FileType.DirLink
//										: FileType.FileLink,
//								f.getTimestamp().getTimeInMillis(), -1,
//								PROTO_FTP, ""));
//					}
//				} else {
//					list.add(new FileInfo(f.getName(),
//							PathUtils.combineUnix(path, f.getName()),
//							f.getSize(),
//							f.isDirectory() ? FileType.Directory
//									: FileType.File,
//							f.getTimestamp().getTimeInMillis(), -1, PROTO_FTP,
//							""));
//				}
//			}
//		}
//
//		return list;
//	}
//
//	@Override
//	public String getHome() throws FileNotFoundException, IOException {
//		return wrapper.getHome();
//	}
//
//	@Override
//	public boolean isLocal() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public InputStream getInputStream(String file, long offset)
//			throws FileNotFoundException, Exception {
//		synchronized (wrapper) {
//			try {
//				return wrapper.getFtp().retrieveFileStream(file);
//			} catch (Exception e) {
//				throw new Exception();
//			}
//		}
//	}
//
//	@Override
//	public OutputStream getOutputStream(String file)
//			throws FileNotFoundException, Exception {
//		synchronized (wrapper) {
//			return wrapper.getFtp().appendFileStream(file);
//		}
//	}
//
//	@Override
//	public void rename(String oldName, String newName)
//			throws FileNotFoundException, Exception {
//		try {
//			synchronized (wrapper) {
//				wrapper.getFtp().rename(oldName, newName);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new Exception(wrapper.getFtp().getReplyString());
//		}
//	}
//
//	@Override
//	public synchronized void delete(FileInfo f) throws Exception {
//		if (f.getType() == FileType.Directory) {
//			List<FileInfo> list = ll(f.getPath(), false);
//			if (list != null && list.size() > 0) {
//				for (FileInfo fc : list) {
//					delete(fc);
//				}
//			}
//			wrapper.getFtp().removeDirectory(f.getPath());
//		} else {
//			wrapper.getFtp().deleteFile(f.getPath());
//		}
//	}
//
//	@Override
//	public void mkdir(String path) throws Exception {
//		wrapper.getFtp().makeDirectory(path);
//	}
//
//	@Override
//	public void close() {
//		try {
//			wrapper.getFtp().disconnect();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}
//
//	@Override
//	public boolean isConnected() {
//		return true;
//	}
//
//	@Override
//	public void chmod(int perm, String path) throws Exception {
//
//	}
//
//	private FTPFile stat(String str) throws IOException {
//		String parent = PathUtils.getParent(str);
//		String name = PathUtils.getFileName(str);
//		FTPFile[] files = wrapper.getFtp().listFiles(parent);
//		if (files == null) {
//			throw new FileNotFoundException("Ftp: not found -> " + str);
//		}
//		for (int i = 0; i < files.length; i++) {
//			if (files[i].getName().equals(name)) {
//				return files[i];
//			}
//		}
//		throw new FileNotFoundException("Ftp: not found -> " + str);
//	}
//
//	@Override
//	public boolean mkdirs(String absPath) throws Exception {
//		System.out.println("mkdirs: " + absPath);
//		if (absPath.equals("/")) {
//			return true;
//		}
//
//		try {
//			stat(absPath);
//			return false;
//		} catch (FileNotFoundException e) {
////eat exception
//		} catch (Exception e) {
//			throw e;
//		}
//
//		System.out.println("Folder does not exists: " + absPath);
//
//		String parent = PathUtils.getParent(absPath);
//		// String file = PathUtils.getFileName(absPath);
//
//		mkdirs(parent);
//		wrapper.getFtp().makeDirectory(absPath);
//
//		return true;
//	}
//
//	@Override
//	public long getAllFiles(String dir, String baseDir,
//			Map<String, String> fileMap, Map<String, String> folderMap)
//			throws Exception {
//		long size = 0;
//		System.out.println("get files: " + dir);
//		String parentFolder = PathUtils.combineUnix(baseDir,
//				PathUtils.getFileName(dir));
//
//		List<FileInfo> list = ll(dir, false, false);
//		for (FileInfo f : list) {
//			if (f.getType() == FileType.Directory) {
//				folderMap.put(f.getPath(),
//						PathUtils.combineUnix(parentFolder, f.getName()));
//				size += getAllFiles(f.getPath(), parentFolder, fileMap,
//						folderMap);
//			} else {
//				fileMap.put(f.getPath(),
//						PathUtils.combineUnix(parentFolder, f.getName()));
//				size += f.getSize();
//			}
//		}
//		return size;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see nixexplorer.core.FileSystemProvider#deleteFile(java.lang.String)
//	 */
//	@Override
//	public void deleteFile(String f) throws Exception {
//		wrapper.getFtp().deleteFile(f);
//	}
//
//	@Override
//	public String getProtocol() {
//		return PROTO_FTP;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see nixexplorer.core.FileSystemProvider#createFile(java.lang.String)
//	 */
//	@Override
//	public void createFile(String path) throws Exception {
//		// TODO Auto-generated method stub
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see nixexplorer.core.FileSystemProvider#getFsRoots()
//	 */
//	@Override
//	public String[] getFsRoots() throws Exception {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see nixexplorer.core.FileSystemProvider#createLink(java.lang.String, java.lang.String, boolean)
//	 */
//	@Override
//	public void createLink(String src, String dst, boolean hardLink)
//			throws Exception {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
