package nixexplorer.core;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import nixexplorer.widgets.util.Utility;

public class FileInfo {
	private String name;
	private String path;
	private long size;
	private FileType type;
	private LocalDateTime lastModified;
	private LocalDateTime created;
	private int permission;
	private String protocol;
	private String permissionString;
	private String extra;

	public FileInfo(String name, String path, long size, FileType type,
			long lastModified, int permission, String protocol,
			String permissionString, long created, String extra) {
		super();
		this.name = name;
		this.path = path;
		this.size = size;
		this.type = type;
		this.lastModified = Utility.toDateTime(lastModified);
		this.permission = permission;
		this.protocol = protocol;
		this.permissionString = permissionString;
		this.created = Utility.toDateTime(created);
		this.extra = extra;
	}

	public String getPath() {
		return path;
	}

	public void setName(String name) {
		this.path = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public FileType getType() {
		return type;
	}

	public void setType(FileType type) {
		this.type = type;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = Utility.toDateTime(lastModified);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "FileInfo [name=" + name + ", path=" + path + ", size=" + size
				+ ", type=" + type + ", lastModified=" + lastModified + "]";
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getPermissionString() {
		return permissionString;
	}

	public void setPermissionString(String permissionString) {
		this.permissionString = permissionString;
	}

	/**
	 * @return the created
	 */
	public LocalDateTime getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	/**
	 * @return the extra
	 */
	public String getExtra() {
		return extra;
	}

	/**
	 * @param extra the extra to set
	 */
	public void setExtra(String extra) {
		this.extra = extra;
	}
}
