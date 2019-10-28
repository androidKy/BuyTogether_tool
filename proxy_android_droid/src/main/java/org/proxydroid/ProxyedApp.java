package org.proxydroid;


public class ProxyedApp {

	private boolean enabled;
	private int uid;
	private String username;
	private String procname;
	private String name;

	private boolean proxyed = false;

	/**
	 * @return the proxyed
	 */
	public boolean isProxyed() {
		return proxyed;
	}

	/**
	 * @param proxyed
	 *            the proxyed to set
	 */
	public void setProxyed(boolean proxyed) {
		this.proxyed = proxyed;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the uid
	 */
	public int getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the procname
	 */
	public String getProcname() {
		return procname;
	}

	/**
	 * @param procname
	 *            the procname to set
	 */
	public void setProcname(String procname) {
		this.procname = procname;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}