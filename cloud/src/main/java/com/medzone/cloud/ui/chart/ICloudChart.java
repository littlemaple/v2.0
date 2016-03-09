package com.medzone.cloud.ui.chart;

import org.achartengine.GraphicalView;

import android.content.Intent;

/**
 * Defines the demo charts.
 */
public interface ICloudChart {
	/** A constant for the name field in a list activity. */
	String NAME = "name";
	/** A constant for the description field in a list activity. */
	String DESC = "desc";

	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	String getName();

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	String getDesc();

	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	Intent getIntent( );

	/**
	 * 
	 * @param context
	 * @return chart view 
	 */
	GraphicalView getView();

}
