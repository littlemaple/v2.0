package com.medzone.framework.data.bean.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.medzone.framework.Log;
import com.medzone.framework.data.bean.BaseEntity;
import com.medzone.framework.network.NetworkClientResult;
import com.medzone.framework.util.DES2Utils;
import com.medzone.framework.util.TimeUtils;

/**
 * 
 * @author ChenJunQi.
 * 
 */
public class Account extends BaseEntity implements Cloneable {

	/**
	 */
	private static final long serialVersionUID = 9218266180251967361L;

	private static final String KEY_ENCODE = "Fsubmitresource";

	public static final String NAME_FIELD_ACCOUNT_ID = "accountID";
	public static final String NAME_FIELD_PASSWORD = "password";
	public static final String NAME_FIELD_NICKNAME = "nickname";
	public static final String NAME_FIELD_HEADPORTRAIT = "headPortRait";
	public static final String NAME_FIELD_IS_MALE = "isMale";
	public static final String NAME_FIELD_BIRTHDAY = "birthday";
	public static final String NAME_FIELD_TALL = "tall";
	public static final String NAME_FIELD_WEIGHT = "weight";
	public static final String NAME_FIELD_SICK = "sick";
	public static final String NAME_FIELD_PHONE = "phone";
	public static final String NAME_FIELD_EMAIL = "email";
	public static final String NAME_FIELD_IDCARD = "IDCard";
	public static final String NAME_FIELD_CREATETIME = "createTime";
	public static final String NAME_FIELD_CREATESOURCE = "createSource";
	public static final String NAME_FIELD_PREGNANT = "prebornday";
	public static final String NAME_FIELD_REALNAME = "realName";
	public static final String NAME_FIELD_LOCATION = "location";
	public static final String NAME_FIELD_ADDRESS = "address";

	public static final String NAME_FIELD_TARGET = "target";
	public static final String NAME_FIELD_CODE = "code";

	public static final String NAME_FIELD_PHONE2 = "phone2";
	public static final String NAME_FIELD_EMAIL2 = "email2";

	@DatabaseField(id = true)
	private Integer accountID;// syncid

	@DatabaseField
	private String password;;

	@DatabaseField
	private String phone2;// use for tag

	@DatabaseField
	private String email2;// use for tag

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getEmail2() {
		return email2;
	}

	public void setEmail2(String email2) {
		this.email2 = email2;
	}

	@DatabaseField
	private String nickname;// nickname

	@DatabaseField
	private String realName;// username

	@DatabaseField
	private String headPortRait;// imagefile

	@DatabaseField
	private Boolean isMale;// gender

	@DatabaseField
	private Date birthday;// birthday

	@DatabaseField
	private Float tall;// tall

	@DatabaseField
	private Float weight;// weight

	@DatabaseField
	private Integer sick;// sick1 疾病信息设置（位运算组合）。

	@DatabaseField
	private String phone;// phone

	@DatabaseField
	private String IDCard;// idcode

	@DatabaseField
	private String email;// email

	@DatabaseField
	private Date createTime;// createtime

	@DatabaseField
	private String createSource;

	@DatabaseField
	private Integer flag;// flag1 用户选项设置（位运算组合）。

	@DatabaseField
	private String prebornday;// prebornday 预产期

	@DatabaseField
	private String location;// "location":"浙江省 - 杭州市 - 西湖区:,

	@DatabaseField
	private String address;// address

	private String pushID;

	private Boolean isLoginSuccess;

	private String accessToken;

	/**
	 * 
	 * @param member
	 * @return 返回显示名，优先级为：备注>昵称>姓名；
	 * 
	 */
	public String getFriendsDisplay(GroupMember member) {
		if (!TextUtils.isEmpty(member.getRemark())) {
			return member.getRemark();
		} else if (!TextUtils.isEmpty(member.getNickname())) {
			return member.getNickname();
		} else {
			return member.getRealName();
		}
	}

	public Integer getAccountID() {
		if (accountID == null) {
			String invalid = String.valueOf(Account.INVALID_ID);
			return Integer.valueOf(invalid);
		}
		return accountID.intValue();
	}

	public void setAccountID(Integer accountID) {
		this.accountID = accountID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the realName
	 */
	public String getRealName() {
		return realName;
	}

	/**
	 * @param realName
	 *            the realName to set
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getHeadPortRait() {
		return headPortRait;
	}

	public void setHeadPortRait(String headPortRait) {
		this.headPortRait = headPortRait;
	}

	public Boolean isMale() {
		return isMale;
	}

	public void setMale(Boolean isMale) {
		this.isMale = isMale;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Float getTall() {
		return tall;
	}

	public void setTall(Float tall) {
		this.tall = tall;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Integer getSick() {
		return sick;
	}

	public void setSick(Integer sick) {
		this.sick = sick;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIDCard() {
		return IDCard;
	}

	public void setIDCard(String iDCard) {
		IDCard = iDCard;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateSource() {
		return createSource;
	}

	public void setCreateSource(String createSource) {
		this.createSource = createSource;
	}

	public Boolean isLoginSuccess() {
		return isLoginSuccess;
	}

	public void setLoginSuccess(Boolean isLoginSuccess) {
		this.isLoginSuccess = isLoginSuccess;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * // 设置选项定义： 0x01 开启语音播报 0x04 开启自动分享
	 * 
	 * @return
	 */
	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getPrebornday() {
		return prebornday;
	}

	public void setPrebornday(String prebornday) {
		this.prebornday = prebornday;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the location
	 */
	public String getAddress() {
		return address;
	}

	public String getPushID() {
		return pushID;
	}

	public void setPushID(String pushID) {
		this.pushID = pushID;
	}

	public static String encodePassword(String accountPassword) {
		String ciphertext = null;
		if (!TextUtils.isEmpty(accountPassword)) {
			try {
				ciphertext = DES2Utils.encode(KEY_ENCODE, accountPassword);
			} catch (Exception e) {
				ciphertext = null;
				Log.d("Account", "encodePassword error:" + e.getMessage());
			}
		}
		return ciphertext;
	}

	public static String decodePassword(String ciphertext) {
		String password = null;
		if (!TextUtils.isEmpty(ciphertext)) {
			try {
				password = DES2Utils.decodeValue(KEY_ENCODE, ciphertext);
			} catch (Exception e) {
				password = null;
				Log.d("Account", "decodePassword error:" + e.getMessage());
			}
		}
		return password;
	}

	public static List<Account> createAccountList(NetworkClientResult res) {
		JSONArray ja = null;
		List<Account> list = null;
		JSONObject jo = res.getResponseResult();
		try {
			ja = jo.getJSONArray("root");
			list = new ArrayList<Account>();
			for (int i = 0; i < ja.length(); i++) {
				Account account = createAccount((JSONObject) ja.get(i));
				if (account != null)
					list.add(account);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static Account createAccount(NetworkClientResult res) {
		return parse(res, new Account());
	}

	public static Account createAccount(JSONObject jo) {
		return parse(jo, new Account());
	}

	public static Account updateAccount(NetworkClientResult res, Account account) {
		return parse(res, account);
	}

	private static Account parse(NetworkClientResult res, Account account) {
		return parse(res.getResponseResult(), account);
	}

	private static Account parse(JSONObject jo, Account account) {

		try {
			if (jo.has("access_token") && !jo.isNull("access_token")) {
				account.setAccessToken(jo.getString("access_token"));
			}
			if (jo.has("syncid") && !jo.isNull("syncid")) {
				account.setAccountID(jo.getInt("syncid"));
			}
			if (jo.has("phone") && !jo.isNull("phone")) {
				account.setPhone(jo.getString("phone"));
			}
			if (jo.has("phone2") && !jo.isNull("phone2")) {
				account.setPhone2(jo.getString("phone2"));
			}
			if (jo.has("email2") && !jo.isNull("email2")) {
				account.setEmail2(jo.getString("email2"));
			}
			if (jo.has("location") && !jo.isNull("location")) {
				account.setLocation(jo.getString("location"));
			}
			if (jo.has("idcode") && !jo.isNull("idcode")) {
				account.setIDCard(jo.getString("idcode"));
			}
			if (jo.has("address") && !jo.isNull("address")) {
				account.setAddress(jo.getString("address"));
			}
			if (jo.has("nickname") && !jo.isNull("nickname")) {
				account.setNickname(jo.getString("nickname"));
			}
			if (jo.has("createtime") && !jo.isNull("createtime")) {
				account.setCreateTime(new Date(jo.getLong("createtime")));
			}
			if (jo.has("email") && !jo.isNull("email")) {
				account.setEmail(jo.getString("email"));
			}
			if (jo.has("username") && !jo.isNull("username")) {
				account.setRealName(jo.getString("username"));
			}
			if (jo.has("gender") && !jo.isNull("gender")) {
				boolean gender = TextUtils.equals(jo.getString("gender"),
						Gender.MALE) ? true : false;
				account.setMale(gender);
			}
			if (jo.has("birthday") && !jo.isNull("birthday")) {
				account.setBirthday(TimeUtils.getDate(jo.getString("birthday"),
						TimeUtils.DATE_FORMAT_DATE));
			}
			if (jo.has("tall") && !jo.isNull("tall")) {
				account.setTall((float) jo.getDouble("tall"));
			}
			if (jo.has("weight") && !jo.isNull("weight")) {
				account.setWeight((float) jo.getDouble("weight"));
			}
			if (jo.has("imagefile") && !jo.isNull("imagefile")) {
				account.setHeadPortRait(jo.getString("imagefile"));
			}
			if (jo.has("sick1") && !jo.isNull("sick1")) {
				account.setSick(jo.getInt("sick1"));
			}
			if (jo.has("flag1") && !jo.isNull("flag1")) {
				account.setFlag(jo.getInt("flag1"));
			}
			if (jo.has("prebornday") && !jo.isNull("prebornday")) {
				account.setPrebornday(jo.getString("prebornday"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return account;
	}

	public boolean isSameRecord(Object o) {
		Account item2 = (Account) o;
		if (this.getAccountID() == INVALID_ID
				|| item2.getAccountID() == INVALID_ID) {
			return false;
		}
		if (!this.getAccountID().equals(item2.getAccountID())) {
			return false;
		}
		return true;
	}

	@Override
	public void cloneFrom(Object o) {
		Account item2 = (Account) o;
		this.setAccountID(item2.getAccountID());
		super.cloneFrom(o);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * 两个AccountID相同即表示具体equal关系
	 */
	@Override
	public boolean equals(Object o) {

		if (o instanceof Account) {
			// 是否为同一个帐号
			Account tmpAccount = (Account) o;
			int curID = getIDValue(this.getAccountID());
			int tmpID = getIDValue(tmpAccount.getAccountID());
			return curID == tmpID;
		} else if (o instanceof Group) {
			// 是否是群主
			Group tmpGroup = (Group) o;
			int curID = getIDValue(this.getAccountID());
			int tmpID = getIDValue(tmpGroup.getCreatorID());
			return curID == tmpID;
		} else if (o instanceof Message) {
			Message tmpMessage = (Message) o;
			int curID = getIDValue(this.getAccountID());
			int tmpID = getIDValue(tmpMessage.getAccountID());
			return curID == tmpID;
		} else if (o instanceof GroupMember) {
			GroupMember tmpGroupMember = (GroupMember) o;
			int curID = getIDValue(this.getAccountID());
			int tmpID = getIDValue(tmpGroupMember.getAccountID());
			return curID == tmpID;
		}
		return super.equals(o);
	}

	public class Gender {
		public static final String MALE = "男";
		public static final String FEMALE = "女";

	}
}
