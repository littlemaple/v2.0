package com.medzone.cloud.network;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.medzone.cloud.CloudApplication;
import com.medzone.cloud.CloudApplicationPreference;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.imp.Account;
import com.medzone.framework.data.bean.imp.Account.Gender;
import com.medzone.framework.data.bean.imp.Group;
import com.medzone.framework.module.ModuleSpecification;
import com.medzone.framework.network.NetworkClientManager;
import com.medzone.framework.network.NetworkClientManagerHelper;
import com.medzone.framework.task.BaseResult;
import com.medzone.framework.util.MD5Util;
import com.medzone.framework.util.TimeUtils;

public final class NetworkClient {

	/**
	 * 沙箱环境
	 */
	private static final String SANDBOX_URI = "http://d11.sunbo.com/~hightman/mhealth/index.php";
	/**
	 * 生产环境
	 */
	private static final String PRODUCTION_URL = "http://v2.mcloudlife.com";

	public static final String VIEW_MEASURE_DATA_URI = "/app/peekIndex?syncid=%d&access_token=%s";

	private static final String RESOURCE_VERSION = "/api/version";
	private static final String RESOURCE_VERIFY = "/api/verify";
	private static final String RESOURCE_REGISTER = "/api/register";
	private static final String RESOURCE_RESETPWD = "/api/passwdReset";
	private static final String RESOURCE_LOGIN = "/api/login";
	private static final String RESOURCE_LOGOUT = "/api/logout";

	private static final String RESOURCE_PROFILE = "/api/profile";
	private static final String RESOURCE_PROFILE_EDIT = "/api/profileEdit";

	/* contact api */
	private static final String RESOURCE_CONTACT_LIST = "/api/contactList";
	private static final String RESOURCE_CONTACT_ADD = "/api/contactAdd";
	private static final String RESOURCE_CONTACT_DEL = "/api/contactDel";
	private static final String RESOURCE_CONTACT_EDIT = "/api/contactEdit";

	/* record api */
	private static final String RESOURCE_RECORD_UPLOAD = "/api/recordUpload";
	private static final String RESOURCE_RECORD_DOWNLOAD = "/api/recordDownload";
	private static final String RESOURCE_RECORD_EDIT = "/api/recordEdit";
	private static final String RESOURCE_RECORD_DEL = "/api/recordDel";
	private static final String RESOURCE_RECORD_SHAREURL = "/api/recordShareUrl";

	/* module api */
	private static final String RESOURCE_MODULE_CONFIG = "/api/appModule";

	/* user api */
	private static final String RESOURCE_USER_QUERY = "/api/userQuery";

	/* message api */
	private static final String RESOURCE_MESSAGE_COUNT = "/api/messageCount";
	private static final String RESOURCE_MESSAGE_LIST = "/api/messageList";
	private static final String RESOURCE_MESSAGE_MARK = "/api/messageMark";
	private static final String RESOURCE_MESSAGE_RESPONSE = "/api/messageResponse";

	/* event api */
	private static final String RESOURCE_EVENT_LIST = "/api/eventList";

	/* group api */
	private static final String RESOURCE_GROUP_ADD = "/api/groupAdd";
	private static final String RESOURCE_GROUP_EDIT = "/api/groupEdit";
	private static final String RESOURCE_GROUP_DEL = "/api/groupDel";
	private static final String RESOURCE_GROUP_LIST = "/api/groupList";
	private static final String RESOURCE_GROUP_VIEW = "/api/groupView";
	private static final String RESOURCE_GROUP_AUTHORIZED = "/api/groupAuthzList";

	private static final String RESOURCE_GROUP_MEMBER_ADD = "/api/groupMemberAdd";
	private static final String RESOURCE_GROUP_MEMBER_EDIT = "/api/groupMemberEdit";
	private static final String RESOURCE_GROUP_MEMBER_PERM = "/api/groupMemberPerm";
	private static final String RESOURCE_GROUP_MEMBER_DEL = "/api/groupMemberDel";
	private static final String RESOURCE_GROUP_MEMBER_LIST = "/api/groupMemberList";
	private static final String RESOURCE_GROUP_MEMBER_CARE_LIST = "/api/groupCareList";
	private static final String RESOURCE_GROUP_MEMBER_TEST_LIST = "/api/groupTestList";

	private static final String RESOURCE_GROUP_MESSAGE_COUNT = "/api/groupMessageCount";
	private static final String RESOURCE_GROUP_MESSAGE_LIST = "/api/groupMessageList";
	private static final String RESOURCE_GROUP_MESSAGE_POST = "/api/groupMessagePost";

	/* rule api */
	private static final String RESOURCE_RULE_LIST = "/api/ruleList";
	private static final String RESOURCE_RULE_MATCH = "/api/ruleMatch";

	private static NetworkClient instance;

	// default value
	private static boolean defSandBox = false;

	public static String getViewDataURL(int syncid, String accesstoken) {

		String uri = String.format(VIEW_MEASURE_DATA_URI, syncid, accesstoken);

		return getCurUrl(CloudApplicationPreference.isSandBox(false)).concat(
				uri);
	}

	public static boolean isSandBox() {
		return CloudApplicationPreference.isSandBox(defSandBox);
	}

	public static void setSandBox(boolean isSandBox) {

		// 这里CurUrl还是切换前的URL
		NetworkClientManagerHelper.discardClient(getCurUrl(!isSandBox));
		CloudApplicationPreference.saveSandBox(isSandBox);
		// 这里是切换之后的URL
		if (!NetworkClientManagerHelper.isServerReady(getCurUrl(isSandBox))) {
			NetworkClientManagerHelper.readyClient(getCurUrl(isSandBox));
		}
		Log.w(getCurUrl(isSandBox));
	}

	public static String getCurUrl(boolean isSandBox) {
		if (isSandBox) {
			return SANDBOX_URI;
		} else {
			return PRODUCTION_URL;
		}
	}

	private NetworkClient(Context context) {
		NetworkClientManagerHelper.init(context);
		NetworkClientManagerHelper.readyClient(getCurUrl(isSandBox()));
	}

	public static void init(Context context) {
		if (instance == null) {
			instance = new NetworkClient(context);
		}
	}

	public static void uninit() {
		NetworkClientManagerHelper.discardClient(getCurUrl(isSandBox()));
		instance = null;
	}

	public static NetworkClient getInstance() {
		if (instance == null) {
			init(CloudApplication.getInstance().getApplicationContext());
		}
		if (!NetworkClientManagerHelper.isServerReady(getCurUrl(isSandBox()))) {
			NetworkClientManagerHelper.readyClient(getCurUrl(isSandBox()));
		}
		return instance;
	}

	/**
	 * 
	 * 
	 */
	public BaseResult getVersion() {
		return NetworkClientManager.getInstance().call(RESOURCE_VERSION, null);
	}

	public BaseResult doLogin(Account account) {

		JSONObject params = new JSONObject();
		try {

			String target;
			if (!TextUtils.isEmpty(account.getPhone())) {
				target = account.getPhone();
			} else {
				target = account.getEmail();
			}
			params.put("target", target);
			params.put("password", MD5Util.encrypt(account.getPassword()));
			if (!TextUtils.isEmpty(account.getAccessToken())) {
				params.put("token_old", account.getAccessToken());
			}
			if (!TextUtils.isEmpty(account.getPushID())) {
				params.put("push_id", account.getPushID());
			}

			return NetworkClientManager.getInstance().call(RESOURCE_LOGIN,
					params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BaseResult doLogout(String accessToken) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);
			return NetworkClientManager.getInstance().call(RESOURCE_LOGOUT,
					params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param target
	 *            要验证的手机号或 Email（请不要包含+86 或 0开头）
	 * @param template
	 *            发送短信或邮件内容模板，{code} 自动被替换为验证码
	 * @param checkExist
	 *            同时检测是否已经被注册：Y=要求已注册，N=要求未注册，默认不做检测
	 * @param checkCode
	 *            用户填写的验证码，若提供此参数则该请求只做验证不会发送短信
	 */
	public BaseResult verifyAccount(String target, String template,
			Boolean checkExist, String checkCode) {
		JSONObject params = new JSONObject();
		try {
			params.put("target", target);
			if (!TextUtils.isEmpty(template))
				params.put("template", template);
			if (checkExist != null)
				params.put("check_exist", checkExist.booleanValue() ? "Y" : "N");
			if (!TextUtils.isEmpty(checkCode))
				params.put("check_code", checkCode);

			return NetworkClientManager.getInstance().call(RESOURCE_VERIFY,
					params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param target
	 *            用户输入的手机号或邮箱 必填
	 * @param code
	 *            验证码 必填
	 * @param password
	 *            密码 必填
	 * @param nickname
	 * @param gender
	 * @param birthday
	 * @param location
	 * @return
	 */
	public BaseResult doRegisterByEmail(String target, String code,
			String password, String nickname, Boolean isMale, Date birthday,
			String location) {
		JSONObject params = new JSONObject();

		try {
			params.put("email", target);
			params.put("email_code", code);
			params.put("password", MD5Util.encrypt(password));
			params.put("nickname", nickname);
			params.put("birthday", TimeUtils.getTime(birthday.getTime(),
					TimeUtils.DATE_FORMAT_DATE));
			params.put("gender", isMale ? Gender.MALE : Gender.FEMALE);
			if (location != null)
				params.put("location", location);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetworkClientManager.getInstance().call(RESOURCE_REGISTER,
				params);
	}

	/**
	 * 
	 * @param target
	 * @param code
	 * @param password
	 * @param nickname
	 * @param isMale
	 * @param birthday
	 * @param location
	 * @return
	 */
	public BaseResult doRegisterByPhone(String target, String code,
			String password, String nickname, Boolean isMale, Date birthday,
			String location) {
		JSONObject params = new JSONObject();
		try {
			params.put("phone", target);
			params.put("phone_code", code);
			params.put("password", MD5Util.encrypt(password));
			params.put("nickname", nickname);
			params.put("birthday", TimeUtils.getTime(birthday.getTime(),
					TimeUtils.DATE_FORMAT_DATE));
			params.put("gender", isMale ? Gender.MALE : Gender.FEMALE);
			if (location != null)
				params.put("location", location);
			return NetworkClientManager.getInstance().call(RESOURCE_REGISTER,
					params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param target
	 * @param checkCode
	 * @param passwordNew
	 * @return
	 */
	public BaseResult resetPwd(String target, String checkCode,
			String passwordNew) {
		JSONObject params = new JSONObject();
		try {

			params.put("target", target);
			params.put("check_code", checkCode);
			params.put("password_new", MD5Util.encrypt(passwordNew));

			return NetworkClientManager.getInstance().call(RESOURCE_RESETPWD,
					params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @return
	 */
	public BaseResult getAccount(String accessToken) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);

			return NetworkClientManager.getInstance().call(RESOURCE_PROFILE,
					params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @author ChenJunQi.
	 * @param accessToken
	 * @param account
	 * @param code
	 *            参数顺序：先旧后新
	 * @return
	 */
	public BaseResult editAccount(String accessToken, Account account,
			String... code) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (account.getPassword() != null) {
				params.put("password", MD5Util.encrypt(account.getPassword()));
			}
			if (account.getLocation() != null) {
				params.put("location", account.getLocation());
			}
			if (account.getPhone() != null && code[0] != null
					&& code[1] != null) {
				params.put("phone", account.getPhone());
				params.put("phone_code_old", code[0]);
				params.put("phone_code_new", code[1]);
			}
			if (account.getEmail() != null && code[0] != null) {
				params.put("email", account.getEmail());
				params.put("email_code", code[0]);
			}
			if (account.getEmail2() != null) {
				params.put("email2", account.getEmail2());
			}
			if (account.getPhone2() != null) {
				params.put("phone2", account.getPhone2());
			}
			if (account.getRealName() != null) {
				params.put("username", account.getRealName());
			}
			if (account.getNickname() != null) {
				params.put("nickname", account.getNickname());
			}
			if (account.getIDCard() != null) {
				params.put("idcode", account.getIDCard());
			}
			if (account.getAddress() != null) {
				params.put("address", account.getAddress());
			}
			if (account.getBirthday() != null) {
				params.put("birthday", TimeUtils.getTime(account.getBirthday()
						.getTime(), TimeUtils.DATE_FORMAT_DATE));
			}
			if (account.isMale() != null) {
				params.put("gender", account.isMale() ? Gender.MALE
						: Gender.FEMALE);
			}
			if (account.getTall() != null) {
				params.put("tall", account.getTall());
			}
			if (account.getWeight() != null) {
				params.put("weight", account.getWeight());
			}
			if (account.getHeadPortRait() != null) {
				params.put("avatar", account.getHeadPortRait());
			}
			if (account.getSick() != null) {
				params.put("sick1", account.getSick());

			}
			if (account.getFlag() != null) {
				params.put("flag1", account.getFlag());
			}
			if (account.getPrebornday() != null) {
				params.put("prebornday", account.getPrebornday());
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_PROFILE_EDIT, params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @return
	 */
	public BaseResult getAllContact(String accessToken) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);

			return NetworkClientManager.getInstance().call(
					RESOURCE_CONTACT_LIST, params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param name
	 * @param phone
	 * @param email
	 * @return
	 */
	public BaseResult addContact(String accessToken, String name, String phone,
			String email) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);
			params.put("name", name);
			params.put("phone", phone);
			params.put("email", email);

			return NetworkClientManager.getInstance().call(
					RESOURCE_CONTACT_ADD, params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param contactId
	 * @return
	 */
	public BaseResult delContact(String accessToken, String contactId) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);
			params.put("contactid", contactId);

			return NetworkClientManager.getInstance().call(
					RESOURCE_CONTACT_DEL, params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param contactId
	 * @param name
	 * @param phone
	 * @param email
	 * @return
	 */
	public BaseResult editContact(String accessToken, String contactId,
			String name, String phone, String email) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);
			params.put("contactid", contactId);
			params.put("name", name);
			params.put("phone", phone);
			params.put("email", email);

			return NetworkClientManager.getInstance().call(
					RESOURCE_CONTACT_EDIT, params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param type
	 * @param data
	 * @param syncId
	 * @param serial
	 *            false
	 * @param limit
	 *            false
	 * @return
	 */

	public BaseResult uploadRecord(String accessToken, String type,
			String data, Integer syncId, Integer serial, Integer limit) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);
			params.put("type", type);
			params.put("up_data", data);
			if (syncId != null)
				params.put("up_syncid", syncId);
			if (serial != null)
				params.put("down_serial", serial.intValue());
			if (limit != null)
				params.put("down_limit", limit.intValue());
			return NetworkClientManager.getInstance().call(
					RESOURCE_RECORD_UPLOAD, params, true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param type
	 * @param timeRange
	 * @param endId
	 * @param serial
	 * @param limit
	 * @param offset
	 * @param sort
	 * @param downSyncid
	 *            待下载的帐号id,默认为当前自己
	 * @return
	 */
	public BaseResult getRecord(String accessToken, String type,
			String timeRange, Integer endId, Integer serial, Integer limit,
			Integer offset, String sort, Integer downSyncid) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);
			params.put("type", type);
			params.put("time_range", timeRange);
			params.put("end_id", endId);
			params.put("down_serial", serial);
			params.put("limit", limit);
			params.put("offset", offset);
			params.put("sort", sort);
			if (downSyncid != null) {
				params.put("down_syncid", downSyncid);
			}

			return NetworkClientManager.getInstance().call(
					RESOURCE_RECORD_DOWNLOAD, params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BaseResult getRecord(String accessToken, String type) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);
			params.put("type", type);

			return NetworkClientManager.getInstance().call(
					RESOURCE_RECORD_DOWNLOAD, params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param type
	 * @param recordId
	 * @param readMe
	 * @param value1
	 * @param value2
	 * @param value3
	 * @return
	 */
	public BaseResult editRecord(String accessToken, String type,
			Integer recordId, String readMe, Float value1, Float value2,
			Float value3) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);
			params.put("type", type);
			params.put("recordid", recordId);
			params.put("readme", readMe);
			params.put("value1", value1);
			params.put("value2", value2);
			params.put("value3", value3);

			return NetworkClientManager.getInstance().call(
					RESOURCE_RECORD_EDIT, params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param type
	 * @param recordId
	 * @return
	 */
	public BaseResult delRecord(String accessToken, String type,
			Integer recordId) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);
			params.put("type", type);
			params.put("recordid", recordId);

			return NetworkClientManager.getInstance().call(RESOURCE_RECORD_DEL,
					params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param type
	 * @param recordId
	 *            多个id用 , 连接
	 * @return
	 */
	public BaseResult delRecord(String accessToken, String type, String recordId) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);
			params.put("type", type);
			params.put("recordid", recordId);

			return NetworkClientManager.getInstance().call(RESOURCE_RECORD_DEL,
					params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BaseResult shareUrlRecord(String accessToken, String type,
			Integer recordId, String recent, String month) {
		JSONObject params = new JSONObject();
		try {

			params.put("access_token", accessToken);
			params.put("type", type);
			params.put("recordid", recordId);
			params.put("recent", recent);
			params.put("month", month);

			return NetworkClientManager.getInstance().call(
					RESOURCE_RECORD_SHAREURL, params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/* group */

	public BaseResult addGroup(String accessToken, String title,
			String description) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			params.put("title", title);
			params.put("description", description);
			return NetworkClientManager.getInstance().call(RESOURCE_GROUP_ADD,
					params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public BaseResult editGroup(String accessToken, Integer groupid,
			String title, String description, String image) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			params.put("title", title);
			params.put("description", description);
			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			params.put("image", image);
			return NetworkClientManager.getInstance().call(RESOURCE_GROUP_EDIT,
					params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public BaseResult delGroup(String accessToken, Integer groupid) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			return NetworkClientManager.getInstance().call(RESOURCE_GROUP_DEL,
					params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public BaseResult getGroupByID(String accessToken, Integer groupid) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			return NetworkClientManager.getInstance().call(RESOURCE_GROUP_VIEW,
					params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public BaseResult getAllGroup(String accessToken) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			return NetworkClientManager.getInstance().call(RESOURCE_GROUP_LIST,
					params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/* group member */

	public BaseResult getAllGroupMember(String accessToken, Integer groupid) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_MEMBER_LIST, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param type
	 *            默认为view
	 * @return
	 */
	public BaseResult getAuthoList(String accessToken, String type) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (type != null) {
				params.put("type", type);
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_AUTHORIZED, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param groupid
	 * @param isupload
	 * @param alert
	 *            群消息提醒方式 0，1，2
	 * @return
	 */
	public BaseResult editGroupMemberSetting(String accessToken,
			Integer groupid, Boolean isupload, Integer alert) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			if (isupload != null) {
				params.put("isupload", isupload.booleanValue() ? "Y" : "N");
			}
			if (alert != null) {
				params.put("alert", alert);
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_MEMBER_EDIT, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param syncid
	 *            目标群成员 账号ID
	 * @param groupid
	 *            共同群ID
	 * @param isView
	 *            查看 Y/N
	 * @param isCare
	 *            关心 Y/N
	 * @param isTest
	 *            测试 Y/N
	 * @param remark
	 *            备注
	 * @return
	 */
	public BaseResult editGroupMemberPerm(String accessToken, Integer syncid,
			Integer groupid, Boolean isView, Boolean isCare, Boolean isTest,
			String remark) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);

			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			if (syncid != null && syncid != Account.INVALID_ID) {
				params.put("syncid", syncid);
			}
			if (isView != null) {
				params.put("isview", isView == true ? "Y" : "N");
			}
			if (isCare != null) {
				params.put("iscare", isCare == true ? "Y" : "N");
			}
			if (isTest != null) {
				params.put("istest", isTest == true ? "Y" : "N");
			}
			if (remark != null) {
				params.put("remark", remark);
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_MEMBER_PERM, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param reverse
	 *            是否反向查询对方对我的授权设置。（Y=是/N=否，默认为 N。设置为 Y 后只允许获取设置不允许改变设置）
	 * @return
	 */
	public BaseResult getAuthoForMe(String accessToken, Integer syncid,
			String reverse) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			params.put("syncid", syncid);
			params.put("reverse", reverse);
			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_MEMBER_PERM, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 
	 * 
	 * @param accessToken
	 * @param groupid
	 * @param syncid
	 * @return
	 */
	public BaseResult delGroupMember(String accessToken, Integer groupid,
			Integer syncid) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			if (syncid != null && syncid != Account.INVALID_ID) {
				params.put("syncid", syncid);
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_MEMBER_DEL, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 
	 * 
	 * @param accessToken
	 * @param groupid
	 * @param syncid
	 * @return
	 */
	public BaseResult addGroupMember(String accessToken, Integer groupid,
			Integer syncid) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			if (syncid != null && syncid != Account.INVALID_ID) {
				params.put("syncid", syncid);
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_MEMBER_ADD, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param accessToken
	 * @param upData
	 * @return
	 */
	public BaseResult getAppModule(String accessToken,
			List<ModuleSpecification> upDataList, Integer syncid) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (upDataList != null) {
				JSONArray ja = new JSONArray();
				for (ModuleSpecification ms : upDataList) {
					JSONObject o = new JSONObject();
					o.put("moduleid", ms.getModuleID());
					o.put("category", ms.getCategory());
					o.put("classname", ms.getClassName());
					o.put("link", ms.getDownLoadLink());
					o.put("packagename", ms.getPackageName());
					o.put("order", ms.getOrder());
					o.put("status", ms.getModuleStatus().getStatusId());
					o.put("settings", ms.getSetting());
					o.put("default_install",
							ms.getExtraAttributeBool(
									ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_INSTALL,
									false));
					o.put("default_show_in_homepage",
							ms.getExtraAttributeBool(
									ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE,
									false));
					o.put("is_uninstallable",
							ms.getExtraAttributeBool(
									ModuleSpecification.EXTRA_ATTRIBUTES_IS_UNINSTALLABLE,
									false));
					ja.put(o);
				}
				params.put("up_data", ja.toString());
			}
			if (syncid != null) {
				params.put("syncid", syncid);
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_MODULE_CONFIG, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public BaseResult getAppModule(String accessToken,
			ModuleSpecification moduleSpecification) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (moduleSpecification != null) {
				JSONObject jo = new JSONObject();
				jo.put("moduleid", moduleSpecification.getModuleID());
				jo.put("category", moduleSpecification.getCategory());
				jo.put("classname", moduleSpecification.getClassName());
				jo.put("link", moduleSpecification.getDownLoadLink());
				jo.put("packagename", moduleSpecification.getPackageName());
				jo.put("order", moduleSpecification.getOrder());
				jo.put("status", moduleSpecification.getModuleStatus()
						.getStatusId());
				jo.put("settings", moduleSpecification.getSetting());
				jo.put("default_install",
						moduleSpecification
								.getExtraAttributeBool(
										ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_INSTALL,
										false));
				jo.put("default_show_in_homepage",
						moduleSpecification
								.getExtraAttributeBool(
										ModuleSpecification.EXTRA_ATTRIBUTES_DEFAULT_SHOW_IN_HOMEPAGE,
										false));
				jo.put("is_uninstallable",
						moduleSpecification
								.getExtraAttributeBool(
										ModuleSpecification.EXTRA_ATTRIBUTES_IS_UNINSTALLABLE,
										false));
				params.put("up_data", jo.toString());
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_MODULE_CONFIG, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param accessToken
	 * @param target
	 * @return
	 */
	public BaseResult queryAccountInfo(String accessToken, String target,
			Integer groupID) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			params.put("target", target);
			if (groupID != null && groupID != Group.INVALID_ID) {
				params.put("groupid", groupID);
			}

			return NetworkClientManager.getInstance().call(RESOURCE_USER_QUERY,
					params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param accessToken
	 * @return
	 */
	public BaseResult getMessageCount(String accessToken) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			return NetworkClientManager.getInstance().call(
					RESOURCE_MESSAGE_COUNT, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param accessToken
	 * @param limit
	 * @param offset
	 * @param system
	 * @return
	 */
	public BaseResult getMessageList(String accessToken, Integer limit,
			Integer offset, Integer system) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (limit != null)
				params.put("limit", limit);
			if (offset != null)
				params.put("offset", offset);
			if (system != null)
				params.put("system", system);

			return NetworkClientManager.getInstance().call(
					RESOURCE_MESSAGE_LIST, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param accessToken
	 * @param messageid
	 * @param isread
	 * @param system
	 * @return
	 */
	public BaseResult markMessageReaded(String accessToken, Integer messageid,
			Boolean isread, Integer system) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (messageid != null) {
				params.put("messageid", messageid);
			}
			if (isread != null) {
				params.put("isread", isread.booleanValue() ? "Y" : "N");
			}
			if (system != null) {
				params.put("system", system);
			}

			return NetworkClientManager.getInstance().call(
					RESOURCE_MESSAGE_MARK, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param accessToken
	 * @param messageid
	 * @param isread
	 *            Y=已读/N=未读/D=删除
	 * @param system
	 * @return
	 */
	public BaseResult markMessageReaded(String accessToken, Integer messageid,
			String isread, Integer system) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (messageid != null) {
				params.put("messageid", messageid);
			}
			if (isread != null) {
				params.put("isread", isread);
			}
			if (system != null) {
				params.put("system", system);
			}

			return NetworkClientManager.getInstance().call(
					RESOURCE_MESSAGE_MARK, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 
	 * 
	 * @param accessToken
	 * @param messageid
	 * @param response
	 * @return
	 */
	public BaseResult markMessageResponse(String accessToken,
			Integer messageid, String response, String extra) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (messageid != null)
				params.put("messageid", messageid);
			if (response != null)
				params.put("response", response);
			if (extra != null) {
				params.put("extra", extra);
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_MESSAGE_RESPONSE, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param accessToken
	 * @param syncid
	 * @param groupid
	 * @param limit
	 * @param offset
	 * @return
	 */
	public BaseResult getEventList(String accessToken, Integer syncid,
			Integer groupid, Integer limit, Integer offset) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);

			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			if (syncid != null && syncid != Account.INVALID_ID) {
				params.put("syncid", syncid);
			}
			if (limit != null) {
				params.put("limit", limit);
			}
			if (offset != null) {
				params.put("offset", offset);
			}
			return NetworkClientManager.getInstance().call(RESOURCE_EVENT_LIST,
					params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param accessToken
	 * @return
	 */
	public BaseResult getGroupMemberCareList(String accessToken) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_MEMBER_CARE_LIST, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param accessToken
	 * @return
	 */
	public BaseResult getGroupMemberTestList(String accessToken) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_MEMBER_TEST_LIST, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param accessToken
	 * @param syncid
	 * @param groupid
	 * @param begin_id
	 * @param end_id
	 * @return
	 */
	public BaseResult getGroupMessageCount(String accessToken, Integer syncid,
			Integer groupid, Integer begin_id, Integer end_id, Boolean unRead) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);
			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			if (syncid != null && syncid != Account.INVALID_ID) {
				params.put("syncid", syncid);
			}

			if (begin_id != null) {
				params.put("begin_id", begin_id);
			}
			if (end_id != null) {
				params.put("end_id", end_id);
			}
			if (unRead != null) {
				params.put("unread", unRead.booleanValue() ? "Y" : "N");
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_MESSAGE_COUNT, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public BaseResult getGroupMessageList(String accessToken, Integer syncid,
			Integer groupid, Integer begin_id, Integer end_id, Integer limit,
			Integer offset) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);

			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			if (syncid != null && syncid != Account.INVALID_ID) {
				params.put("syncid", syncid);
			}
			if (begin_id != null) {
				params.put("begin_id", begin_id);
			}
			if (end_id != null) {
				params.put("end_id", end_id);
			}
			if (limit != null) {
				params.put("limit", limit);
			}
			if (offset != null) {
				params.put("offset", offset);
			}
			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_MESSAGE_LIST, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param accessToken
	 * @param syncid
	 * @param groupid
	 * @param data
	 * @param type
	 * @return
	 */
	public BaseResult postGroupMessage(String accessToken, Integer syncid,
			Integer groupid, String data, Integer type) {
		JSONObject params = new JSONObject();
		try {
			params.put("access_token", accessToken);

			if (groupid != null && groupid != Group.INVALID_ID) {
				params.put("groupid", groupid);
			}
			if (syncid != null && syncid != Account.INVALID_ID) {
				params.put("syncid", syncid);
			}
			if (type != null) {
				params.put("type", type);
			}
			params.put("data", data);

			return NetworkClientManager.getInstance().call(
					RESOURCE_GROUP_MESSAGE_POST, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param measuretype
	 * @return
	 */
	public BaseResult getRuleList(Integer measuretype) {
		JSONObject params = new JSONObject();
		try {
			params.put("measuretype", measuretype);

			return NetworkClientManager.getInstance().call(RESOURCE_RULE_LIST,
					params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param syncid
	 * @param measuretype
	 * @param value1
	 * @param value2
	 * @return
	 */
	public BaseResult doRuleMatch(Integer syncid, Integer measuretype,
			Float value1, Float value2, Float value3) {
		JSONObject params = new JSONObject();
		try {
			params.put("measuretype", measuretype);
			params.put("syncid", syncid);
			params.put("value1", value1);
			if (value2 != null)
				params.put("value2", value2);
			if (value3 != null)
				params.put("value3", value3);
			return NetworkClientManager.getInstance().call(RESOURCE_RULE_MATCH,
					params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
