/**
 * 
 */
package nixexplorer.widgets.sysmon;

/**
 * @author subhro
 *
 */
public class ServiceEntry {
	private String name;
	private String unitStatus;
	private String desc;
	private String unitFileStatus;

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
	 * @return the unitStatus
	 */
	public String getUnitStatus() {
		return unitStatus;
	}

	/**
	 * @param unitStatus the unitStatus to set
	 */
	public void setUnitStatus(String unitStatus) {
		this.unitStatus = unitStatus;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the unitFileStatus
	 */
	public String getUnitFileStatus() {
		return unitFileStatus;
	}

	/**
	 * @param unitFileStatus the unitFileStatus to set
	 */
	public void setUnitFileStatus(String unitFileStatus) {
		this.unitFileStatus = unitFileStatus;
	}
}
