package com.medzone.framework.errorcode;

/**
 * 错误场景码，需要保证不同的场景唯一对应一个Code，但是相同的场景，可以结合。
 * 
 * @author junqi
 * 
 */
public abstract class ProxyCode {

	public class NetError {

		// ============================
		// 验证手机、邮箱 /api/verify
		// ============================

		/** 参数不正确，通常是缺少提供必要参数 */
		public static final int CODE_40000 = 40000;
		/** 提供的 access_token 不正确或已过期不可用 */
		public static final int CODE_40001 = 40001;
		/** old token has been kicked */
		public static final int CODE_40002 = 40002;
		/** 手机号码格式不正确 */
		public static final int CODE_40100 = 40100;
		/** 电子邮箱格式不正确 */
		public static final int CODE_40101 = 40101;
		/** 手机号/邮箱未注册。 */
		public static final int CODE_40102 = 40102;
		/** 手机号/邮箱已被注册 */
		public static final int CODE_40103 = 40103;
		/** 发送太频繁（APP可以忽略这个错误）。 */
		public static final int CODE_40104 = 40104;
		/** 短信/邮件网关发生错误。 */
		public static final int CODE_40105 = 40105;
		/** 用户输入的验证码不正确。 */
		public static final int CODE_40106 = 40106;

		// ============================
		// 注册新账号 /api/register
		// ============================

		/** 账号注册资料不正确，在 errors 字段中附带出错字段的具体原因。 */
		public static final int CODE_40300 = 40300;
		/** 密码不正确或为空。 */
		public static final int CODE_40301 = 40301;
		/** 用户输入的手机验证码不正确。 */
		public static final int CODE_40302 = 40302;
		/** 用户输入的邮箱验证码不正确。 */
		public static final int CODE_40303 = 40303;

		// ============================
		// 重置密码 /api/passwdReset
		// ============================

		/** 绑定账号不存在。 */
		public static final int CODE_40400 = 40400;
		/** 新密码不正确或为空。 */
		public static final int CODE_40401 = 40401;
		/** 验证码不正确。 */
		public static final int CODE_40402 = 40402;

		// ============================
		// 登录授权 /api/login
		// ============================

		/** 密码解密失败。 */
		public static final int CODE_40500 = 40500;
		/** 相应的绑定账号不存在。 */
		public static final int CODE_40501 = 40501;
		/** 密码错误。 */
		public static final int CODE_40502 = 40502;
		/** 密码错误次数过多，请 15 分钟后再试 >>> 40503 */
		public static final int CODE_40503 = 40503;
		/** old token has been kicked 原有 token 已被踢，请手动登录。 */
		public static final int CODE_40504 = 40504;

		// ============================
		// 修改账号资料 /api/profileEdit
		// ============================
		/** 账号资料不正确，在 errors 字段中附带出错字段的具体原因。 */
		public static final int CODE_40700 = 40700;
		/** 旧手机验证码不正确。 */
		public static final int CODE_40701 = 40701;
		/** 新手机验证码不正确。 */
		public static final int CODE_40702 = 40702;
		/** 新密码解码失败。 */
		public static final int CODE_40703 = 40703;
		/** 头像数据解码不正确，须经 base64 编码。 */
		public static final int CODE_40704 = 40704;
		/** 邮箱修改验证码不正确。 */
		public static final int CODE_40705 = 40705;

		// ============================
		// 联系人管理
		// ============================

		/** 联系人资料填写不正确，在 errors 字段中附带出错字段的具体原因。 */
		public static final int CODE_41000 = 41000;
		/** 联系人数量已达上限，不能再添加了。 */
		public static final int CODE_41001 = 41001;
		/** 联系人主键不存在或不属于当前账号。 */
		public static final int CODE_41100 = 41100;
		/** 联系人主键不存在或不属于当前账号。 */
		public static final int CODE_41200 = 41200;
		/** 联系人资料填写不正确，在 errors 字段中附带出错字段的具体原因。 */
		public static final int CODE_41201 = 41201;

		// ============================
		// 测量数据管理
		// ============================

		/** 提供的测量类型不正确。 */
		public static final int CODE_41300 = 41300;
		/** 上传的测量数据解码失败（要求 JSON 格式） */
		public static final int CODE_41301 = 41301;
		/** 代码测量的用户 ID 无效。（不存在或无权限）。 */
		public static final int CODE_41302 = 41302;

		/** 提供的测量类型不正确。 */
		public static final int CODE_41400 = 41400;
		/** 下载数据的用户 ID 无效。（不存在或无权限）。 */
		public static final int CODE_41401 = 41401;

		/** 提供的测量类型不正确。 */
		public static final int CODE_41500 = 41500;
		/** 数据主键不存在或不属于当前账号。 */
		public static final int CODE_41501 = 41501;
		/** 测量数据填写不正确，在 errors 字段中附带出错字段的具体原因。 */
		public static final int CODE_41502 = 41502;

		/** 提供的测量类型不正确。 */
		public static final int CODE_41600 = 41600;

		// ============================
		// APP 模块配置存取 /api/appModule
		// ============================

		/** 上传的模块配置数据解码失败（要求 JSON 格式） */
		public static final int CODE_41700 = 41700;
		/** 要读取配置的 syncid 不存在或无效。 */
		public static final int CODE_41701 = 41701;

		// ============================
		// 查询用户资料 /api/userQuery
		// ============================

		/** 要查询的目标账号不存在。。 */
		public static final int CODE_41800 = 41800;

		// ============================
		// 生成数据分享 URL /api/recordShareUrl
		// ============================

		/** 提供的测量类型不正确。 */
		public static final int CODE_41900 = 41900;
		/** 要分享的数据 ID 无效（不存在或不属于当前用户）。 */
		public static final int CODE_41901 = 41901;
		/** 要分享的趋势时间起点不正确。 */
		public static final int CODE_41902 = 41902;
		/** 要分享的月份格式不正确。 */
		public static final int CODE_41903 = 41903;
		/** 不正确的分享类型，recordid/recent/month 至少需要指定一项。 */
		public static final int CODE_41904 = 41904;

		// ============================
		// 用户消息/事件
		// ============================

		/** 提供的 messageid 不正确或不隶属于当前用户。 */
		public static final int CODE_42200 = 42200;
		/** 提供的 messageid 不正确或不隶属于当前用户。 */
		public static final int CODE_42300 = 42300;
		/** 提供的响应操作参数不可为空。 */
		public static final int CODE_42301 = 42301;
		/** 处理消息失败（通常是重复处理或不需处理或内部出错） */
		public static final int CODE_42302 = 42302;
		/** 无效的用户 ID，通常是不存在 */
		public static final int CODE_42400 = 42400;
		/** 无效的群 ID，不存在或双方并不都在该群中 */
		public static final int CODE_42401 = 42401;

		// ============================
		// 群主功能
		// ============================
		/** 群资料填写不正确，在 errors 字段中附带出错字段的具体原因。 */
		public static final int CODE_42500 = 42500;
		/** 用户群数量已达上限，不能再添加了。 */
		public static final int CODE_42501 = 42501;
		/** 群资料填写不正确，在 errors 字段中附带出错字段的具体原因 */
		public static final int CODE_42600 = 42600;
		/** 无效的群 ID，不存在或无权修改 */
		public static final int CODE_42601 = 42601;

		/** 无效的群 ID，不存在或无权解散 */
		public static final int CODE_42700 = 42700;
		/** 无效的群 ID，不存在或无权邀请 */
		public static final int CODE_42800 = 42800;

		/** 目标用户不存在。 */
		public static final int CODE_42801 = 42801;
		/** 用户已存在于该群。 */
		public static final int CODE_42802 = 42802;
		/** 无效的群 ID，不存在或无权踢人权限。 */
		public static final int CODE_42900 = 42900;
		/** 群主不可以被踢或退群。 */
		public static final int CODE_42901 = 42901;
		/** 用户不在群中。 */
		public static final int CODE_42902 = 42902;

		/** 群资料填写不正确，在 errors 字段中附带出错字段的具体原因。 */
		public static final int CODE_43100 = 43100;
		/** 无效的群 ID，不存在或不在该群中。 */
		public static final int CODE_43101 = 43101;
		/** 群资料填写不正确，在 errors 字段中附带出错字段的具体原因。 */
		public static final int CODE_43200 = 43200;
		/** 无效的用户 ID，通常是不存在。 */
		public static final int CODE_43201 = 43201;
		/** 无效的群 ID，不存在或双方并不都在该群中。 */
		public static final int CODE_43202 = 43202;
		/** 特别关心的人数达到上限。 */
		public static final int CODE_43203 = 43203;
		/** 允许查看的人数达到上限。 */
		public static final int CODE_43204 = 43204;
		/** 允许代测的人数达到上限。 */
		public static final int CODE_43205 = 43205;
		/** 无效的群组 ID 或不在此群中。 */
		public static final int CODE_43300 = 43300;
		/** 无效的群 ID，不存在或者您不在该群中。 */
		public static final int CODE_43600 = 43600;
		/** 无效的对话用户 ID，对方不在这个群中。 */
		public static final int CODE_43601 = 43601;
		/** 无效的群 ID，不存在或者您不在该群中 */
		public static final int CODE_43700 = 43700;
		/** 无效的对话用户 ID，对方不在这个群中。 */
		public static final int CODE_43701 = 43701;
		/** 无效的群 ID，不存在或者您不在该群中。 */
		public static final int CODE_43800 = 43800;
		/** 无效的对话用户 ID，对方不在这个群中。 */
		public static final int CODE_43801 = 43801;
		/** 您不是群主，无权直接和用户对话。 */
		public static final int CODE_43802 = 43802;
		/** 不能自己发消息给自己。 */
		public static final int CODE_43803 = 43803;
		/** 消息数据不可为空，对于非普通消息必须是合法的 JSON 数据。 */
		public static final int CODE_43804 = 43804;
		/** 无效的群 ID，群不存在。 */
		public static final int CODE_43900 = 43900;

		/** 无效的订阅群 ID，群不存在或者类型不正确。 */
		public static final int CODE_44100 = 44100;
		/** 已经订阅过本群了。 */
		public static final int CODE_44101 = 44101;
		/** 提供的账号 ID 不正确。 */
		public static final int CODE_45100 = 45100;
		/** 无效的测量类型。 */
		public static final int CODE_45102 = 45102;
		/** 无合适匹配规则。 */
		public static final int CODE_45103 = 45103;
		/** 内部错误，通常是数据库出错 */
		public static final int CODE_50001 = 50001;
	}

	public class LocalError {

		// =============================
		// 公共部分
		// =============================

		public static final int CODE_SUCCESS = 0;

		/** 无 */
		public static final int CODE_10000 = 10000;
		/** 当前网络不可用,请检查网络设置 */
		public static final int CODE_10001 = 10001;
		/** 网络异常,请稍后再试 */
		public static final int CODE_10002 = 10002;
		/** 字数超过限制 */
		public static final int CODE_10003 = 10003;
		/** 没有找到储存卡 */
		public static final int CODE_10004 = 10004;
		/** 服务器异常，请稍后再试 */
		public static final int CODE_10005 = 10005;

		/** 账号为空 */
		public static final int CODE_ACCOUNT_EMPTY = 10045;
		/** 账号不合法 */
		public static final int CODE_ACCOUNT_ILLAGE = 10046;

		/** 手机为空 */
		public static final int CODE_PHONE_EMPTY = 10050;
		/** 手机不合法 */
		public static final int CODE_PHONE_ILLAGE = 10051;

		/** 邮箱为空 */
		public static final int CODE_EMAIL_EMPTY = 10056;
		/** 邮箱不合法 */
		public static final int CODE_EMAIL_ILLAGE = 10057;

		/** 密码为空 */
		public static final int CODE_PASSWORD_EMPTY = 10060;
		/** 密码不合法 */
		public static final int CODE_PASSWORD_ILLAGE = 10061;

		/** 昵称为空 */
		public static final int CODE_NICKNAME_EMPTY = 10070;
		/** 昵称不合法(昵称只能由汉字、数字及英文字母组成；) */
		public static final int CODE_NICKNAME_ILLAGE = 10071;
		/** 昵称不合法(字数超过限制) */
		public static final int CODE_NICKNAME_TOO_LONG = 10072;

		/** 验证码展现模板( 验证码已发送至%1$s\n%2$s) */
		public static final int CODE_10203 = 10203;

		/** 验证码为空 */
		public static final int CODE_10076 = 10076;

		// =============================
		// 登录
		// =============================

		// 请输入手机号（见通用）
		// 请输入邮箱（见通用）
		// 请输入正确的手机号（见通用）
		// 请输入正确的邮箱（见通用）
		// 请输入密码（见通用）

		public static final int CODE_PASSWORD_ERROR = 10125;

		/** 登陆顶号事件存在 */
		public static final int CODE_LOGIN_KICKED_ERROR = 10126;

		// =============================
		// 注册-流程手机号码验证
		// =============================

		// 请输入手机号（见通用）
		// 请输入邮箱（见通用）
		// 请输入正确的手机号（见通用）
		// 请输入正确的邮箱（见通用）
		// 验证码已经发送至手机186xxxx；验证码已经发送至xxx@126.com（见通用）

		/** 注册-个人信息-请输入验证码 */
		public static final int CODE_10312 = 10312;
		/** 注册-个人信息-验证码错误 */
		public static final int CODE_10206 = 10206;

		// =============================
		// 注册-个人信息
		// =============================

		// 请输入昵称（见通用）
		// 请输入正确的昵称（见通用）

		/** 注册-个人信息-请选择年龄 */
		public static final int CODE_10212 = 10212;
		/** 注册-个人信息-请选择性别 */
		public static final int CODE_10213 = 10213;
		/** 注册-个人信息-注册成功 */
		public static final int CODE_10211 = 10211;
		/** 注册-个人信息-注册流程出现异常，请尝试重新注册 */
		public static final int CODE_10214 = 10214;

		// =============================
		// 注册-设备选择
		// =============================

		// 无

		// =============================
		// 忘记密码-新密码设定
		// =============================

		// 请输入手机号（见通用）
		// 请输入邮箱（见通用）
		// 请输入正确的密码（见通用）
		// 请输入正确的手机号（见通用）
		// 请输入正确的邮箱（见通用）
		// 验证码已经发送至手机186xxxx；验证码已经发送至xxx@126.com（见通用）

		/** 忘记密码-验证码错误 */
		public static final int CODE_10304 = 10304;
		/** 忘记密码-请输入新密码 */
		public static final int CODE_10305 = 10305;
		/** 忘记密码-新密码设置成功 */
		public static final int CODE_10307 = 10307;

		// =============================
		// 设置-个人信息
		// =============================

		// 请输入昵称（见通用）
		// 请输入正确的昵称（见通用）

		/** 妊娠天数不可以为空 */
		public static final int CODE_13204 = 13204;
		/** 妊娠期在当前时间之前，不合理！ */
		public static final int CODE_13205 = 13205;

		// =============================
		// 设置-安全与隐私-信息绑定
		// =============================
		/** 信息绑定-请输入邮箱 */
		public static final int CODE_13105 = 13105;
		/** 信息绑定-请输入正确的邮箱号 */
		public static final int CODE_13106 = 13106;

		/** 信息绑定-请输入手机号 */
		public static final int CODE_INFO_BIND_PHONE_NULL = 13127;
		/** 信息绑定-请输入正确的手机号 */
		public static final int CODE_13107 = 13107;

		/** 信息绑定-真实姓名为空(请输入姓名) */
		public static final int CODE_REALNAME_EMPTY = 10066;
		/** 信息绑定-真实姓名不合法(姓名只能由汉字、数字及英文字母组成) */
		public static final int CODE_REALNAME_ILLAGE = 10067;
		/** 信息绑定-真实姓名不合法(字数超过限制) */
		public static final int CODE_REALNAME_TOO_LONG = 10068;

		/** 信息绑定-请输入身份证号码 */
		public static final int CODE_INFO_BIND_IDCRAD_NULL = 13128;
		/** 信息绑定-请输入正确的身份证号码 */
		public static final int CODE_INFO_BIND_IDCRAD_ILLAGE = 13108;

		/** 信息绑定-请选择省份,请选择城市 */
		public static final int CODE_13109 = 13109;
		/** 信息绑定-您输入的地址格式不正确，请重新输入,您输入的地址格式不正确，请重新输入地址只能由汉字、数字或英文字母组成 */
		public static final int CODE_13110 = 13110;
		/** 信息绑定-地址-字数超过限制 */
		public static final int CODE_13111 = 13111;
		/** 信息绑定-地址-请输入地址 */
		public static final int CODE_13112 = 13112;

		// =============================
		// 设置-安全与隐私-修改心云密码
		// =============================

		/** 修改密码-请输入原密码 */
		public static final int CODE_13201 = 13201;
		/** 修改密码-原密码输入不正确，请重新输入 */
		public static final int CODE_13202 = 13202;

		/** 修改密码-请输入新密码 */
		public static final int CODE_RESET_NEW_PASSWORD_NULL = 13221;
		/** 修改密码-新密码输入不正确(密码需为6-16位英文或数字，请重新输入) */
		public static final int CODE_RESET_NEW_PASSWORD_ILLAGE = 13222;
		/** 修改密码-新密码设置成功 */
		public static final int CODE_RESET_NEW_PASSWORD_SUCCESS = 13223;

		/*
		 * 
		 * 测量部分-硬件通讯部分
		 */
		/** 蓝牙未打开，请在系统设置中打开 */
		protected static final int CODE_11101 = 11101;
		/** 连接失败，请检查设备是否打开 */
		protected static final int CODE_11102 = 11102;
		/** 蓝牙已被占用，无法连接到设备 */
		protected static final int CODE_11103 = 11103;
		/** 蓝牙通信失败，请重新连接 */
		protected static final int CODE_11104 = 11104;
		/** 连接断开，请检查设备是否正常工作，不要离设备 */
		protected static final int CODE_11105 = 11105;

		/** 检测故障，请重新测量 */
		protected static final int CODE_11201 = 11201;
		/** 电池电量不足，请更换电池 */
		protected static final int CODE_11202 = 11202;
		/** 设备异常，请重新测量 */
		protected static final int CODE_11203 = 11203;
		/** 请将手指插入血氧仪 */
		protected static final int CODE_11204 = 11204;

		/** 设备异常，无法进行正常测量 */
		protected static final int CODE_11301 = 11301;
		/** 连接断开，请检查设备是否打开，是否正常连到手机 */
		protected static final int CODE_11302 = 11302;
		/** 测量结果异常，请使用正确姿势测量 */
		protected static final int CODE_11303 = 11303;
		/** 环境温度异常，请在10℃~40℃温度下测量 */
		protected static final int CODE_11304 = 11304;

		// ===========================
		// 测量-历史列表/同步
		// ===========================
		/** 此前无任何数据 */
		public static final int CODE_11401 = 11401;
		/** 不能选择当前日期之后 */
		public static final int CODE_11402 = 11402;
		/** 删除成功 */
		public static final int CODE_11403 = 11403;
		/** 删除失败 */
		public static final int CODE_11404 = 11404;
		/** 该月无任何数据 */
		public static final int CODE_11405 = 11405;
		/** 血氧不能为空 */
		public static final int CODE_BLOOD_OXYGEN_EMPTY = 11410;
		/** 心率不能为空 */
		public static final int CODE_HEART_RATE_EMPTY = 11411;
		/** 收缩压不能为空 */
		public static final int CODE_SYSTOLIC_PRESSURE_EMPTY = 11412;
		/** 舒张压不能为空 */
		public static final int CODE_DIASTOLIC_PRESSURE_EMPTY = 11413;
		/** 耳温不能为空 */
		public static final int CODE_TEMPERATURE_EMPTY = 11414;
		/** 收缩压不能小于舒张压 */
		public static final int CODE_SYSTOLIC_LESS_THAN_DIASTOLIC = 11415;
		/** 替他人测量完成 */
		public static final int CODE_SOMEONT_ELSE_IS_FINISED = 11450;
		/** 数据上传失败，请稍后再试 */
		public static final int CODE_DATA_UPLOAD_FAILURE = 11451;
		/** 当前无网络，无法查看分析及建议 */
		public static final int CODE_CANNOT_DISPLAY_SUGGEST = 11455;
		/** 当前网络不可用，无法替他人测量 */
		public static final int CODE_MEASUREMENT_FOR_OTHERS = 11456;

		// ================================
		// 群组
		// ================================
		/** （Dev：）账号信息加载中，请稍后。。。！ */
		public static final int CODE_12416 = 12416;

		// ================================
		// 群组-创建群
		// ================================
		/** 群组-创建群-请输入群名称 */
		public static final int CODE_12101 = 12101;
		/** 群组-创建群-群名称只能由汉字、数字及英文字母组成 */
		public static final int CODE_12102 = 12102;
		/** 群组-创建群-群名称字数超过限制 */
		public static final int CODE_12103 = 12103;

		/** 群组-创建群-群介绍不合法情况 */
		public static final int CODE_GROUP_INTRODUCE_ILLAGE = 12150;
		/** 群组-创建群-请输入群介绍 */
		public static final int CODE_GROUP_INTRODUCE_EMPTY = 12151;
		/** 群组-创建群-群介绍字数超过限制 */
		public static final int CODE_GROUP_INTRODUCE_TOO_LONG = 12151;

		// ================================
		// 群组-邀请成员
		// ================================

		/** 群组-邀请成员-输入邀请成员用户名 */
		public static final int CODE_12201 = 12201;
		/** 群组-邀请成员-输入邀请的用户名，格式错误（显示:该用户名不存在） */
		public static final int CODE_12202 = 12202;
		/** 群组-邀请成员-已发送邀请 */
		public static final int CODE_12203 = 12203;
		/** 群组-邀请成员-邀请失败 */
		public static final int CODE_12204 = 12204;
		/** 群成员已被踢出该群 */
		public static final int CODE_12205 = 12205;
		/** 群已经被解散 */
		public static final int CODE_12206 = 12206;

		// ================================
		// 群交互
		// ================================

		/** 群交互-最多发送400个字符 */
		public static final int CODE_12301 = 12301;
		/** 群交互-输入信息为空，不可发送！ */
		public static final int CODE_12302 = 12302;
		/** 无法发送空白消息！ */
		public static final int CODE_12303 = 12303;
		/** 消息发送太频繁 */
		public static final int CODE_12304 = 12304;

		// ================================
		// 成员详细资料
		// ================================

		/** 成员详细资料-备注只能由汉字、数字及英文字母组成 */
		public static final int CODE_12401 = 12401;
		/** 成员详细资料-备注字数超过限制 */
		public static final int CODE_12402 = 12402;
		/** 成员详细资料-对方未授权您替Ta测量 */
		public static final int CODE_12403 = 12403;
		/** 成员详细资料-对方未授予您权限访问 */
		public static final int CODE_12404 = 12404;

		// ================================
		// 测量提醒
		// ================================
		/** 测量提醒-请输入标签 */
		public static final int CODE_14103 = 14103;
		/** 测量提醒-最多20个字符 */
		public static final int CODE_14104 = 14104;
		/** 测量提醒-标签只能由汉字、数字及英文字母组成 */
		public static final int CODE_14101 = 14101;
		/** 测量提醒-闹钟最多创建20个 */
		public static final int CODE_14102 = 14102;

		// ====================================
		// 测量分享
		// ====================================
		/** 当前网络不可用，无法进行心云分享 */
		public static final int CODE_18100 = 18100;
		/** 数据正在上传中，请稍后再试*/
		public static final int CODE_18101 = 18101;
	}

}
