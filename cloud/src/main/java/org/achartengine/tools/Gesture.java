package org.achartengine.tools;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.chart.AbstractChart;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class Gesture extends AbstractTool {

  private List<SingleTapListener> mSingleTapListeners = new ArrayList<SingleTapListener>();

  private GestureDetector mGestureDetector;

  public Gesture(AbstractChart chart, Context context, OnGestureListener listener) {
    super(chart);
    mGestureDetector = new GestureDetector(context, listener);
  }

  public void dispatchTouchEvent(MotionEvent event) {
    mGestureDetector.onTouchEvent(event);
  }

  public synchronized void addSingleTapListener(SingleTapListener listener) {
    mSingleTapListeners.add(listener);
  }

  public synchronized void removeSingleTapListenerr(SingleTapListener listener) {
    mSingleTapListeners.remove(listener);
  }

  public synchronized void notifySingleTapListener(MotionEvent event) {
    for (SingleTapListener listener : mSingleTapListeners) {
      listener.onSingleTap(event);
    }
  }
}
