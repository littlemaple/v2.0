/**
 * 
 */
package com.medzone.framework.data.controller.manager;

import java.util.ArrayList;

import android.os.Handler;

/**
 * @author ChenJunQi.
 * 
 * 
 */
public final class ControllerRoot {

	private static final ThreadLocal<RunQueue> sRunQueues = new ThreadLocal<RunQueue>();

	public static RunQueue getRunQueue() {
		RunQueue rq = sRunQueues.get();
		if (rq != null) {
			return rq;
		}
		rq = new RunQueue();
		sRunQueues.set(rq);
		return rq;
	}

	private static class HandlerAction {
		Runnable action;
		long delay;

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (null == obj || getClass() != obj.getClass())
				return false;
			HandlerAction that = (HandlerAction) obj;
			return !(action != null ? !action.equals(that.action)
					: that.action != null);
		}

		@Override
		public int hashCode() {
			int result = action != null ? action.hashCode() : 0;
			result = 31 * result + (int) (delay ^ (delay >>> 32));
			return result;
		}
	}

	/**
	 * 
	 * @author ChenJunQi.
	 * 
	 */
	public static final class RunQueue {
		private final ArrayList<HandlerAction> mActions = new ArrayList<HandlerAction>();

		public void post(Runnable action) {
			postDelayed(action, 0);
		}

		public void postDelayed(Runnable action, long delayMillis) {
			HandlerAction handlerAction = new HandlerAction();
			handlerAction.action = action;
			handlerAction.delay = delayMillis;

			synchronized (mActions) {
				mActions.add(handlerAction);
			}
		}

		public void removeCallbacks(Runnable action) {
			final HandlerAction handlerAction = new HandlerAction();
			handlerAction.action = action;

			synchronized (mActions) {
				final ArrayList<HandlerAction> actions = mActions;

				while (actions.remove(handlerAction)) {
					// Keep going
				}
			}
		}

		public void executeActions() {
			synchronized (mActions) {
				final ArrayList<HandlerAction> actions = mActions;
				final int count = actions.size();

				for (int i = 0; i < count; i++) {
					final HandlerAction handlerAction = actions.get(i);
					mControllerHandler.postDelayed(handlerAction.action,
							handlerAction.delay);
				}
				actions.clear();
			}
		}
	}

	private static ControllerHandler mControllerHandler = new ControllerHandler();

	static final class ControllerHandler extends Handler {
		// TODO Super handler to do something.
	}
 }
