/**
 * 
 */
package nixexplorer.worker.editwatcher;

/**
 * @author subhro
 *
 */
public class WatchTargetEntry {
	private String tempFile;
	private String realFile;

	/**
	 * @param tempFile
	 * @param realFile
	 */
	public WatchTargetEntry(String tempFile, String realFile) {
		super();
		this.tempFile = tempFile;
		this.realFile = realFile;
	}

	/**
	 * @return the tempFile
	 */
	public String getTempFile() {
		return tempFile;
	}

	/**
	 * @param tempFile the tempFile to set
	 */
	public void setTempFile(String tempFile) {
		this.tempFile = tempFile;
	}

	/**
	 * @return the realFile
	 */
	public String getRealFile() {
		return realFile;
	}

	/**
	 * @param realFile the realFile to set
	 */
	public void setRealFile(String realFile) {
		this.realFile = realFile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((realFile == null) ? 0 : realFile.hashCode());
		result = prime * result
				+ ((tempFile == null) ? 0 : tempFile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WatchTargetEntry other = (WatchTargetEntry) obj;
		if (realFile == null) {
			if (other.realFile != null)
				return false;
		} else if (!realFile.equals(other.realFile))
			return false;
		if (tempFile == null) {
			if (other.tempFile != null)
				return false;
		} else if (!tempFile.equals(other.tempFile))
			return false;
		return true;
	}
}
