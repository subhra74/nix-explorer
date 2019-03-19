/**
 * 
 */
package nixexplorer.widgets.logviewer;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * @author subhro
 *
 */
public class RandomAccessInputStream extends InputStream {

	private RandomAccessFile raf;

	/**
	 * @param raf
	 */
	public RandomAccessInputStream(RandomAccessFile raf) {
		super();
		this.raf = raf;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public void seek(long pos) throws IOException {
		raf.seek(pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		return raf.read();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		return raf.read(b, off, len);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException {
		// TODO Auto-generated method stub
		return raf.read(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		raf = null;
	}
}
