/**
 * 
 */
package nixexplorer.command;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshTtyConnector;
import nixexplorer.widgets.console.CustomJediterm;

/**
 * @author subhro
 *
 */
public class TermHolder {
	private SessionInfo info;
	private SshTtyConnector tty;
	private CustomJediterm term;
	private ImageIcon icon;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return info.getName();
	}

	/**
	 * @return the info
	 */
	public SessionInfo getInfo() {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(SessionInfo info) {
		this.info = info;
	}

	/**
	 * @return the term
	 */
	public CustomJediterm getTerm() {
		return term;
	}

	/**
	 * @param term the term to set
	 */
	public void setTerm(CustomJediterm term) {
		this.term = term;
	}

	public SshTtyConnector getTty() {
		return tty;
	}

	public void setTty(SshTtyConnector tty) {
		this.tty = tty;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
}
