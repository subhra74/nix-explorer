/**
 * 
 */
package nixexplorer.widgets.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author subhro
 *
 */
public class CountingInputStream extends InputStream {
	private InputStream in;
	private long pos = 0L;

	/**
	 * @param in
	 */
	public CountingInputStream(InputStream in, long pos) {
		super();
		this.in = in;
		this.pos = pos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int x = in.read();
		if (x != -1) {
			pos++;
		}
		return x;
	}

	/**
	 * @return the pos
	 */
	public long getPos() {
		return pos;
	}

	/**
	 * @param pos the pos to set
	 */
	public void setPos(long pos) {
		this.pos = pos;
	}
}
