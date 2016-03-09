package com.medzone.cloud.task;

import android.content.Context;

import com.medzone.cloud.network.NetworkClient;
import com.medzone.framework.task.BaseResult;

public class VerifyAccountTask extends BaseCloudTask {

	private String target, template;
	private String checkCode;
	private Boolean checkExist;

	/**
	 * 
	 * @param context
	 * @param target
	 *            - 要验证的手机号或 Email（请不要包含+86 或 0开头）。
	 * @param template
	 *            - 发送短信或邮件内容模板，{code} 自动被替换为验证码。
	 * @param checkExist
	 *            - 同时检测是否已经被注册：Y=要求已注册，N=要求未注册，默认不做检测。
	 * @param checkCode
	 *            - 用户填写的验证码，若提供此参数则该请求只做验证不会发送短信。
	 */
	public VerifyAccountTask(Context context, String target, String template,
			Boolean checkExist, String checkCode) {
		super(context, 0);
		this.target = target;
		this.template = template;
		this.checkCode = checkCode;
		this.checkExist = checkExist;
	}

	@Override
	protected BaseResult doInBackground(Void... params) {
		return NetworkClient.getInstance().verifyAccount(target, template,
				checkExist, checkCode);
	}

}
