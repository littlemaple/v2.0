package com.medzone.cloud.ui.dialog.share;

import com.medzone.framework.task.TaskHost;

public interface ICloudShare {

	public void doGetShareURL(TaskHost taskHost);

	public void doSendShareMessage(TaskHost taskHost, int groupid, String data);

}
