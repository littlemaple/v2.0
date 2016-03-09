package com.medzone.cloud.acl.audio.decode;

import java.util.ArrayList;
import java.util.List;

public class AudioDecodeResult {
	public int type;// 1:查询终端;21:开始检测第一包,22:开始检测第二包;-1:错误;255:回音
	public List<Short> decimalResultList;

	public AudioDecodeResult() {
		this.type = 0;
		this.decimalResultList = new ArrayList<Short>();
	}

	public AudioDecodeResult(int type, List<Short> list) {
		this.type = type;
		List<Short> tmplist = list;
		this.decimalResultList = tmplist;
	}

}
