package com.medzone.cloud.data.helper;

import java.util.Comparator;

import com.medzone.cloud.clock.manager.Alarm;
import com.medzone.cloud.data.CurrentAccountManager;
import com.medzone.framework.data.bean.imp.GroupMember;
import com.medzone.framework.util.LetterUtil;
import com.medzone.framework.util.TimeUtils;

public class CustomComparator<T> implements Comparator<T> {

	@Override
	public int compare(T lhs, T rhs) {
		// 对昵称备注进行排序，按照字母排序，数字在后，相同的按id排序
		if (lhs instanceof GroupMember && rhs instanceof GroupMember) {

			GroupMember m1 = (GroupMember) lhs;
			GroupMember m2 = (GroupMember) rhs;

			String s1, s2;

			String displayName1 = CurrentAccountManager.getCurAccount()
					.getFriendsDisplay(m1);
			String displayName2 = CurrentAccountManager.getCurAccount()
					.getFriendsDisplay(m2);
			s1 = LetterUtil.getPingYin(displayName1.toLowerCase());
			s2 = LetterUtil.getPingYin(displayName2.toLowerCase());

			int maxLength = (s1.length() > s2.length()) ? s1.length() : s2
					.length();
			int flag = 0;
			int i = 0;
			while (i < maxLength) {
				int res = sort(s1.charAt(i), s2.charAt(i));
				i++;
				if (res == 0) {
					if (i >= s1.length() && i < s2.length()) {
						flag = -1;
						break;
					} else if (i >= s2.length() && i < s1.length()) {
						flag = 1;
						break;
					} else if (i >= s2.length() && i >= s1.length()) {
						// 两者长度相等，用id来比较大小
						if (m1.getAccountID() > m2.getAccountID())
							flag = 1;
						else
							flag = -1;
						break;
					}
					continue;
				} else {
					flag = res;
					break;
				}
			}
			return flag;
		}
		if (lhs instanceof Alarm && rhs instanceof Alarm) {

			Alarm m1 = (Alarm) lhs;
			Alarm m2 = (Alarm) rhs;

			return TimeUtils.getDate(m1.getClockTime()).compareTo(
					TimeUtils.getDate(m2.getClockTime()));
		} else {
			return -1;
		}
	}

	private int sort(char leftC, char rightC) {
		int asc1 = (int) leftC;
		int asc2 = (int) rightC;
		// asc1是字母
		if (isEnChar(asc1)) {
			if (isEnChar(asc2)) {
				return compareChar(asc1, asc2);
			} else {
				return -1;
			}
		} else {
			// asc1是数字
			if (isEnChar(asc2)) {
				return 1;
			} else {
				return compareChar(asc1, asc2);
			}
		}
	}

	// ascill码中的小写字母范围
	public boolean isEnChar(int num) {
		if (num > 96 && num < 123)
			return true;
		return false;

	}

	public int compareChar(int num1, int num2) {
		if (num1 > num2)
			return 1;
		if (num1 < num2)
			return -1;
		return 0;
	}

}
