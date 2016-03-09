package com.medzone.common.media.aidl;
import com.medzone.common.media.bean.Media;
import java.util.List;
interface ServiceConnect{
	void exit();
	void setList(in List<Media> list);
	List<Media> getList();
	void setPlayMode(int mode);
	boolean prepare(int index);
	boolean start();
	boolean play(int index);
	boolean seekTo(int position);
	boolean pause();
	boolean playPre();
    boolean playNext();
	boolean stop();
    int getcurPosition();
    int getDuration();
    int getCurPlayIndex();
    int getPlayState();
    int getPlayMode();  
}