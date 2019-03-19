/**
 * 
 */
package nixexplorer.worker.editwatcher;

import java.nio.file.Path;

/**
 * @author subhro
 *
 */
public class FileEntry {

	private Path folder, file;
	private ChangeUploader cu;
	private String fileName;

	/**
	 * @param file
	 * @param folder
	 */
	public FileEntry(String fileName, Path file, Path folder,
			ChangeUploader cu) {
		super();
		this.fileName = fileName;
		this.file = file;
		this.folder = folder;
		this.cu = cu;
	}

	/**
	 * @return the folder
	 */
	public Path getFolder() {
		return folder;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(Path folder) {
		this.folder = folder;
	}

	/**
	 * @return the file
	 */
	public Path getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(Path file) {
		this.file = file;
	}

	/**
	 * @return the cu
	 */
	public ChangeUploader getCu() {
		return cu;
	}

	/**
	 * @param cu the cu to set
	 */
	public void setCu(ChangeUploader cu) {
		this.cu = cu;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
