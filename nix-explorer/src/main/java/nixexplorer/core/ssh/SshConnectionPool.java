/**
 * 
 */
package nixexplorer.core.ssh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nixexplorer.app.session.SessionInfo;

/**
 * @author subhro
 *
 */
public class SshConnectionPool {
	private static final int MAX_POOL_SIZE = 10;
	private final List<SshWrapper> POOL = new ArrayList<>(MAX_POOL_SIZE);

	public synchronized SshWrapper get() {
		for (int i = 0; i < POOL.size(); i++) {
			SshWrapper wrapper = POOL.remove(i);
			if (wrapper.isConnected()) {
				return wrapper;
			}
		}
		return null;
	}

	public synchronized boolean addToPool(SshWrapper wrapper) {
		if (POOL.size() == MAX_POOL_SIZE) {
			return false;
		}
		POOL.add(wrapper);
		return true;
	}

	public synchronized void cleanup() {
		for (SshWrapper wr : POOL) {
			try {
				wr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		POOL.clear();
	}
}
