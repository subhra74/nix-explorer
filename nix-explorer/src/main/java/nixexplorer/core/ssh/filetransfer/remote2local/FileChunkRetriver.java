//package nixexplorer.core.ssh.filetransfer.remote2local;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.RandomAccessFile;
//
//import com.jcraft.jsch.ChannelSftp;
//
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.core.ssh.SshWrapper;
//
//public class FileChunkRetriver implements Runnable {
//	private FileChunk chunk;
//	private ChannelSftp sftp;
//	private long downloaded;
//	private String remoteFile;
//	private byte[] b = new byte[8192];
//	private RandomAccessFile raf;
//	private SshWrapper wrapper;
//	private int maxRetry = 5;
//	private int backoffTime = 30;
//	private boolean closeSession;
//	private ChunkList chunkList;
//	private String localFile;
//	private Thread t;
//
//	public FileChunkRetriver(SessionInfo info, ChunkList chunkList,
//			String localFile, String remoteFile) {
//		this.closeSession = true;
//		this.wrapper = new SshWrapper(info);
//		this.chunkList = chunkList;
//	}
//
//	public FileChunkRetriver(SshWrapper wrapper, ChunkList chunkList,
//			String localFile, String remoteFile, ChannelSftp sftp) {
//		this.wrapper = wrapper;
//		this.closeSession = false;
//		this.chunkList = chunkList;
//		this.sftp = sftp;
//	}
//
//	public void start() {
//		this.t = new Thread(this);
//		this.t.start();
//	}
//
//	@Override
//	public void run() {
//		retrieve();
//	}
//
//	public void retrieve() {
//		while (true) {
//			if (sftp == null || (!sftp.isConnected())) {
//				retry_loop: {
//					for (int i = 0; i < maxRetry; i++) {
//						if (!chunkList.hasAvailableChunk()) {
//							System.out.println("No availale chunk");
//							close();
//							return;
//						}
//						try {
//							connect();
//							break retry_loop;
//						} catch (Exception e) {
//							e.printStackTrace();
//							try {
//								Thread.sleep(backoffTime);
//							} catch (InterruptedException e1) {
//								e1.printStackTrace();
//							}
//							close();
//						}
//					}
//					System.out.println("Quit after max retry");
//					return;
//				}
//			}
//			if (!chunkList.hasAvailableChunk()) {
//				System.out.println("No availale chunk");
//				close();
//				return;
//			}
//			this.chunk = chunkList.pickNextChunk();
//			if (chunk == null) {
//				System.out.println("No availale chunk");
//				close();
//				return;
//			}
//			try {
//				download();
//				this.chunk.setFinished(true);
//				chunkList.chunkFinished();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private void connect() throws Exception {
//		wrapper.connect();
//		sftp = wrapper.getSftpChannel();
//		raf = new RandomAccessFile(localFile, "rw");
//	}
//
//	private void download() throws Exception {
//		InputStream in = sftp.get(remoteFile, null,
//				chunk.getStartPos() + downloaded);
//		raf.seek(chunk.getStartPos() + downloaded);
//		int len = b.length;
//		if (b.length > chunk.getLength() - downloaded) {
//			len = (int) (chunk.getLength() - downloaded);
//		}
//		while (true) {
//			int x = in.read(b, 0, len);
//			if (x == -1) {
//				throw new IOException("Unexpected EOF");
//			}
//			raf.write(b, 0, x);
//			downloaded += x;
//			if (downloaded > chunk.getLength()) {
//				in.close();
//				break;
//			}
//		}
//	}
//
//	private void close() {
//		try {
//			raf.close();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		try {
//			sftp.disconnect();
//		} catch (Exception e) {
//
//		}
//		if (closeSession) {
//			try {
//				wrapper.disconnect();
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
//	}
//
//	public void stop() {
//		t.interrupt();
//	}
//
//}
