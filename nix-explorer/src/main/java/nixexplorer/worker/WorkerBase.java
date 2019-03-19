package nixexplorer.worker;

import java.util.Map;

import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;

public abstract class WorkerBase implements Runnable {
	protected SessionInfo info;
	protected Map<String, Object> env;
	protected String[] args;
	protected AppSession session;

	public WorkerBase(SessionInfo info, AppSession session, String[] args) {
		super();
		this.info = info;
		this.session = session;
		this.args = args;
	}
}
