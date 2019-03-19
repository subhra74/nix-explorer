/**
 * 
 */
package nixexplorer.widgets.http;

/**
 * @author subhro
 *
 */
public class KeyValuePair {
	/**
	 * @param key
	 * @param value
	 */
	public KeyValuePair(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	/**
	 * 
	 */
	public KeyValuePair() {
		super();
		// TODO Auto-generated constructor stub
	}

	private String key, value;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
