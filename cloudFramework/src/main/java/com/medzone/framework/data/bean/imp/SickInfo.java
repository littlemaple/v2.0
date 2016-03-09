/**
 * 
 */
package com.medzone.framework.data.bean.imp;

import com.medzone.framework.data.bean.BaseDatabaseObject;

/**
 * @author ChenJunQi.
 * 
 */
public class SickInfo extends BaseDatabaseObject {

	/**
	 */
	private static final long serialVersionUID = -7191947990046438936L;

	public static final int MAX_DISEASE_NUMBER = 10;

	private String diseaseName;
	private boolean isDiseaseExist;

	/**
	 * 
	 */
	public SickInfo() {
	}

	/**
	 * @return the illnessName
	 */
	public String getDiseaseName() {
		return diseaseName;
	}

	/**
	 * @param diseaseName
	 *            the illnessName to set
	 */
	public void setDiseaseName(String diseaseName) {
		this.diseaseName = diseaseName;
	}

	/**
	 * @return the isOwner
	 */
	public boolean isDiseaseExist() {
		return isDiseaseExist;
	}

	/**
	 * @param isDiseaseExist
	 *            the isOwner to set
	 */
	public void setDiseaseExist(boolean isDiseaseExist) {
		this.isDiseaseExist = isDiseaseExist;
	}

	public void setOwner(String isDiseaseExist) {
		this.isDiseaseExist = isDiseaseExist.equals("Y") ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.medzone.framework.data.bean.BaseDatabaseObject#isSameRecord(java.
	 * lang.Object)
	 */
	@Override
	public boolean isSameRecord(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
}
