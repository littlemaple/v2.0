package com.medzone.common.media.bean;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Media implements Parcelable {

	private final static String RAW_ID = "raw_id";
	private int rawId;

	public int getRawId() {
		return rawId;
	}

	public void setRawId(int rawId) {
		this.rawId = rawId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		Bundle bundel = new Bundle();
		bundel.putInt(RAW_ID, rawId);
		dest.writeBundle(bundel);
	}

	public static final Parcelable.Creator<Media> CREATOR = new Creator<Media>() {

		@Override
		public Media[] newArray(int size) {
			return new Media[size];
		}

		@Override
		public Media createFromParcel(Parcel source) {
			Media media = new Media();
			Bundle bundle = source.readBundle();
			media.setRawId(bundle.getInt(RAW_ID));
			return media;
		}
	};

}
