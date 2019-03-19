package nixexplorer.widgets.dnd;

import java.util.ArrayList;
import java.util.List;

import nixexplorer.app.session.SessionInfo;

public class TransferFileInfo {
	private List<SessionInfo> info = new ArrayList<>();
	private List<String> sourceFiles;
	private List<String> sourceFolders;
	private String baseFolder;
	private Action action = Action.DRAG_DROP;

	public enum Action {
		DRAG_DROP, COPY, CUT
	}

	public List<SessionInfo> getInfo() {
		return info;
	}

	public void addInfo(SessionInfo info) {
		this.info.add(info);
	}

	public List<String> getSourceFiles() {
		return sourceFiles;
	}

	public void setSourceFiles(List<String> remoteFiles) {
		this.sourceFiles = remoteFiles;
	}

	public List<String> getSourceFolders() {
		return sourceFolders;
	}

	public void setSourceFolders(List<String> remoteFolders) {
		this.sourceFolders = remoteFolders;
	}

	public String getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(String localBaseFolder) {
		this.baseFolder = localBaseFolder;
	}

	@Override
	public String toString() {
		return "SftpFileInfo [info=" + info + ", remoteFiles=" + sourceFiles
				+ ", remoteFolders=" + sourceFolders + ", localBaseFolder="
				+ baseFolder + "]";
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

}
