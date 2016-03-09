/**
 * 
 */
package com.medzone.framework.module;

/**
 * @author ChenJunQi.
 * 
 *         初始，安装，卸载，显示，隐藏，进行中短暂状态暂不考虑
 */
public enum ModuleStatus {
	HIDDEN(-2), UNINSTALL(-1), INITIAL(0), INSTALL(1), DISPLAY(2);

	protected Integer statusId;

	ModuleStatus(Integer id) {
		setStatusId(id);
	}

	/**
	 * @param statusId
	 *            the statusId to set
	 */
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	/**
	 * @return the statusId
	 */
	public Integer getStatusId() {
		return statusId;
	}
}
