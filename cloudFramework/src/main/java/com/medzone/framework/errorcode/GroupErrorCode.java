package com.medzone.framework.errorcode;

import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyCode.NetError;

public class GroupErrorCode extends ErrorCode {

	public GroupErrorCode() {
		super();
	}

	@Override
	protected void initCodeCollect() {
		super.initCodeCollect();
		errorCodeMap.put(NetError.CODE_41000, "错误41000");
		errorCodeMap.put(NetError.CODE_41001, "成员数量达到上限，无法邀请新成员");
		errorCodeMap.put(NetError.CODE_41100, "联系人主键不存在或不属于当前账号");
		errorCodeMap.put(NetError.CODE_41800, "该用户不存在");
		errorCodeMap.put(NetError.CODE_41200, "联系人主键不存在或不属于当前账号");
		errorCodeMap.put(NetError.CODE_41201,
				"联系人资料填写不正确，在 errors 字段中附带出错字段的具体原因");

		errorCodeMap.put(NetError.CODE_42200, "提供的 messageid 不正确或不隶属于当前用户");
		errorCodeMap.put(NetError.CODE_42300, "提供的 messageid 不正确或不隶属于当前用户");
		errorCodeMap.put(NetError.CODE_42301, "提供的响应操作参数不可为空");
		errorCodeMap.put(NetError.CODE_42302, "处理消息失败");
		errorCodeMap.put(NetError.CODE_42400, "无效的用户 ID，通常是不存在 ");
		errorCodeMap.put(NetError.CODE_42401, "该群已经不存在");
		errorCodeMap.put(NetError.CODE_42500, "错误42500");
		errorCodeMap.put(NetError.CODE_42501, "创建群超过上限,无法创建");
		errorCodeMap.put(NetError.CODE_42600,
				"群资料填写不正确，在 errors 字段中附带出错字段的具体原因");
		errorCodeMap.put(NetError.CODE_42601, "无效的群 ID，不存在或无权修改");
		errorCodeMap.put(NetError.CODE_42700, "无效的群 ID，不存在或无权解散");
		errorCodeMap.put(NetError.CODE_42800, "无效的群 ID，不存在或无权邀请");
		errorCodeMap.put(NetError.CODE_42801, "目标用户不存在");
		errorCodeMap.put(NetError.CODE_42802, "用户已存在于该群");
		errorCodeMap.put(NetError.CODE_42900, "无效的群 ID，不存在或无权踢人权限");
		errorCodeMap.put(NetError.CODE_42901, "群主不可以被踢或退群");
		errorCodeMap.put(NetError.CODE_42902, "用户不在群中");
		errorCodeMap.put(NetError.CODE_43100,
				"群资料填写不正确，在 errors 字段中附带出错字段的具体原因");
		errorCodeMap.put(NetError.CODE_43101, "无效的群 ID，不存在或不在该群中");
		errorCodeMap.put(NetError.CODE_43200,
				"群资料填写不正确，在 errors 字段中附带出错字段的具体原因");
		errorCodeMap.put(NetError.CODE_43201, "无效的用户 ID，通常是不存在");
		errorCodeMap.put(NetError.CODE_43202, "无效的群 ID，不存在或双方并不都在该群中");

		errorCodeMap.put(NetError.CODE_43203, "允许人数达到上限，无法开启新允许");
		errorCodeMap.put(NetError.CODE_43204, "允许人数达到上限，无法开启新允许");
		errorCodeMap.put(NetError.CODE_43205, "允许人数达到上限，无法开启新允许");

		errorCodeMap.put(NetError.CODE_43300, "无效的群组 ID 或不在此群中");
		errorCodeMap.put(NetError.CODE_43600, "无效的群 ID，不存在或者您不在该群中");
		errorCodeMap.put(NetError.CODE_43601, "无效的对话用户 ID，对方不在这个群中");
		errorCodeMap.put(NetError.CODE_43700, "无效的群 ID，不存在或者您不在该群中");
		errorCodeMap.put(NetError.CODE_43701, "无效的对话用户 ID，对方不在这个群中");
		errorCodeMap.put(NetError.CODE_43800, "无效的群 ID，不存在或者您不在该群中");
		errorCodeMap.put(NetError.CODE_43801, "无效的对话用户 ID，对方不在这个群中");
		errorCodeMap.put(NetError.CODE_43802, "您不是群主，无权直接和用户对话");
		errorCodeMap.put(NetError.CODE_43803, "不能自己发消息给自己");
		errorCodeMap.put(NetError.CODE_43804, "消息数据不可为空，对于非普通消息必须是合法的 JSON 数据");
		errorCodeMap.put(NetError.CODE_43900, "无效的群 ID，群不存在");
		errorCodeMap.put(NetError.CODE_44100, "无效的订阅群 ID，群不存在或者类型不正确");
		errorCodeMap.put(NetError.CODE_44101, "已经订阅过本群了");
		errorCodeMap.put(NetError.CODE_45102, "提供的账号 ID 不正确");
		errorCodeMap.put(NetError.CODE_45103, "无合适匹配规则");

		errorCodeMap.put(LocalError.CODE_12101, "请输入群名称");
		errorCodeMap.put(LocalError.CODE_12102, "群名称只能由汉字、数字及英文字母组成");
		errorCodeMap.put(LocalError.CODE_12103, "字数超过限制");

		errorCodeMap.put(LocalError.CODE_GROUP_INTRODUCE_ILLAGE,
				"群介绍中有异常字符，请重新输入");

		errorCodeMap.put(LocalError.CODE_12201, "请输入邀请成员用户名");
		errorCodeMap.put(LocalError.CODE_12202, "该用户名不存在");
		errorCodeMap.put(LocalError.CODE_12303, "无法发送空白消息");

		errorCodeMap.put(LocalError.CODE_12401, "备注只能由汉字、数字及英文字母组成");
		errorCodeMap.put(LocalError.CODE_12402, "字数超过限制");
		errorCodeMap.put(LocalError.CODE_12403, "对方未授权您替Ta测量");
		errorCodeMap.put(LocalError.CODE_12404, "对方未授权您访问其健康中心");
		errorCodeMap.put(LocalError.CODE_12203, "已发送");
		errorCodeMap.put(LocalError.CODE_12204, "邀请失败");

		errorCodeMap.put(LocalError.CODE_12301, "最多发送400个字符");
		errorCodeMap.put(LocalError.CODE_12205, "您已经被踢出%s群!");
		errorCodeMap.put(LocalError.CODE_12206, "%s群已解散!");
		errorCodeMap.put(LocalError.CODE_12302, "输入信息为空，不可发送");
		errorCodeMap.put(LocalError.CODE_12304, "消息发送太频繁!");
		errorCodeMap.put(LocalError.CODE_12416, "帐号信息加载中，请稍后...");

	}
}
