/**
 * 
 */
package com.medzone.cloud.module.modules;

/**
 * @author ChenJunQi.
 * 
 * 
 */
public interface IMonthlyStatInterface {

	public int getMonthlyIndicator();

	public int getMonthlyAllCounts(Integer year, Integer month);

	public int getMonthlyExceptionCounts(Integer year, Integer month);

}
