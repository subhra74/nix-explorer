/**
 * 
 */
package nixexplorer.widgets.portforwarding;

/**
 * @author subhro
 *
 */
public class PortForwardingEntry {
	/**
	 * @param target
	 * @param targetPort
	 * @param localPort
	 * @param allowRemoteConnections
	 */
	public PortForwardingEntry(String name, String target, int targetPort,
			int localPort, String bindAddress) {
		super();
		this.name = name;
		this.target = target;
		this.targetPort = targetPort;
		this.sourcePort = localPort;
		this.bindAddress = bindAddress;
	}

	private String target;
	private int targetPort;
	private int sourcePort;
	private String name;
	private boolean connected;
	private String bindAddress;

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return the targetPort
	 */
	public int getTargetPort() {
		return targetPort;
	}

	/**
	 * @param targetPort the targetPort to set
	 */
	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	/**
	 * @return the localPort
	 */
	public int getSourcePort() {
		return sourcePort;
	}

	/**
	 * @param localPort the localPort to set
	 */
	public void setSourcePort(int localPort) {
		this.sourcePort = localPort;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * @param connected the connected to set
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	/**
	 * @return the bindAddress
	 */
	public String getBindAddress() {
		return bindAddress;
	}

	/**
	 * @param bindAddress the bindAddress to set
	 */
	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}
}
