package com.medzone.framework.data.controller;

import java.util.Observable;

import android.os.Handler;

import com.medzone.framework.data.bean.imp.AttachInfo;
import com.medzone.framework.data.controller.manager.ControllerManager;
import com.medzone.framework.data.controller.manager.ControllerManagerImpl;
import com.medzone.framework.data.controller.manager.ControllerRoot;
import com.medzone.framework.data.model.Cache;

/**
 * 
 * @author ChenJunQi.
 * 
 * @param <T>
 *            An instance of the model class is required.
 * 
 */
public abstract class AbstractController<T extends Cache<?>> extends Observable {

	private ControllerManager sControllerManager;
	protected Handler sHandler;
	protected T sCache;

	public static enum AttachEvent {
		ATTACH, DETACH
	}

	protected abstract T createCache();

	protected void cacheChanged() {
		setChanged();
		notifyObservers();
	}

	public AbstractController() {
		sCache = createCache();
	}

	public T getCache() {
		return sCache;
	}

	public void clearCache() {
		sCache.clear();
		cacheChanged();
	}

	public abstract void setAttachInfo(AttachInfo mAttachInfo);

	public abstract AttachInfo getAttachInfo();

	public abstract boolean isVaild();

	protected void onAttach() {

	};

	/**
	 * 可以在这里释放一些对象
	 */
	protected void onDetach() {

	};

	public void dispatchAttachedToController(AttachEvent event) {

		switch (event) {
		case ATTACH:
			// ControllerRoot.getRunQueue().executeActions();
			onAttach();
			break;
		case DETACH:
			onDetach();
			break;
		default:
			break;
		}

	}

	public ControllerManager getControllerManager() {
		return sControllerManager;
	}

	public void setControllerManager(ControllerManager cm) {
		if (cm == null) {
			cm = ControllerManagerImpl.getDefault();
		}
		sControllerManager = new LocalControllerManager(cm);
	}

	/**
	 * 
	 * 由{@link LocalControllerManager} 装饰{@link AbstractController},使
	 * {@link AbstractController}拥有管理controller的职责。
	 * 
	 * @author ChenJunQi.
	 * 
	 */
	protected static class LocalControllerManager extends
			ControllerManagerImpl.CompatModeWrapper {

		public LocalControllerManager(ControllerManager cm) {
			super(cm);
		}

		@Override
		public final void addController(AbstractController<?> c) {

			final AttachInfo ai = c.getAttachInfo();
			if (ai == null) {
				throw new BadTokenException(
						"Please ensure AttachInfo is attach.");
			}
			super.addController(c);
		}

		@Override
		public final void removeController(AbstractController<?> c) {
			super.removeController(c);
		}

		public final void removeControllerImmediate(AbstractController<?> c,
				boolean immediately) {
			final AttachInfo ai = c.getAttachInfo();
			if (ai == null) {
				throw new BadTokenException(
						"Please ensure AttachInfo is attach.");
			}
			if (!immediately)
				super.removeController(c);
			else
				super.removeControllerImmediate(c);
		}

		public boolean post(AbstractController<?> c, Runnable action) {
			final AttachInfo attachInfo = c.getAttachInfo();
			if (attachInfo != null) {
				attachInfo.mHandler.post(action);
			}
			// Assume that post will succeed later
			// 避免在attachInfo还未初始化的时候就执行了调用的保障措施
			ControllerRoot.getRunQueue().post(action);
			return true;
		}

		public boolean post(AbstractController<?>[] c, Runnable action) {
			if (c != null) {
				final int count = c.length;
				for (int i = 0; i < count; i++) {
					final AttachInfo attachInfo = c[i].getAttachInfo();
					if (attachInfo != null) {
						attachInfo.mHandler.post(action);
					}
					// Assume that post will succeed later
					ControllerRoot.getRunQueue().post(action);
				}
				return true;
			}
			return false;
		}

		public boolean postDelay(AbstractController<?> c, Runnable action,
				long delayMillis) {
			final AttachInfo attachInfo = c.getAttachInfo();
			if (attachInfo != null) {
				attachInfo.mHandler.postDelayed(action, delayMillis);
			}
			// Assume that post will succeed later
			ControllerRoot.getRunQueue().postDelayed(action, delayMillis);
			return true;
		}

		public boolean postDelay(AbstractController<?>[] c, Runnable action,
				long delayMillis) {
			if (c != null) {
				final int count = c.length;
				for (int i = 0; i < count; i++) {

					final AttachInfo attachInfo = c[i].getAttachInfo();
					if (attachInfo != null) {
						attachInfo.mHandler.postDelayed(action, delayMillis);
					}
					// Assume that post will succeed later
					ControllerRoot.getRunQueue().postDelayed(action,
							delayMillis);
				}
				return true;
			}
			return false;
		}

		public boolean removeCallbacks(AbstractController<?> c, Runnable action) {
			final AttachInfo attachInfo = c.getAttachInfo();
			if (attachInfo != null) {
				attachInfo.mHandler.removeCallbacks(action);
			}
			// Assume that post will succeed later
			ControllerRoot.getRunQueue().removeCallbacks(action);
			return true;
		}

		public boolean removeCallbacks(AbstractController<?>[] c,
				Runnable action) {

			if (c != null) {
				final int count = c.length;
				for (int i = 0; i < count; i++) {
					final AttachInfo attachInfo = c[i].getAttachInfo();
					if (attachInfo != null) {
						attachInfo.mHandler.removeCallbacks(action);
					}
					// Assume that post will succeed later
					ControllerRoot.getRunQueue().removeCallbacks(action);
				}
				return true;
			}
			return false;
		}
	}

}
