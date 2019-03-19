//package nixexplorer.core.ssh.filetransfer;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.Executors;
//
//import com.jcraft.jsch.ChannelSftp;
//import com.jcraft.jsch.Session;
//
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.core.ssh.SshWrapper;
//
//public class SshConnectionPool implements Runnable {
//	private static SshConnectionPool me;
//
//	public static synchronized SshConnectionPool getSharedInstance() {
//		if (me == null) {
//			me = new SshConnectionPool();
//		}
//		return me;
//	}
//
//	private class WrapperEntry {
//		long time;
//		SshWrapper wrapper;
//	}
//
//	private SshConnectionPool() {
//		Executors.newSingleThreadExecutor().submit(this);
//	}
//
//	private ConcurrentHashMap<String, WrapperEntry> pool = new ConcurrentHashMap<>();
//
//	public synchronized SshWrapper getCachedEntry(String key1) {
//
//		for (String key : pool.keySet()) {
//			if (key.startsWith(key1)) {
//				WrapperEntry ent = pool.remove(key);
//				if (ent != null) {
//					return ent.wrapper;
//				}
//			}
//		}
//
//		return null;
//	}
//
//	public synchronized void putEntry(String key, SshWrapper wrapper) {
//		synchronized (pool) {
//			WrapperEntry ent = new WrapperEntry();
//			ent.time = System.currentTimeMillis();
//			ent.wrapper = wrapper;
//			pool.put(key + ":" + wrapper.hashCode(), ent);
//		}
//	}
//
//	@Override
//	public void run() {
//		while (true) {
//			try {
//				Thread.sleep(30 * 1000);
//				//System.out.println("Cleaning stalled sshwrappers");
//			} catch (Exception e) {
//			}
//			long t = System.currentTimeMillis();
//			List<String> keys = new ArrayList<>();
//			synchronized (pool) {
//				//System.out.println("Pool size: " + pool.size());
//				for (Map.Entry<String, WrapperEntry> ent : pool.entrySet()) {
//					if (!ent.getValue().wrapper.isConnected()) {
//						keys.add(ent.getKey());
//					}
//				}
//				for (String key : keys) {
//					pool.remove(key);
//				}
//			}
//		}
//	}
//
//}
