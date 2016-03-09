/**
 * 
 */
package com.medzone.cloud.data.helper;

import java.util.ArrayList;
import java.util.List;

import com.medzone.framework.data.bean.imp.SickInfo;

/**
 * @author ChenJunQi.
 * 
 */
public class SickHelper {
	// 解析SICK
	public static List<SickInfo> parseSick(int sick) {
		List<SickInfo> chronicDiseaseList = new ArrayList<SickInfo>();
		for (int i = 0; i < SickInfo.MAX_DISEASE_NUMBER; i++) {
			SickInfo sickInfo = new SickInfo();
			boolean isDiseaseExist = (sick & (1 << i)) > 0 ? true : false;
			sickInfo.setDiseaseExist(isDiseaseExist);
			switch (i) {
			case 0:
				sickInfo.setDiseaseName("糖尿病");
				break;
			case 1:
				sickInfo.setDiseaseName("心血管疾病（冠心病/心肌梗塞等）");
				break;
			case 2:
				sickInfo.setDiseaseName("高血脂");
				break;
			case 3:
				sickInfo.setDiseaseName("脑血管疾病（脑中风/脑眩晕等）");
				break;
			case 4:
				sickInfo.setDiseaseName("呼吸系统疾病");
				break;
			case 5:
				sickInfo.setDiseaseName("高血压");
				break;
			case 6:
				sickInfo.setDiseaseName("感冒");
				break;
			case 7:
				sickInfo.setDiseaseName("肺炎");
				break;
			case 8:
				sickInfo.setDiseaseName("恶性肿瘤");
				break;
			case 9:
				sickInfo.setDiseaseName("风湿类疾病");
				break;

			default:
				break;
			}
			chronicDiseaseList.add(sickInfo);
		}
		return chronicDiseaseList;
	}

	// 打包SICK,与疾病的排列顺序耦合
	public static int packSick(List<SickInfo> chronicDiseaseList) {
		int sick = 0;

		for (int i = 0; i < SickInfo.MAX_DISEASE_NUMBER; i++) {
			if (chronicDiseaseList.get(i).isDiseaseExist()) {
				sick += 1 << i;
			}
		}
		return sick;
	}

	// 获取疾病的信息文本
	public static String getSickMessage(int sick) {

		List<SickInfo> list = parseSick(sick);
		String result = "";
		boolean isFirst = true;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isDiseaseExist()) {
				if (isFirst) {
					result += list.get(i).getDiseaseName();
					isFirst = false;
				} else {
					result += "," + list.get(i).getDiseaseName();
				}
			}
		}
		if (sick == 0) {
			return "无";
		} else if (result.length() < 7)
			return result;
		else
			return result.substring(0, 7) + "...";
	}
}
