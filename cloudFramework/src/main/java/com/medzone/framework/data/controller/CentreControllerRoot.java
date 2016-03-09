/**
 * 
 */
package com.medzone.framework.data.controller;

import java.util.ArrayList;
import java.util.List;

import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.AttachInfo;
import com.medzone.framework.data.controller.AbstractController.LocalControllerManager;
import com.medzone.framework.data.controller.manager.ControllerManager;
import com.medzone.framework.data.controller.manager.ControllerManagerImpl;
import com.medzone.framework.module.BaseModule;

/**
 * @author ChenJunQi.
 * 
 */
public final class CentreControllerRoot {

	private final static String INVAILD_MODULE_ID = BaseModule.INVAILD_ID;
	private LocalControllerManager mLocalControllerManager;
	private static CentreControllerRoot instance = new CentreControllerRoot();

	public static CentreControllerRoot getInstance() {
		return instance;
	}

	private CentreControllerRoot() {
		ControllerManager cm = ControllerManagerImpl.getDefault();
		mLocalControllerManager = new LocalControllerManager(cm);
	}

	public void addController(AbstractController<?> c) {
		mLocalControllerManager.addController(c);
	}

	public void removeController(AbstractController<?> c) {
		mLocalControllerManager.removeController(c);
	}

	public void addControllerList(List<AbstractController<?>> list) {
		for (AbstractController<?> c : list) {
			mLocalControllerManager.addController(c);
		}
	}

	public void removeControllerList(List<AbstractController<?>> list) {
		for (AbstractController<?> c : list) {
			mLocalControllerManager.removeController(c);
		}
	}

	public void removeAllController() {
		List<AbstractController<?>> list = getAdditionalControllers();
		removeControllerList(list);
	}

	public void removeControllerByAttachInfo(AttachInfo mAttachInfo) {

		List<AbstractController<?>> list = getCollectByAttachInfo(mAttachInfo);
		removeControllerList(list);
	}

	public void removeControllerByAccountBeanToken(Account mAccountBeanToken) {

		List<AbstractController<?>> list = getCollectByAccountBeanToken(mAccountBeanToken);
		removeControllerList(list);
	}

	public void removeControllerByModuleToken(String moduleID) {

		List<AbstractController<?>> list = getCollectByModuleID(moduleID);
		removeControllerList(list);
	}

	/**
	 * 获取所有已经附加的Controller
	 */
	public List<AbstractController<?>> getAdditionalControllers() {
		List<AbstractController<?>> list = new ArrayList<AbstractController<?>>();
		AbstractController<?>[] absArray = mLocalControllerManager
				.getAdditionalControllers();
		final int count = absArray != null ? absArray.length : 0;
		for (int i = 0; i < count; i++) {
			list.add(absArray[i]);
		}
		return list;
	}

	/**
	 * 获取指定AttachInfo的控制器
	 */
	public List<AbstractController<?>> getCollectByAttachInfo(
			AttachInfo mAttachInfo) {

		if (mAttachInfo == null) {
			throw new IllegalArgumentException("mAttachInfo can not empty.");
		}
		List<AbstractController<?>> list = new ArrayList<AbstractController<?>>();
		AbstractController<?>[] absArray = mLocalControllerManager
				.getAdditionalControllers();
		final int count = absArray != null ? absArray.length : 0;
		for (int i = 0; i < count; i++) {
			if (absArray[i].getAttachInfo() == mAttachInfo) {
				list.add(absArray[i]);
			}
		}
		return list;
	}

	/**
	 * 获取指定AccountBean的控制器
	 */
	public List<AbstractController<?>> getCollectByAccountBeanToken(
			Account mAccountBeanToken) {
		if (mAccountBeanToken == null) {
			throw new IllegalArgumentException(
					"mAccountBeanToken can not empty.");
		}
		List<AbstractController<?>> list = new ArrayList<AbstractController<?>>();
		AbstractController<?>[] absArray = mLocalControllerManager
				.getAdditionalControllers();
		final int count = absArray != null ? absArray.length : 0;
		for (int i = 0; i < count; i++) {
			if (absArray[i].getAttachInfo().mAccount == mAccountBeanToken) {
				list.add(absArray[i]);
			}
		}
		return list;
	}

	/**
	 * 获取指定Module的控制器
	 */
	public List<AbstractController<?>> getCollectByModuleID(String moduleID) {
		if (moduleID == INVAILD_MODULE_ID) {
			throw new IllegalArgumentException(
					"mModuleToken == INVAILD_MODULE_ID");
		}
		List<AbstractController<?>> list = new ArrayList<AbstractController<?>>();
		AbstractController<?>[] absArray = mLocalControllerManager
				.getAdditionalControllers();
		final int count = absArray != null ? absArray.length : 0;
		for (int i = 0; i < count; i++) {
			if (absArray[i].getAttachInfo().mModuleId == moduleID) {
				list.add(absArray[i]);
			}
		}
		return list;
	}

	// public boolean postAll(Runnable action) {
	// List<AbstractController<?>> list = getAdditionalControllers();
	// for (AbstractController<?> c : list) {
	// LocalControllerManager lcm = (LocalControllerManager) c
	// .getControllerManager();
	// lcm.post(c, action);
	// }
	// return true;
	// }
	//
	// public boolean postCollectByAttachInfo(AttachInfo mAttachInfo,
	// Runnable action) {
	// List<AbstractController<?>> list = getCollectByAttachInfo(mAttachInfo);
	// for (AbstractController<?> c : list) {
	// LocalControllerManager lcm = (LocalControllerManager) c
	// .getControllerManager();
	// lcm.post(c, action);
	// }
	// return true;
	// }
	//
	// public boolean postCollectByAttachInfoDelay(AttachInfo mAttachInfo,
	// Runnable action, long delayMillis) {
	// List<AbstractController<?>> list = getCollectByAttachInfo(mAttachInfo);
	// for (AbstractController<?> c : list) {
	// LocalControllerManager lcm = (LocalControllerManager) c
	// .getControllerManager();
	// lcm.postDelay(c, action, delayMillis);
	// }
	// return true;
	// }
	//
	// public boolean postCollectByAccountBeanToken(Account mAccountBeanToken,
	// Runnable action) {
	// List<AbstractController<?>> list =
	// getCollectByAccountBeanToken(mAccountBeanToken);
	// for (AbstractController<?> c : list) {
	// LocalControllerManager lcm = (LocalControllerManager) c
	// .getControllerManager();
	// Log.v("debug", "处理者：>>>" + c.getClass().getSimpleName());
	// lcm.post(c, action);
	// }
	// return true;
	// }
	//
	// public boolean postCollectByAccountBeanTokenDelay(
	// Account mAccountBeanToken, Runnable action, long delayMillis) {
	// List<AbstractController<?>> list =
	// getCollectByAccountBeanToken(mAccountBeanToken);
	// for (AbstractController<?> c : list) {
	// LocalControllerManager lcm = (LocalControllerManager) c
	// .getControllerManager();
	// lcm.postDelay(c, action, delayMillis);
	// }
	// return true;
	// }
	//
	// public boolean postCollectByModuleToken(String moduleID, Runnable action)
	// {
	//
	// List<AbstractController<?>> list = getCollectByModuleID(moduleID);
	// for (AbstractController<?> c : list) {
	// LocalControllerManager lcm = (LocalControllerManager) c
	// .getControllerManager();
	// lcm.post(c, action);
	// }
	// return true;
	// }
	//
	// public boolean postCollectByModuleTokenDelay(String moduleID,
	// Runnable action, long delayMillis) {
	//
	// List<AbstractController<?>> list = getCollectByModuleID(moduleID);
	// for (AbstractController<?> c : list) {
	// LocalControllerManager lcm = (LocalControllerManager) c
	// .getControllerManager();
	// lcm.postDelay(c, action, delayMillis);
	// }
	// return true;
	// }
	//
	// public boolean removeAll(Runnable action) {
	// List<AbstractController<?>> list = getAdditionalControllers();
	// for (AbstractController<?> c : list) {
	// LocalControllerManager lcm = (LocalControllerManager) c
	// .getControllerManager();
	// lcm.removeCallbacks(c, action);
	// }
	// return true;
	// }
	//
	// public boolean removeCallbacksCollectByAttachInfo(AttachInfo mAttachInfo,
	// Runnable action) {
	// List<AbstractController<?>> list = getCollectByAttachInfo(mAttachInfo);
	// for (AbstractController<?> c : list) {
	// LocalControllerManager lcm = (LocalControllerManager) c
	// .getControllerManager();
	// lcm.removeCallbacks(c, action);
	// }
	// return true;
	// }
	//
	// public boolean removeCallbacksCollectByAccountBeanToken(
	// Account mAccountBeanToken, Runnable action) {
	// List<AbstractController<?>> list =
	// getCollectByAccountBeanToken(mAccountBeanToken);
	// for (AbstractController<?> c : list) {
	// LocalControllerManager lcm = (LocalControllerManager) c
	// .getControllerManager();
	// lcm.removeCallbacks(c, action);
	// }
	// return true;
	// }
	//
	// public boolean removeCallbacksCollectByModuleToken(String moduleID,
	// Runnable action) {
	//
	// List<AbstractController<?>> list = getCollectByModuleID(moduleID);
	// for (AbstractController<?> c : list) {
	// LocalControllerManager lcm = (LocalControllerManager) c
	// .getControllerManager();
	// lcm.removeCallbacks(c, action);
	// }
	// return true;
	// }

}
