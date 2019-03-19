package nixexplorer.app;

import nixexplorer.app.session.AppSession;

public interface SessionListCallback {
	public void close(AppSession session);
}
