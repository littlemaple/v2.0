package com.medzone.cloud.acl.thread.audio;

public abstract class QueueMember {

	public short[] member;
	public int mlength = 0;
	public int rcount = 0;
	public int MemberBufSize = 0;

	public void setMemberLength(int length) {
		MemberBufSize = length;
		member = new short[MemberBufSize];
	}
}
