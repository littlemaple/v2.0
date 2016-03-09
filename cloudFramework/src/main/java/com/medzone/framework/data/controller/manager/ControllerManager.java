/**
 * 
 */
package com.medzone.framework.data.controller.manager;

import com.medzone.framework.data.controller.AbstractController;

/**
 * @author ChenJunQi.
 * 
 * 
 */
public interface ControllerManager {

	void addController(AbstractController<?> c);

	void removeController(AbstractController<?> c);

	void removeControllerImmediate(AbstractController<?> c);

	public static class BadTokenException extends RuntimeException {

		private static final long serialVersionUID = 527690517424095372L;

		public BadTokenException() {
		}

		public BadTokenException(String name) {
			super(name);
		}
	}
}
