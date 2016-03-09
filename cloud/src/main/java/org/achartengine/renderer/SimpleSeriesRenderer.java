/**
 * Copyright (C) 2009 - 2012 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.achartengine.renderer;

import java.io.Serializable;
import java.text.NumberFormat;

import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * A simple series renderer.
 * 一个简单的渲染器系列。
 */
public class SimpleSeriesRenderer implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -3065433371441957730L;
  /** The series color. 系列的颜色。 */
  private int mColor = Color.BLUE;
  /** If the values should be displayed above the chart points. 图表上面是否需要显示的值点。 */
  private boolean mDisplayChartValues;
  /** The minimum distance between displaying chart values. 图表显示值之间的最小距离。 */
  private int mDisplayChartValuesDistance = 100;
  /** The chart values text size. 图表文字大小值。 */
  private float mChartValuesTextSize = 10;
  /** The chart values text alignment. 表值文本对齐方式。*/
  private Align mChartValuesTextAlign = Align.CENTER;
  /** The chart values spacing from the data point. 图表数据点的间距值。 */
  private float mChartValuesSpacing = 5f;
  /** The stroke style. 描边风格*/
  private BasicStroke mStroke;
  /** If gradient is enabled. 是否启用了梯度。(趋势图中好像不需要这值)*/
  private boolean mGradientEnabled = false;
  /** The gradient start value. 梯度-开始值。 */
  private double mGradientStartValue;
  /** The gradient start color. 渐变开始颜色。 */
  private int mGradientStartColor;
  /** The gradient stop value. 梯度-结束值。*/
  private double mGradientStopValue;
  /** The gradient stop color. 渐变结束颜色。*/
  private int mGradientStopColor;
  /** If the legend item for this renderer is visible. 渲染器的图例条目是否可见。*/
  private boolean mShowLegendItem = true;
  /** The chart values format. 值的图表格式。 */
  private NumberFormat mChartValuesFormat;
  /** If this is a highlighted slice (pie chart displays slice as exploded). 是否显示为一个高亮片段区域(饼图显示片分解视图)。 */
  private boolean mHighlighted;

  /**
   * Returns the series color.
   * 返回系列的颜色。
   * @return the series color
   */
  public int getColor() {
    return mColor;
  }

  /**
   * Sets the series color.
   * 设置系列的颜色。
   * @param color the series color
   */
  public void setColor(int color) {
    mColor = color;
  }

  /**
   * Returns if the chart point values should be displayed as text.
   * 返回 图点的值是否需要显示为文本。
   * @return if the chart point values should be displayed as text
   */
  public boolean isDisplayChartValues() {
    return mDisplayChartValues;
  }

  /**
   * Sets if the chart point values should be displayed as text.
   * 设置 图点的值是否需要显示为文本。
   * @param display if the chart point values should be displayed as text
   */
  public void setDisplayChartValues(boolean display) {
    mDisplayChartValues = display;
  }

  /**
   * Returns the chart values minimum distance.
   * 返回值最小距离的图表。
   * @return the chart values minimum distance
   */
  public int getDisplayChartValuesDistance() {
    return mDisplayChartValuesDistance;
  }

  /**
   * Sets chart values minimum distance.
   * 设置 值最小距离的图表。
   * @param distance the chart values minimum distance
   */
  public void setDisplayChartValuesDistance(int distance) {
    mDisplayChartValuesDistance = distance;
  }

  /**
   * Returns the chart values text size.
   * 返回 图表文字大小值。
   * @return the chart values text size
   */
  public float getChartValuesTextSize() {
    return mChartValuesTextSize;
  }

  /**
   * Sets the chart values text size.
   * 设置 图表文字大小值。
   * @param textSize the chart values text size
   */
  public void setChartValuesTextSize(float textSize) {
    mChartValuesTextSize = textSize;
  }

  /**
   * Returns the chart values text align.
   * 返回 表值文本对齐方式。
   * @return the chart values text align
   */
  public Align getChartValuesTextAlign() {
    return mChartValuesTextAlign;
  }

  /**
   * Sets the chart values text align.
   * 设置 表值文本对齐方式。
   * @param align the chart values text align
   */
  public void setChartValuesTextAlign(Align align) {
    mChartValuesTextAlign = align;
  }

  /**
   * Returns the chart values spacing from the data point.
   * 返回 图表数据点的间距值。
   * @return the chart values spacing
   */
  public float getChartValuesSpacing() {
    return mChartValuesSpacing;
  }

  /**
   * Sets the chart values spacing from the data point.
   * 设置 图表数据点的间距。
   * @param spacing the chart values spacing (in pixels) from the chart data
   *          point
   */
  public void setChartValuesSpacing(float spacing) {
    mChartValuesSpacing = spacing;
  }

  /**
   * Returns the stroke style.
   * 返回 描边风格
   * @return the stroke style
   */
  public BasicStroke getStroke() {
    return mStroke;
  }

  /**
   * Sets the stroke style.
   * 设置 描边风格
   * @param stroke the stroke style
   */
  public void setStroke(BasicStroke stroke) {
    mStroke = stroke;
  }

  /**
   * Returns the gradient is enabled value.
   * 返回 是否启用了梯度。
   * @return the gradient enabled
   */
  public boolean isGradientEnabled() {
    return mGradientEnabled;
  }

  /**
   * Sets the gradient enabled value.
   * 设置 是否启用梯度
   * @param enabled the gradient enabled
   */
  public void setGradientEnabled(boolean enabled) {
    mGradientEnabled = enabled;
  }

  /**
   * Returns the gradient start value.
   * 返回 梯度-开始值
   * @return the gradient start value
   */
  public double getGradientStartValue() {
    return mGradientStartValue;
  }

  /**
   * Returns the gradient start color.
   * 设置 梯度-开始值
   * @return the gradient start color
   */
  public int getGradientStartColor() {
    return mGradientStartColor;
  }

  /**
   * Sets the gradient start value and color.
   * 设置 梯度-开始值 和 渐变开始颜色
   * @param start the gradient start value
   * @param color the gradient start color
   */
  public void setGradientStart(double start, int color) {
    mGradientStartValue = start;
    mGradientStartColor = color;
  }

  /**
   * Returns the gradient stop value.
   * 返回 梯度-结束值。
   * @return the gradient stop value
   */
  public double getGradientStopValue() {
    return mGradientStopValue;
  }

  /**
   * Returns the gradient stop color.
   * 返回 渐变结束颜色。
   * @return the gradient stop color
   */
  public int getGradientStopColor() {
    return mGradientStopColor;
  }

  /**
   * Sets the gradient stop value and color.
   * 设置 梯度-结束值 和 渐变结束颜色。
   * @param start the gradient stop value
   * @param color the gradient stop color
   */
  public void setGradientStop(double start, int color) {
    mGradientStopValue = start;
    mGradientStopColor = color;
  }

  /**
   * Returns if the legend item for this renderer should be visible.
   * 返回 渲染器的图例条目是否可见。
   * @return the visibility flag for the legend item for this renderer
   */
  public boolean isShowLegendItem() {
    return mShowLegendItem;
  }

  /**
   * Sets if the legend item for this renderer should be visible.
   * 设置 渲染器的图例条目是否可见。
   * @param showLegend the visibility flag for the legend item for this renderer
   */
  public void setShowLegendItem(boolean showLegend) {
    mShowLegendItem = showLegend;
  }

  /**
   * Returns if the item is displayed highlighted.
   * 返回 是否显示为一个高亮片段区域(饼图显示片分解视图)。 
   * @return the highlighted flag for the item for this renderer
   */
  public boolean isHighlighted() {
    return mHighlighted;
  }

  /**
   * Sets if the item for this renderer should be highlighted. Pie chart is supported for now.
   * 设置 是否显示为一个高亮片段区域，饼图的支持。 
   * @param highlighted the highlighted flag for the item for this renderer
   */
  public void setHighlighted(boolean highlighted) {
    mHighlighted = highlighted;
  }

  /**
   * Returns the number format for displaying chart values.
   * 返回 数字格式显示表的值。
   * @return the number format for chart values
   */
  public NumberFormat getChartValuesFormat() {
    return mChartValuesFormat;
  }

  /**
   * Sets the number format for displaying chart values.
   * 设置 数字格式显示表的值。
   * @param format the number format for chart values
   */
  public void setChartValuesFormat(NumberFormat format) {
    mChartValuesFormat = format;
  }

}
