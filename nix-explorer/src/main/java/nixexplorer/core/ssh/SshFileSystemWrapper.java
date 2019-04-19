package nixexplorer.core.ssh;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import nixexplorer.PathUtils;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.DataTransferProgress;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;

public class SshFileSystemWrapper implements FileSystemWrapper {

	private SessionInfo info;
	private SshWrapper wrapper;
	private ChannelSftp sftp;

	public SshFileSystemWrapper(SessionInfo info) {
		this.info = info;
	}

	private void ensureConnected() throws Exception {
		if (sftp != null && sftp.isConnected()) {
			return;
		}
		connect();
	}

	@Override
	public synchronized void connect() throws Exception {
		System.out.println("Connecting to: " + info);
		wrapper = SshUtility.connect(info);
		this.sftp = wrapper.getSftpChannel();
	}

	@Override
	public synchronized List<FileInfo> list(String path) throws Exception {
		ensureConnected();
		return listFiles(path);
	}

	private FileInfo resolveSymlink(String name, String pathToResolve, SftpATTRS attrs, String longName)
			throws Exception {
		try {
			System.out.println("Following symlink: " + pathToResolve);
			while (true) {
				String str = sftp.readlink(pathToResolve);
				System.out.println("Read symlink: " + pathToResolve + "=" + str);
				pathToResolve = str.startsWith("/") ? str : PathUtils.combineUnix(pathToResolve, str);
				System.out.println("Getting link attrs: " + pathToResolve);
				attrs = sftp.stat(pathToResolve);

				if (!attrs.isLink()) {
					FileInfo e = new FileInfo(name, pathToResolve, (attrs.isDir() ? -1 : attrs.getSize()),
							attrs.isDir() ? FileType.DirLink : FileType.FileLink, (long) attrs.getMTime() * 1000,
							attrs.getPermissions(), SshFileSystemProvider.PROTO_SFTP, attrs.getPermissionsString(),
							attrs.getATime(), longName);
					return e;
				}
			}
		} catch (SftpException e) {
			if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE || e.id == ChannelSftp.SSH_FX_PERMISSION_DENIED) {
				return new FileInfo(name, pathToResolve, 0, FileType.FileLink, (long) attrs.getMTime() * 1000,
						attrs.getPermissions(), SshFileSystemProvider.PROTO_SFTP, attrs.getPermissionsString(),
						attrs.getATime(), longName);
			}
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	private List<FileInfo> listFiles(String path) throws Exception {
		synchronized (sftp) {
			System.out.println("Listing file: " + path);
			List<FileInfo> childs = new ArrayList<>();
			try {
				if (path == null || path.length() < 1) {
					path = sftp.getHome();
				}
				Vector<?> files = sftp.ls(path);
				if (files.size() > 0) {
					for (int i = 0; i < files.size(); i++) {
						ChannelSftp.LsEntry ent = (LsEntry) files.get(i);
						if (ent.getFilename().equals(".") || ent.getFilename().equals("..")) {
							continue;
						}
						SftpATTRS attrs = ent.getAttrs();
						if (attrs.isLink()) {
							childs.add(resolveSymlink(ent.getFilename(), PathUtils.combineUnix(path, ent.getFilename()),
									attrs, ent.getLongname()));
						} else {
							FileInfo e = new FileInfo(ent.getFilename(), PathUtils.combineUnix(path, ent.getFilename()),
									(attrs.isDir() ? -1 : attrs.getSize()),
									attrs.isDir() ? FileType.Directory : FileType.File, (long) attrs.getMTime() * 1000,
									ent.getAttrs().getPermissions(), SshFileSystemProvider.PROTO_SFTP,
									ent.getAttrs().getPermissionsString(), attrs.getATime(), ent.getLongname());
							childs.add(e);
						}
					}
				}
			} catch (SftpException e) {
				e.printStackTrace();
				if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
					throw new FileNotFoundException(path);
				}
				if (e.id == ChannelSftp.SSH_FX_PERMISSION_DENIED) {
					throw new AccessDeniedException(path);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e);
			}
			return childs;
		}
	}

	@Override
	public synchronized void close() throws Exception {
		if (wrapper != null) {
			System.out.println("Closing wrapper");
			wrapper.close();
		}
	}

	@Override
	public synchronized String getHome() throws Exception {
		ensureConnected();
		return sftp.getHome();
	}

	@Override
	public String[] getRoots() throws Exception {
		return new String[] { "/" };
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public synchronized FileInfo get(String path) throws Exception {
		ensureConnected();
		SftpATTRS attrs = sftp.stat(path);
		if (attrs.isLink()) {
			return resolveSymlink(PathUtils.getFileName(path), path, attrs, null);
		} else {
			FileInfo e = new FileInfo(PathUtils.getFileName(path), path, (attrs.isDir() ? -1 : attrs.getSize()),
					attrs.isDir() ? FileType.Directory : FileType.File, (long) attrs.getMTime() * 1000,
					attrs.getPermissions(), SshFileSystemProvider.PROTO_SFTP, attrs.getPermissionsString(),
					attrs.getATime(), null);
			return e;
		}
	}

	@Override
	public synchronized void copyTo(String source, String dest, DataTransferProgress prg, int mode) throws Exception {
		ensureConnected();
		this.sftp.get(source, dest, prg, mode);
	}

	@Override
	public synchronized void copyTo(String source, OutputStream dest, DataTransferProgress prg, int mode, long offset)
			throws Exception {
		ensureConnected();
		this.sftp.get(source, dest, prg, mode, offset);
	}
}