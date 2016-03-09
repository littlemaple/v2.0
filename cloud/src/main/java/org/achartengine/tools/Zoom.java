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
package org.achartengine.tools;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.RoundChart;
import org.achartengine.chart.XYChart;
import org.achartengine.renderer.DefaultRenderer;

/**
 * The zoom tool.
 */
public class Zoom extends AbstractTool {
  /** A flag to be used to know if this is a zoom in or out. 放大还是缩小 true:in 放大  false:out 缩小 */
  private boolean mZoomIn;
  /** The zoom rate. 变焦率 (应该是放大缩小倍数) */
  private float mZoomRate;
  /** The zoom listeners. 回调函数集合 */
  private List<ZoomListener> mZoomListeners = new ArrayList<ZoomListener>();
  /** Zoom limits reached on the X axis. */
  private boolean limitsReachedX = false;
  /** Zoom limits reached on the Y axis. */
  private boolean limitsReachedY = false;

  /** Zoom on X axis and Y axis 在X轴和Y轴缩放 及XY都缩放 */
  public static final int ZOOM_AXIS_XY = 0;
  /** Zoom on X axis independently 独立缩放在X轴上 及只在X轴上缩放 */
  public static final int ZOOM_AXIS_X = 1;
  /** Zoom on Y axis independently 独立缩放在Y轴上 及只在Y轴上缩放 */
  public static final int ZOOM_AXIS_Y = 2;

  /**
   * Builds the zoom tool.
   * 
   * @param chart the chart
   * @param in zoom in or out 放大还是缩小 true:in 放大  false:out 缩小
   * @param rate the zoom rate 变焦率 (应该是放大缩小倍数)
   */
  public Zoom(AbstractChart chart, boolean in, float rate) {
    super(chart);
    mZoomIn = in;
    setZoomRate(rate);
  }

  /**
   * Sets the zoom rate.
   * 设置 变焦率 (应该是放大缩小倍数)
   * @param rate
   */
  public void setZoomRate(float rate) {
    mZoomRate = rate;
  }

  /**
   * Apply the zoom. 应用放大
   * 传入参数的意义：缩放的模式 ZOOM_AXIS_X ZOOM_AXIS_Y ZOOM_AXIS_XY (应该没错)
   */
  public void apply(int zoom_axis) {
    if (mChart instanceof XYChart) {
      int scales = mRenderer.getScalesCount();
      for (int i = 0; i < scales; i++) {
        double[] range = getRange(i);
        checkRange(range, i);
        double[] limits = mRenderer.getZoomLimits();

        double centerX = (range[0] + range[1]) / 2;
        double centerY = (range[2] + range[3]) / 2;
        double newWidth = range[1] - range[0];
        double newHeight = range[3] - range[2];
        double newXMin = centerX - newWidth / 2;
        double newXMax = centerX + newWidth / 2;
        double newYMin = centerY - newHeight / 2;
        double newYMax = centerY + newHeight / 2;

        // if already reached last zoom, then it will always set to reached
        if (i == 0) {
          limitsReachedX = limits != null && (newXMin <= limits[0] || newXMax >= limits[1]);
          limitsReachedY = limits != null && (newYMin <= limits[2] || newYMax >= limits[3]);
        }

        if (mZoomIn) {
          if (mRenderer.isZoomXEnabled() && (zoom_axis == ZOOM_AXIS_X || zoom_axis == ZOOM_AXIS_XY)) {
            if (limitsReachedX && mZoomRate < 1) {
              // ignore pinch zoom out once reached X limit
            } else {
              newWidth /= mZoomRate;
            }
          }

          if (mRenderer.isZoomYEnabled() && (zoom_axis == ZOOM_AXIS_Y || zoom_axis == ZOOM_AXIS_XY)) {
            if (limitsReachedY && mZoomRate < 1) {
            } else {
              newHeight /= mZoomRate;
            }
          }
        } else {
          if (mRenderer.isZoomXEnabled() && !limitsReachedX
              && (zoom_axis == ZOOM_AXIS_X || zoom_axis == ZOOM_AXIS_XY)) {
            newWidth *= mZoomRate;
          }

          if (mRenderer.isZoomYEnabled() && !limitsReachedY
              && (zoom_axis == ZOOM_AXIS_Y || zoom_axis == ZOOM_AXIS_XY)) {
            newHeight *= mZoomRate;
          }
        }
        if (mRenderer.isZoomXEnabled() && (zoom_axis == ZOOM_AXIS_X || zoom_axis == ZOOM_AXIS_XY)) {
          newXMin = centerX - newWidth / 2;
          newXMax = centerX + newWidth / 2;
          setXRange(newXMin, newXMax, i);
        }
        if (mRenderer.isZoomYEnabled() && (zoom_axis == ZOOM_AXIS_Y || zoom_axis == ZOOM_AXIS_XY)) {
          newYMin = centerY - newHeight / 2;
          newYMax = centerY + newHeight / 2;
          setYRange(newYMin, newYMax, i);
        }
      }
    } else {
      DefaultRenderer renderer = ((RoundChart) mChart).getRenderer();
      if (mZoomIn) {
        renderer.setScale(renderer.getScale() * mZoomRate);
      } else {
        renderer.setScale(renderer.getScale() / mZoomRate);
      }
    }
    notifyZoomListeners(new ZoomEvent(mZoomIn, mZoomRate));
  }

  /**
   * Notify the zoom listeners about a zoom change.
   * 
   * @param e the zoom event
   */
  private synchronized void notifyZoomListeners(ZoomEvent e) {
    for (ZoomListener listener : mZoomListeners) {
      listener.zoomApplied(e);
    }
  }

  /**
   * Notify the zoom listeners about a zoom reset.
   */
  public synchronized void notifyZoomResetListeners() {
    for (ZoomListener listener : mZoomListeners) {
      listener.zoomReset();
    }
  }

  /**
   * Adds a new zoom listener.
   * 
   * @param listener zoom listener
   */
  public synchronized void addZoomListener(ZoomListener listener) {
    mZoomListeners.add(listener);
  }

  /**
   * Removes a zoom listener.
   * 
   * @param listener zoom listener
   */
  public synchronized void removeZoomListener(ZoomListener listener) {
    mZoomListeners.remove(listener);
  }

}
