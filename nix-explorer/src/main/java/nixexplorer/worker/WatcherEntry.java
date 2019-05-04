/**
 * 
 */
package nixexplorer.worker;

/**
 * @author subhro
 *
 */
public class WatcherEntry {
	private String remoteFile, localTempFile, localDirectory, remoteFileName;
	private long lastModified;

	/**
	 * @param remoteFile
	 * @param localTempFile
	 * @param localDirectory
	 */
	public WatcherEntry(String remoteFile, String localTempFile,
			String localDirectory, long lastModified, String remoteFileName) {
		super();
		this.remoteFile = remoteFile;
		this.localTempFile = localTempFile;
		this.localDirectory = localDirectory;
		this.lastModified = lastModified;
		this.remoteFileName = remoteFileName;
	}

	/**
	 * @return the remoteFile
	 */
	public String getRemoteFile() {
		return remoteFile;
	}

	/**
	 * @param remoteFile the remoteFile to set
	 */
	public void setRemoteFile(String remoteFile) {
		this.remoteFile = remoteFile;
	}

	/**
	 * @return the localTempFile
	 */
	public String getLocalTempFile() {
		return localTempFile;
	}

	/**
	 * @param localTempFile the localTempFile to set
	 */
	public void setLocalTempFile(String localTempFile) {
		this.localTempFile = localTempFile;
	}

	/**
	 * @return the localDirectory
	 */
	public String getLocalDirectory() {
		return localDirectory;
	}

	/**
	 * @param localDirectory the localDirectory to set
	 */
	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}

	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the remoteFileName
	 */
	public String getRemoteFileName() {
		return remoteFileName;
	}

	/**
	 * @param remoteFileName the remoteFileName to set
	 */
	public void setRemoteFileName(String remoteFileName) {
		this.remoteFileName = remoteFileName;
	}
}
