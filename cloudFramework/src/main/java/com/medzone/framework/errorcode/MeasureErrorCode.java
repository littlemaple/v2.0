package com.medzone.framework.errorcode;

import com.medzone.framework.errorcode.ProxyCode.LocalError;
import com.medzone.framework.errorcode.ProxyCode.NetError;

public class MeasureErrorCode extends ErrorCode {

	public MeasureErrorCode() {
		super();
	}

	@Override
	protected void initCodeCollect() {
		super.initCodeCollect();
		errorCodeMap.put(NetError.CODE_41300, "提供的测量类型不正确");
		errorCodeMap.put(NetError.CODE_41301, "服务器异常，请稍后再试");
		errorCodeMap.put(NetError.CODE_41302, "代码测量的用户ID无效");
		errorCodeMap.put(NetError.CODE_41400, "提供的测量类型不正确");
		errorCodeMap.put(NetError.CODE_41500, "提供的测量类型不正确");
		errorCodeMap.put(NetError.CODE_41501, "数据主键不存在或不属于当前帐号");
		errorCodeMap.put(NetError.CODE_41502, "测量数据填写不正确");
		errorCodeMap.put(NetError.CODE_41600, "提供的测量类型不正确");
		errorCodeMap.put(NetError.CODE_41700, "服务器异常，请稍后再试");
		errorCodeMap.put(NetError.CODE_41800, " 要查询的目标帐号不存在");
		errorCodeMap.put(NetError.CODE_45102, "无效的测量类型");

		errorCodeMap.put(LocalError.CODE_11401, "此前无任何数据");
		// errorCodeMap.put(LocalError.CODE_11402, "不能穿越哦");
		errorCodeMap.put(LocalError.CODE_11403, "删除成功");
		errorCodeMap.put(LocalError.CODE_11404, "删除失败");
		errorCodeMap.put(LocalError.CODE_11405, "该月无任何数据");
		errorCodeMap.put(LocalError.CODE_DATA_UPLOAD_FAILURE, "数据上传失败，请稍后再试");

		errorCodeMap.put(LocalError.CODE_HEART_RATE_EMPTY, "心率不能为空");
		errorCodeMap.put(LocalError.CODE_SYSTOLIC_PRESSURE_EMPTY, "收缩压不能为空");
		errorCodeMap.put(LocalError.CODE_DIASTOLIC_PRESSURE_EMPTY, "舒张压不能为空");
		errorCodeMap.put(LocalError.CODE_BLOOD_OXYGEN_EMPTY, "血氧不能为空");
		errorCodeMap.put(LocalError.CODE_TEMPERATURE_EMPTY, "耳温不能为空");
		errorCodeMap.put(LocalError.CODE_SYSTOLIC_LESS_THAN_DIASTOLIC,
				"输入错误，收缩压必须大于舒张压");
		errorCodeMap.put(LocalError.CODE_18100, "当前网络不可用，无法进行心云分享");
		errorCodeMap.put(LocalError.CODE_SOMEONT_ELSE_IS_FINISED, "替他人测量完成");
		errorCodeMap.put(LocalError.CODE_MEASUREMENT_FOR_OTHERS, "当前网络不可用，无法替他人测量");
		errorCodeMap.put(LocalError.CODE_18101, "数据正在上传中,请稍后再试");
	}
}
