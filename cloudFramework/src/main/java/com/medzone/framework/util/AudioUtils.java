package com.medzone.framework.util;

public class AudioUtils {

	/**
	 * DATA：传递数据
	 */
	public static final String DATA = "DATA";

	/**
	 * Action：关闭后台Service
	 */
	public static final String AUDIO_ACTION_STOP_SERVICE = "AUDIO_ACTION_STOP_SERVICE";
	/**
	 * Action：耳机为插入
	 */
	public static final String HEADEST_NOT_INSERT = "HEADEST_NOT_INSERT";
	/**
	 * Action：耳机插入
	 */
	public static final String HEADEST_IS_INSERT = "HEADEST_IS_INSERT";
	/**
	 * Action：监测耳机插入
	 */
	public static final String HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
	/**
	 * Action：音频搜索
	 */
	public static final String ACTION_AUDIO_DETECTION = "ACTION_AUDIO_DETECTION";
	/**
	 * Action：检测音频连接
	 */
	public static final String ACTION_AUDIO_CONNECT_DETECTION = "ACTION_AUDIO_CONNECT_DETECTION";

	/**
	 * Action：音频连接失败
	 */
	public static final String ACTION_AUDIO_CONNECT_FAILURE = "ACTION_AUDIO_CONNECT_FAILURE";

	/**
	 * Action：音频连接成功
	 */
	public static final String ACTION_AUDIO_CONNECT_SUCCESS = "ACTION_AUDIO_CONNECT_SUCCESS";

	/**
	 * Action：检测终端
	 */
	public static final String ACTION_QUERY_AUDIO = "ACTION_QUERY_AUDIO";
	/**
	 * Action：获取数据
	 */
	public static final String ACTION_GET_DATA = "ACTION_GET_DATA";
	/**
	 * Action：通讯异常
	 */
	public static final String ACTION_COMMON_ERROR = "ACTION_COMMON_ERROR";
	/**
	 * @author hjs 采样模式：每bit32采样点，或每bit16采样点
	 */
	public final static int SAMPLES_PER_BIT_32 = 32;
	public final static int SHORT_PULSE_WIDTH_32 = 24;
	public final static int LONG_PULSE_WIDTH_32 = 80;

	public final static int SAMPLES_PER_BIT_16 = 16;
	public final static int SHORT_PULSE_WIDTH_16 = 12;
	public final static int LONG_PULSE_WIDTH_16 = 40;

	/**
	 * @author hjs 通信类型：查询终端、开始测量、结束检测
	 */
	public final static int COMMUNICATE_TYPE_QUERY = 31;
	public final static int COMMUNICATE_TYPE_START = 32;
	public final static int COMMUNICATE_TYPE_ADJUST = 34;

	/**
	 * @author hjs 发送命令类型:查询终端ID、开始测量、校准检测
	 */
	public final static int[] MEASURE_CMD_QUERY_ID = { 0xAA, 0xAA, 0xAA, 0xAA,
			0xAA, 0xAA, 0x7E, 0xAA, 0xAA, 0xAA, 0xAA, 0xAA, 0xAA, 0xAA, 0xAB,
			0x01, 0x31, 0xCE, 0x7E, 0x7E };
	public final static int[] MEASURE_CMD_START = { 0xAA, 0xAA, 0xAA, 0xAA,
			0xAA, 0xAA, 0x7E, 0xAA, 0xAA, 0xAA, 0xAA, 0xAA, 0xAA, 0xAA, 0xAB,
			0x01, 0x32, 0xCD, 0x7E, 0x7E };
	public final static int[] MEASURE_CMD_ADJUST = { 0xAA, 0xAA, 0xAA, 0xAA,
			0xAA, 0xAA, 0x7E, 0xAA, 0xAA, 0xAA, 0xAA, 0xAA, 0xAA, 0xAA, 0xAB,
			0x01, 0x34, 0xCB, 0x7E, 0x7E };

	/**
	 * @author hjs 消息类型==> 指令发送完毕 接收的数据、接收完成、接收错误
	 */
	public final static int MSG_SEND_CMD_OVER = 71;
	public final static int MSG_RECEIVE_DATA = 72;
	public final static int MSG_RECEIVE_DONE = 73;
	public final static int MSG_DECODE_ERROR = 74;
	public final static int MSG_TOAST = 77;
	public final static int MSG_DEBUG = 78;
	public final static int MSG_MESSAGE = 80;

}
