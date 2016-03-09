package com.medzone.cloud;

public final class Constants {

	private Constants() {

	}

	public static final String OFFICAL_WEBSITE = "http://www.mcloudlife.com/";
	public static final String PUBLISH_DATE_FOR_TESTOR = "\n当前版本：v2.0.0.32";
	public static final String PUBLISH_DATE = "\n当前版本：v2.0.0";
	public static final int SPLASH_TIME = 1000;

	public static final int DURING_PREGNANCY = 280;
	// The client support API level
	public static final int LOCAL_SERVER_API_HIGH_VERSION = 1;
	public static final int LOCAL_SERVER_API_LOW_VERSION = 0;
	public static final int LOCAL_SERVER_API_CHANGES = 0;

	// public static final String ACTIVITY_COLLECT_REGISTER = "regsiter";

	/**
	 * 假的群成员，通常被作为占位符，用于定义其他事件
	 */
	public static final String OWNER_MANAGER_TAG = "fakeMember";

	public static final String INDICATOR_HOME = "home";
	public static final String INDICATOR_GROUP = "group";
	public static final String INDICATOR_MEASURE = "measure";
	public static final String INDICATOR_SERVICE = "service";
	public static final String INDICATOR_SETTING = "setting";

	public final static String DEVICE_DATA = "device_data";
	public final static String BLUETOOTH_DEVICE = "bluetooth_device";
	public final static String AUDIO_DEVICE = "audio_device";
	public final static String mCloud_P = "mCloud-P";
	public final static String mCloud_O = "mCloud-O";
	public final static String mCloud_S = "mCloud-S";
	public final static String mCloud_ET = "mCloud-ET";
	public final static String MEASURE = "measure";
	public final static String INPUT = "input";

	// Default Blood Sugar
	public static final String BLOOD_SUGAR = "blood_sugar";
	public static final float BLOOD_SUGAR_LOW = 2f;
	public static final float BLOOD_SUGAR_HIGH = 8f;

	// Default Ear Temperature
	public static final String EAR_TEMPERATURE = "ear_temperature";
	public static final float EAR_TEMPERATURE_LOW = 32f;
	public static final float EAR_TEMPERATURE_HIGH = 42.3f;
	public static final float EAR_TEMPERATURE_DEFAULT = 37f;

	// Default Oximeter
	public static final String BLOOD_OXYGEN = "blood_oxygen";
	public static final int OXIMETRY_LOW = 35;
	public static final int OXIMETRY_HIGH = 99;
	public static final int OXIMETRY_DEFAULT_HIGH = 95;
	public static final int OXIMETRY_RATE = 60;

	// Default Heart Rate
	public static final String HEART_RATE = "heart_rate";
	public static final int HEART_RATE_LOW = 30;
	public static final int HEART_RATE_HIGH = 200;

	// Default blood pressure rate
	public static final int BLOODPRESSURE_DEFAULT_RATE = 60;
	// Default Systolic Pressure
	public static final String SYSTOLIC_PRESSURE = "systolic_pressure";
	public static final int SYSTOLIC_PRESSURE_LOW = 30;
	public static final int SYSTOLIC_PRESSURE_HIGH = 300;
	public static final int SYSTOLIC_PRESSURE_DEFAULT_HIGH = 120;

	// Default Diastolic Pressure
	public static final String DIASTOLIC_PRESSURE = "diastolic_pressure";
	public static final int DIASTOLIC_PRESSURE_LOW = 30;
	public static final int DIASTOLIC_PRESSURE_HIGH = 300;
	public static final int DIASTOLIC_PRESSURE_DEFAULT_LOW = 80;

	// Error Type
	public static final int ERR_POPUP_BT_OPEN = 101;
	public static final int ERR_POPUP_BT_DISCOVER = 102;
	public static final int ERR_POPUP_BT_CONNECT = 103;
	public static final int ERR_POPUP_BT_SOCKET = 104;
	public static final int ERR_POPUP_BP_CMDOUTTIME_SHORT = 105;
	public static final int ERR_POPUP_BP_CMDOUTTIME_LONG = 106;
	public static final int ERR_POPUP_BP_TEST_ERROR = 107;
	public static final int ERR_POPUP_BP_BAT_LOW = 108;
	public static final int ERR_POPUP_BP_GAS_CHARGE = 109;
	public static final int ERR_POPUP_OX_CMDOUTTIME_SHORT = 110;
	public static final int ERR_POPUP_OX_CMDOUTTIME_LONG = 111;
	public static final int ERR_POPUP_OX_FINGER_OUT = 112;
	public static final int ERR_POPUP_OX_BAT_LOW = 113;
	public static final int ERR_POPUP_OX_FINGER_BAT = 114;
	public static final int ERR_POPUP_ET_CONNECT = 115;
	public static final int ERR_POPUP_ET_SENSOR = 116;
	public static final int ERR_POPUP_ET_EEPROM = 117;
	public static final int ERR_POPUP_ET_RESULT_HIGH = 118;
	public static final int ERR_POPUP_ET_RESULT_LOW = 119;
	public static final int ERR_POPUP_ET_ENVIRONMENT_HIGH = 120;
	public static final int ERR_POPUP_ET_ENVIRONMENT_LOW = 121;
	public static final int ERR_POPUP_ET_BAT_LOW = 122;

	public static final boolean DEBUG = true;

	public static final int CODE_DELAY_COUNT = 60;
	public static final int DIALOG_DISMISS_TIME = 2000;

	public static final int TYPE_NORMAL = 0; // 普通文本消息
	public static final int TYPE_INVITE_GROUP = 1; // 邀请入群
	public static final int TYPE_ACCEPT_GROUP = 2; // 同意入群
	public static final int TYPE_REFUSE_GROUP = 3; // 拒绝入群
	public static final int TYPE_KICK_GROUP = 4; // 被踢出群
	public static final int TYPE_QUIT_GROUP = 5; // 用户退群
	public static final int TYPE_DISMISS_GROUP = 6; // 解散群

	// 新消息通知
	public static final int ALERT_POPUP = 0; // 弹出提醒
	public static final int ALERT_COUNT = 1; // 只显示数量
	public static final int ALERT_QUIET = 2; // 静默不提示

	public static final int HEIGHT_DEFAULT = 160;
	public static final int HEIGHT_MIN = 10;
	public static final int HEIGHT_MAX = 240;

	public static final int WEIGHT_DEFAULT = 60;
	public static final int WEIGHT_MIN = 2;
	public static final int WEIGHT_MAX = 150;

	public static final long millisecondOfDay = 86400000;
	public static final int PRE_PRODUCTION_PERIOD = 280;

	public static final String UNIT_ch_CM = "厘米";
	public static final String UNIT_ch_KG = "公斤";
	public static final String UNIT_ch_DAY = "天";

	// public static final String UNIT_en_CM = "cm";
	// public static final String UNIT_en_KG = "kg";
	// public static final String UNIT_en_DAY = "day";

	// 临时保存key
	public static final String TEMPORARYDATA_KEY_SHARE_CARRIED = "share_carried";
	public static final String TEMPORARYDATA_KEY_SHARE_TYPE = "share_type";
	public static final String TEMPORARYDATA_KEY_MEASURE_TYPE = "measure_type";
	public static final String TEMPORARYDATA_KEY_TEST_ACCOUNT = "test_account";
	public static final String TEMPORARYDATA_KEY_VIEW_ACCOUNT = "view_account";
	public static final String TEMPORARYDATA_KEY_VIEW_GROUP_MEMBER = "view_group_member";
	public static final String TEMPORARYDATA_KEY_MEMBER_ID = "member_id";
	public static final String TEMPORARYDATA_KEY_URL = "webview_url";
	public static final String TEMPORARYDATA_KEY_TITLE = "webview_title";

	// pressure 、oygen、et ：key
	public static final int VALUE_RECENT = 0;
	public static final int VALUE_MONTH = 1;

	public static final String KEY_ALL_COUNT = "all_count";
	public static final String KEY_EXCEPTION_COUNT = "exception_count";
	public static final String KEY_COUNT = "count";
	public static final String KEY_MONTH = "month";
	public static final String KEY_ABNORMAL = "abnormal";

	// measure
	public static final String WHETHER_FOR_A_LONG_TIME = "long";
	public static final String OXYGEN = "oxygen";
	public static final String RATE = "rate";
	public static final String HIGH_PRESSURE = "high";
	public static final String LOW_PRESSURE = "low";
	public static final String TEMPERATURE = "temperatue";

	public final static int CONNECT_FLAG_START = 0;
	public final static int CONNECT_FLAG_IN = 1;
	public final static int CONNECT_FLAG_END = 2;
	public final static int SLIDING_FLAG_INIT = 3;
	public final static int OPEN_DEVICE_FLAG_INIT = 5;
	public static final int MEASURE_PENDING = 0;
	public static final int MEASURE_COMPLETE = 1;
	public static final int MEASURE_TIMEOUT = 2;
	public static final int MEASURE_STATE = 3;

	// measure animation
	public static final int MEASURE_TIME_ONE = 1600;
	public static final int MEASURE_TIME_TWO = 800;

	// setting
	public static final int MAX_EMS_IDCARD = 18;
	public static final int MAX_EMS_NAME = 15;
	public static final int MAX_EMS_EMAIL = 64;
	public static final int MAX_EMS_ADDRESS = 110;
	public static final int MAX_EMS_PASSWORD = 16;
	public static final int MAX_EMS_PHONE = 11;

}
