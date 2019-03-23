package nixexplorer.core.ssh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.plaf.FileChooserUI;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import nixexplorer.PathUtils;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileSystemProvider;
import nixexplorer.core.FileType;

public class SshFileSystemProvider implements FileSystemProvider {

	private ChannelSftp sftp;
	public static final String PROTO_SFTP = "sftp";

	public SshFileSystemProvider(ChannelSftp sftp) throws Exception {
		this.sftp = sftp;
	}

	public void chmod(int perm, String path) throws Exception {
		this.sftp.chmod(perm, path);
	}

	public synchronized void delete(FileInfo f) throws Exception {
//		if (this.sftp == null) {
//			this.sftp = wrapper.getSftpChannel();
//		}
		if (f.getType() == FileType.Directory) {
			List<FileInfo> list = ll(f.getPath(), false);
			if (list != null && list.size() > 0) {
				for (FileInfo fc : list) {
					delete(fc);
				}
			}
			this.sftp.rmdir(f.getPath());
		} else {
			this.sftp.rm(f.getPath());
		}
	}

	@Override
	public synchronized String getHome()
			throws FileNotFoundException, IOException {
		try {
//			if (this.sftp == null) {
//				this.sftp = wrapper.getSftpChannel();
//			}
			synchronized (sftp) {
				return sftp.getHome();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (!sftp.isConnected()) {
				throw new IOException(e);
			}
			throw new FileNotFoundException(e.getMessage());
		}
	}

	@Override
	public synchronized List<FileInfo> ll(String path, boolean onlyDir)
			throws FileNotFoundException, IOException {
		return ll(path, onlyDir, true);
	}

	private synchronized List<FileInfo> ll(String path, boolean onlyDir,
			boolean followSymlink) throws FileNotFoundException, IOException {
		try {
			System.out.println("Listing file: " + path);
			List<FileInfo> childs = new ArrayList<>();
			try {
//			if (this.sftp == null) {
//				this.sftp = wrapper.getSftpChannel();
//			}
				if (path == null || path.length() < 1) {
					path = sftp.getHome();
				}
				synchronized (sftp) {
					Vector files = sftp.ls(path);
					if (files.size() > 0) {
						for (int i = 0; i < files.size(); i++) {
							ChannelSftp.LsEntry ent = (LsEntry) files.get(i);
							if (ent.getFilename().equals(".")
									|| ent.getFilename().equals("..")) {
								continue;
							}
							SftpATTRS attrs = ent.getAttrs();
							// System.out.println(attrs.getPermissionsString());
							if (attrs.isLink()) {
								if (!followSymlink) {
									FileInfo e = new FileInfo(ent.getFilename(),
											PathUtils.combineUnix(path,
													ent.getFilename()),
											(attrs.isDir() ? -1
													: attrs.getSize()),
											attrs.isDir() ? FileType.Directory
													: FileType.File,
											(long) attrs.getMTime() * 1000,
											ent.getAttrs().getPermissions(),
											PROTO_SFTP,
											ent.getAttrs()
													.getPermissionsString(),
											attrs.getATime(),
											ent.getLongname());
									childs.add(e);
								} else {
									try {
										String pathToResolve = PathUtils
												.combineUnix(path,
														ent.getFilename());
										System.out.println("Following symlink: "
												+ pathToResolve);

										String str = sftp
												.readlink(pathToResolve);

										System.out.println("Read symlink: "
												+ pathToResolve + "=" + str);

										str = str.startsWith("/") ? str
												: PathUtils.combineUnix(path,
														str);
										System.out.println(
												"Getting link attrs: " + str);

										try {
											attrs = sftp.stat(str);
											System.out.println(
													"Adding link resolved: "
															+ ent.getFilename());

											FileInfo e = new FileInfo(
													ent.getFilename(), str,
													(attrs.isDir() ? -1
															: attrs.getSize()),
													attrs.isDir()
															? FileType.DirLink
															: FileType.FileLink,
													(long) attrs.getMTime()
															* 1000,
													ent.getAttrs()
															.getPermissions(),
													PROTO_SFTP,
													ent.getAttrs()
															.getPermissionsString(),
													attrs.getATime(),
													ent.getLongname());
											childs.add(e);
										} catch (SftpException e) {
											if (!sftp.isConnected()) {
												throw e;
											}
											childs.add(new FileInfo(
													ent.getFilename(),
													PathUtils.combineUnix(path,
															ent.getFilename()),
													0, FileType.FileLink,
													(long) attrs.getMTime()
															* 1000,
													ent.getAttrs()
															.getPermissions(),
													PROTO_SFTP,
													ent.getAttrs()
															.getPermissionsString(),
													attrs.getATime(),
													ent.getLongname()));
										}
									} catch (Exception e) {
										e.printStackTrace();
										if (!sftp.isConnected()) {
											throw e;
										}
									}
								}
							} else {

								if (onlyDir && (!attrs.isDir())) {
									continue;
								}
								FileInfo e = new FileInfo(ent.getFilename(),
										PathUtils.combineUnix(path,
												ent.getFilename()),
										(attrs.isDir() ? -1 : attrs.getSize()),
										attrs.isDir() ? FileType.Directory
												: FileType.File,
										(long) attrs.getMTime() * 1000,
										ent.getAttrs().getPermissions(),
										PROTO_SFTP,
										ent.getAttrs().getPermissionsString(),
										attrs.getATime(), ent.getLongname());
								childs.add(e);
							}
						}
					}
				}
				System.out.println("Listing directory: " + path);

			} catch (Exception e) {
				if (!sftp.isConnected()) {
					throw new IOException(e);
				}
//			if (!wrapper.isConnected()) {
//				throw new IOException(e);
//			}
				throw new FileNotFoundException(e.getMessage());
			}

			return childs;
		} finally {
			System.out.println("Returned fro ll");
		}
	}

	@Override
	public synchronized FileInfo getInfo(String path)
			throws FileNotFoundException, IOException

	{
		synchronized (sftp) {
			try {
				SftpATTRS attrs = sftp.stat(path);
				FileInfo info = new FileInfo(PathUtils.getFileName(path), path,
						(attrs.isDir() ? -1 : attrs.getSize()),
						attrs.isDir() ? FileType.Directory
								: (attrs.isLink() ? FileType.FileLink
										: FileType.File),
						attrs.getMTime(), attrs.getPermissions(), PROTO_SFTP,
						attrs.getPermissionsString(), attrs.getATime(), "");
				return info;
			} catch (Exception e) {
				if (!sftp.isConnected()) {
					throw new IOException(e);
				}
				throw new FileNotFoundException(e.getMessage());
			}
		}
	}

	@Override
	public boolean isLocal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputStream getInputStream(String file, long offset)
			throws FileNotFoundException, Exception {
//		if (this.sftp == null) {
//			this.sftp = wrapper.getSftpChannel();
//		}
		synchronized (sftp) {
			try {
				return sftp.get(file, null, offset);
			} catch (Exception e) {
				if (sftp.isConnected()) {
					throw new FileNotFoundException();
				}
				throw new Exception();
			}
		}
	}

	@Override
	public OutputStream getOutputStream(String file)
			throws FileNotFoundException, Exception {
		synchronized (sftp) {
			return sftp.put(file, null, ChannelSftp.APPEND, 0);
		}
	}

	public void rename(String oldName, String newName)
			throws FileNotFoundException, Exception {
		try {
			synchronized (sftp) {
				sftp.rename(oldName, newName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (sftp.isConnected()) {
				throw new FileNotFoundException();
			}
			throw new Exception();
		}
	}

	@Override
	public void mkdir(String path) throws Exception {
		try {
			sftp.mkdir(path);
		} catch (Exception e) {
			if (sftp.isConnected()) {
				throw new FileNotFoundException(e.getMessage());
			}
			throw new Exception(e);
		}
	}

	@Override
	public void createFile(String path) throws Exception {
		try {
			sftp.put(path).close();
		} catch (Exception e) {
			if (sftp.isConnected()) {
				throw new FileNotFoundException(e.getMessage());
			}
			throw new Exception(e);
		}
	}

	@Override
	public void close() {
		try {
			this.sftp.disconnect();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public boolean isConnected() {
		return this.sftp != null && this.sftp.isConnected();
	}

	@Override
	public boolean mkdirs(String absPath) throws Exception {
		System.out.println("mkdirs: " + absPath);
		if (absPath.equals("/")) {
			return true;
		}

		try {
			sftp.stat(absPath);
			return false;
		} catch (Exception e) {
			if (!sftp.isConnected()) {
				throw e;
			}
		}

		System.out.println("Folder does not exists: " + absPath);

		String parent = PathUtils.getParent(absPath);
		// String file = PathUtils.getFileName(absPath);

		mkdirs(parent);
		sftp.mkdir(absPath);

		return true;
	}

	@Override
	public long getAllFiles(String dir, String baseDir,
			Map<String, String> fileMap, Map<String, String> folderMap)
			throws Exception {
		long size = 0;
		System.out.println("get files: " + dir);
		String parentFolder = PathUtils.combine(baseDir,
				PathUtils.getFileName(dir), File.separator);

		folderMap.put(dir, parentFolder);

		List<FileInfo> list = ll(dir, false, false);
		for (FileInfo f : list) {
			if (f.getType() == FileType.Directory) {
				folderMap.put(f.getPath(), PathUtils.combine(parentFolder,
						f.getName(), File.separator));
				size += getAllFiles(f.getPath(), parentFolder, fileMap,
						folderMap);
			} else {
				fileMap.put(f.getPath(), PathUtils.combine(parentFolder,
						f.getName(), File.separator));
				size += f.getSize();
			}
		}
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.core.FileSystemProvider#deleteFile(java.lang.String)
	 */
	@Override
	public void deleteFile(String f) throws Exception {
		this.sftp.rm(f);
	}

	@Override
	public String getProtocol() {
		return PROTO_SFTP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.core.FileSystemProvider#getFsRoots()
	 */
	@Override
	public String[] getFsRoots() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void createLink(String src, String dst, boolean hardLink)
			throws Exception {
		if (hardLink) {
			this.sftp.hardlink(src, dst);
		} else {
			this.sftp.symlink(src, dst);
		}
	}

}
