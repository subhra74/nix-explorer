package nixexplorer.core.ssh.filetransfer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TransferQueue {
	private ExecutorService threadPool;

	public TransferQueue(int threadCount) {
		threadPool = Executors.newFixedThreadPool(threadCount);
	}

	public Future<?> submit(Runnable r) {
		return threadPool.submit(r);
	}
}
