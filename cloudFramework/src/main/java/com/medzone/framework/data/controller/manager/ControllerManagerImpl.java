/**
 * 
 */
package com.medzone.framework.data.controller.manager;

import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.AttachInfo;
import com.medzone.framework.data.controller.AbstractController;
import com.medzone.framework.data.controller.AbstractController.AttachEvent;

/**
 * 
 * ControllerManagerImpl类，针对controller的管理最终都是在这里进行处理。
 * 
 * @author ChenJunQi.
 * 
 */
public final class ControllerManagerImpl implements ControllerManager {

	private static ControllerManagerImpl sDefault = new ControllerManagerImpl();
	private AbstractController<?>[] sAbstractControllerArray;

	public static ControllerManagerImpl getDefault() {
		return sDefault;
	}

	public AbstractController<?>[] getAdditionalControllers() {
		return sAbstractControllerArray;
	}

	@Override
	public void addController(AbstractController<?> c) {
		addController(c, false);
	}

	/***
	 * 
	 * 
	 * @param c
	 * @param isDuplicateNest
	 * 
	 *            <p>
	 *            不同于Window的添加模式，这里Controller默认情况下一个帐号下只允许添加一次。
	 *            这里预留了nest字段用于兼容未来，允许controller被多次添加的情景。 当前默认不允许重复添加(false)
	 *            </p>
	 * 
	 */
	public void addController(AbstractController<?> c, boolean isDuplicateNest) {

		final AbstractController<?> controller = c;
		final AttachInfo attachInfo = controller.getAttachInfo();

		synchronized (this) {
			int index = findControllerLocked(controller, false);
			if (index >= 0) {
				if (!isDuplicateNest) {
					Log.w("Initialization is not allowed to repeat!"
							+ controller.getClass().getSimpleName());
				}
				return;
			}

			if (sAbstractControllerArray == null) {
				index = 1;
				sAbstractControllerArray = new AbstractController[index];
			} else {
				index = sAbstractControllerArray.length + 1;
				Object[] old = sAbstractControllerArray;
				sAbstractControllerArray = new AbstractController[index];
				System.arraycopy(old, 0, sAbstractControllerArray, 0, index - 1);
			}
			index--;
			attachInfo.mNestCount++;
			sAbstractControllerArray[index] = controller;
		}
		// Do something after controller is registered in manager.
		controller.dispatchAttachedToController(AttachEvent.ATTACH);
		Log.i("capacity of the registry >>>" + sAbstractControllerArray.length);
		Log.i("controller is registered >>>" + controller.getClass().getName());
	}

	/**
	 * 移除controller对象，如果被移除的对象不存在，则会抛出异常。
	 */
	@Override
	public void removeController(AbstractController<?> c) {
		final AbstractController<?> controller = c;
		synchronized (this) {
			int index = findControllerLocked(controller, true);
			AbstractController<?> removeController = removeControllerLocked(index);
			if (controller == removeController) {
				// Do something after controller is unregistered in manager.
				controller.dispatchAttachedToController(AttachEvent.DETACH);
				Log.i("Capacity of the registry  >>>"
						+ sAbstractControllerArray.length);
				Log.i("controller is registered >>>"
						+ controller.getClass().getName());
				return;
			}
			throw new BadTokenException(
					"controller to be removed is unregistered.");
		}
	}

	/**
	 * 立即移除controller对象，如果被移除的对象不存在，则会抛出异常。 相较于
	 * {@link #removeController(AbstractController)}他会立刻触发
	 * {@link AbstractController}中的onDetach()方法。
	 */
	@Override
	public void removeControllerImmediate(AbstractController<?> c) {
		final AbstractController<?> controller = c;

		synchronized (this) {
			int index = findControllerLocked(controller, true);
			AbstractController<?> root = sAbstractControllerArray[index];

			root.getAttachInfo().mNestCount = 0;
			controller.dispatchAttachedToController(AttachEvent.DETACH);

			final int count = sAbstractControllerArray.length;

			AbstractController<?>[] tmpArray = new AbstractController[count - 1];
			removeItem(tmpArray, sAbstractControllerArray, index);
			sAbstractControllerArray = tmpArray;

			if (controller == root) {
				Log.i("Capacity of the registry  >>>"
						+ sAbstractControllerArray.length);
				Log.i("controller is registered >>>"
						+ controller.getClass().getName());
				return;
			}

			throw new BadTokenException(
					"controller to be removed is unregistered.");
		}

	}

	/**
	 * 
	 * 
	 * 
	 * @param c
	 *            controller
	 * @param required
	 *            若标识为true，若找不到则将抛出{@link #IllegalArgumentException}异常
	 * @return controller在引用列表中的位置。当required为false情况下，无返回-1，否则抛出异常。
	 */
	private int findControllerLocked(AbstractController<?> c, boolean required) {
		synchronized (this) {
			final AttachInfo attachInfo = c.getAttachInfo();
			final int count = sAbstractControllerArray != null ? sAbstractControllerArray.length
					: 0;
			for (int i = 0; i < count; i++) {
				if (sAbstractControllerArray[i] == c) {
					Log.i(c.getClass().getName()
							+ "in the controller reference list.");
					return i;
				}

				if (sAbstractControllerArray[i].getAttachInfo().mModuleId == (attachInfo.mModuleId)
						& sAbstractControllerArray[i].getAttachInfo().mAccount == attachInfo.mAccount) {
					Log.i(c.getClass().getName()
							+ "is not in the controller reference list,but the same value object existed.");
					return i;
				}
			}
			if (required) {
				throw new BadTokenException(
						"for operating the controller has not yet been attached!");
			}
			return -1;
		}
	}

	private AbstractController<?> removeControllerLocked(int index) {

		final AbstractController<?> controller = sAbstractControllerArray[index];
		final AttachInfo attachInfo = controller.getAttachInfo();

		if (--attachInfo.mNestCount > 0) {
			Log.w("detected repeat add >>>" + controller.getClass().getName()
					+ "additional number is:" + attachInfo.mNestCount);
		}

		final int count = sAbstractControllerArray.length;

		AbstractController<?>[] tmpArray = new AbstractController[count - 1];
		removeItem(tmpArray, sAbstractControllerArray, index);
		sAbstractControllerArray = tmpArray;
		return controller;

	}

	private static void removeItem(Object[] dst, Object[] src, int index) {
		if (dst.length > 0) {
			if (index > 0) {
				System.arraycopy(src, 0, dst, 0, index);
			}
			if (index < dst.length) {
				System.arraycopy(src, index + 1, dst, index, src.length - index
						- 1);
			}
		}
	}

	/**
	 * 包装器，由于将管理的职责下发给{@link AbstractController}
	 * 
	 * @author ChenJunQi.
	 * 
	 */
	public static class CompatModeWrapper implements ControllerManager {
		private final ControllerManagerImpl mControllerManager;

		public CompatModeWrapper(ControllerManager cm) {
			mControllerManager = cm instanceof CompatModeWrapper ? ((CompatModeWrapper) cm).mControllerManager
					: (ControllerManagerImpl) cm;
		}

		@Override
		public void addController(AbstractController<?> c) {
			mControllerManager.addController(c);
		}

		@Override
		public void removeController(AbstractController<?> c) {
			mControllerManager.removeController(c);
		}

		@Override
		public void removeControllerImmediate(AbstractController<?> c) {
			mControllerManager.removeControllerImmediate(c);
		}

		public AbstractController<?>[] getAdditionalControllers() {
			return mControllerManager.getAdditionalControllers();
		}
	}

}
