package com.medzone.cloud.acl.audio.decode;

import java.io.File;
import java.util.List;

public interface AudioDecodeInterface {

	public void doDecodeWithoutDirectResult(short[] buff, int buflength,
			int buffersize, int recordcount);

	public List<AudioDecodeResult> doDecode(short[] buff, int buflength,
			int buffersize, int recordcount);

	public void doDecode(File file);

	public List<AudioDecodeResult> doDecode(File file, boolean b);

	public void reset();

	public void stop();

}
