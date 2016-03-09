/**
 * 
 */
package com.medzone.framework.timeout;

import android.content.Context;
import android.util.SparseArray;

import com.medzone.framework.util.NetUtil;

/**
 * @author ChenJunQi.
 * 
 */
public class BaseOverdue implements IOverdue {

	/**
	 * No cache else WIFI is connected.
	 */
	private long WIFI = 0;
	private long MOBILE_EDGE = 1000 * 60 * 5;// 2G
	private long MOBILE_GPRS = 1000 * 60 * 3;// 2.5G
	private long MOBILE_3G = 1000 * 60 * 2;// 3G

	private SparseArray<Long> map = new SparseArray<Long>();

	public BaseOverdue() {
		init();
	}

	protected void init() {
		map.put(NetUtil.NETTYPE_NONE, (long) -1);
		map.put(NetUtil.NETTYPE_WIFI, WIFI);
		map.put(NetUtil.NETTYPE_MOBILE_GPRS, MOBILE_EDGE);
		map.put(NetUtil.NETTYPE_MOBILE_EDGE, MOBILE_GPRS);
		map.put(NetUtil.NETTYPE_MOBILE_3G, MOBILE_3G);
	}

	@Override
	public void updateOverdue(int key, long value) {
		if (key < NetUtil.NETTYPE_NONE && key > NetUtil.NETTYPE_MOBILE_3G) {
			System.err
					.println("invalid key is coming,the correct key range start at "
							+ NetUtil.NETTYPE_NONE
							+ " and end of "
							+ NetUtil.NETTYPE_MOBILE_3G);
			return;
		}
		map.put(key, value);
	}

	@Override
	public long getCurrentOverdueValue(Context context) {
		int state = NetUtil.getCurrentNetType(context);
		if (state == NetUtil.NETTYPE_UNKNOW) {
			state = NetUtil.NETTYPE_WIFI;// Default is WIFI.
		}
		return map.get(state);
	}

	@Override
	public boolean isTimeOut(Context context, long lastUpdateTime) {

		int state = NetUtil.getCurrentNetType(context);

		switch (state) {
		case NetUtil.NETTYPE_NONE:
			return false;
		case NetUtil.NETTYPE_WIFI:
			return true;
		default:
			long timeoutInterval = getCurrentOverdueValue(context);
			long currentTimeMillis = System.currentTimeMillis();
			if (currentTimeMillis - lastUpdateTime >= timeoutInterval) {
				return true;
			} else {
				return false;
			}
		}
	}

}
