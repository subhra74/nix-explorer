//package nixexplorer.core.ssh.filetransfer.remote2local;
//
//import com.jcraft.jsch.ChannelSftp;
//import com.jcraft.jsch.SftpATTRS;
//
//import nixexplorer.core.ssh.SshWrapper;
//
//public class Downloader implements Runnable {
//	private Object synchonizer = new Object();
//	private SshWrapper wrapper;
//	private ChunkList ChunkList = new ChunkList(synchonizer);
//	private String remoteFile, localFile;
//	private long mizSize = 512 * 1024;
//
//	private void init() throws Exception {
//		ChannelSftp sftp = wrapper.getSftpChannel();
//		SftpATTRS attrs = sftp.stat(remoteFile);
//		long size = attrs.getSize();
//		int chunkCount = 1;
//		if (size > mizSize) {
//			if (size > 1024 * 1024 * 1024) {
//				chunkCount = 100;
//			} else if (size > 100 * 1024 * 1024) {
//				chunkCount = 20;
//			} else if (size > 5 * 1024 * 1024) {
//				chunkCount = 10;
//			} else
//				chunkCount = 5;
//		}
//		long chunkSize = size / chunkCount;
//		for (int i = 0; i < chunkCount; i++) {
//			FileChunk fc = new FileChunk();
//			fc.setFinished(false);
//			fc.setStartPos(0);
//			fc.setLength(size - i * chunkSize);
//			ChunkList.addToList(fc);
//		}
//	}
//
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//
//	}
//
//}
