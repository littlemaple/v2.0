package com.medzone.cloud.acl.audio.decode;

public class AudioWaveRestorer {

	private final short PPOINT = 25000;
	private final short NPOINT = -25000;
	private short[] tmpDyncBuf = new short[48];
	private short[] newBuf = new short[480000];
	private int count = 0;
	private int bufcount = 0;
	private int fscount = 0;
	private int ppcount = 0;
	private int tmpPeak = 0;
	private int tmpValley = 0;
	private int posPeak = 0;
	private int posValley = 0;
	private int pvStart = 0;
	private int gotStartCount = 0;
	private int dyncBufStart = 0;
	private int ptimes = 0;
	private int ntimes = 0;
	private boolean isGotOneFrameStart = false;
	private boolean peakORvalley = false;
	private boolean findonepulse = false;

	public short[] doWaveRestore(short[] buff, int buflength, int buffersize,
			int recordcount) {
		int buflen = buflength;// 每次处理的数据长度，最后一次可能是不同的
		int bufsize = buffersize;// buffer的大小，应该固定
		short[] tmpBuf = new short[bufsize];
		System.arraycopy(buff, 0, tmpBuf, 0, buflen);
		bufcount = recordcount;// 记录处理buffer的次数，从1开始
		// --------第一个buffer数据，初始化-----------
		if (bufcount <= 1) {
			findonepulse = false;
			isGotOneFrameStart = false;
			fscount = 0;
			ppcount = 0;
			tmpPeak = tmpBuf[0];
			tmpValley = tmpBuf[0];
			posPeak = 0;
			posValley = 0;
			gotStartCount = 0;
			dyncBufStart = 0;
			ptimes = 0;
			ntimes = 0;
		}
		// ---------------------------------------
		for (int index = 0; index < buflen; index++) {
			count = bufsize * (bufcount - 1) + index;
			// -------先找起始-------- //
			if (!isGotOneFrameStart) {
				fscount++;
				newBuf[count] = 0;
				if (fscount <= 64) {
					if (tmpPeak < tmpBuf[index]) {
						tmpPeak = tmpBuf[index];
						posPeak = count;
					}
					if (tmpValley > tmpBuf[index]) {
						tmpValley = tmpBuf[index];
						posValley = count;
					}
				}
				if (fscount >= 64) {
					fscount = 0;
					if ((tmpPeak - tmpValley) > 2000) {
						int widthPV = Math.abs(posPeak - posValley);
						int startbyPV = (posPeak >= posValley) ? posPeak
								: posValley;
						if ((int) (count / bufsize) > (int) (startbyPV / bufsize)) {
							startbyPV = count;
						}
						int st = startbyPV % bufsize;
						findonepulse = true;
						if (12 <= widthPV && widthPV <= 20) {
							pvStart = st;
						} else if (28 <= widthPV && widthPV <= 36) {
							pvStart = st;
						} else if (44 <= widthPV && widthPV <= 52) {
							pvStart = st;
						} else if (60 <= widthPV && widthPV <= 64) {
							pvStart = st;
						} else {
							// System.out.println("pvStart in >2000==>" +
							// pvStart);
							findonepulse = false;
							tmpPeak = 0;
							tmpValley = 0;
							posPeak = 0;
							posValley = 0;
							pvStart = index;
							gotStartCount = 0;
						}
					} else {
						findonepulse = false;
						tmpPeak = 0;
						tmpValley = 0;
						posPeak = 0;
						posValley = 0;
						pvStart = index;
						gotStartCount = 0;
					}
					if (findonepulse) {
						gotStartCount++;
					} else {
						gotStartCount = 0;
					}
					// -----------连续两次找到满足条件的峰谷------------
					if (gotStartCount >= 2) {
						isGotOneFrameStart = true;
						gotStartCount = 0;
						index = pvStart;
						dyncBufStart = bufsize * (bufcount - 1) + index;// todo
						ppcount = 0;
						peakORvalley = false;
						int tmp1 = tmpValley;
						tmpPeak = tmp1;
						tmpValley = tmpPeak;
						ptimes = 0;
						ntimes = 0;
						System.out.println("got started! ==> " + count
								+ ",index==>" + index + ",dyncBufStart"
								+ dyncBufStart);
					} else {
						if (index >= 1)
							index--;
					}
				}
			}
			// ---------------------找到了起始-----------------------
			if (isGotOneFrameStart) {
				ppcount++;
				// System.out.println(ppcount);
				if (ppcount <= 40) {
					tmpDyncBuf[ppcount - 1] = tmpBuf[index];
				}
				if (ppcount >= 40) {
					// -------找出40点里面的峰点或谷点-------- //
					for (int i = 0; i < 40; i++) {
						// ----已找到峰点，则找谷点----
						if (peakORvalley) {
							if (tmpValley > tmpDyncBuf[i]) {
								tmpValley = tmpDyncBuf[i];
							}
						} else {// ----已找到谷点，则找峰点----
							if (tmpPeak < tmpDyncBuf[i]) {
								tmpPeak = tmpDyncBuf[i];
							}
						}
					}
					// -------找到之后进行阈值比较整形波形-------- //
					if (Math.abs(tmpPeak - tmpValley) > 2000) {
						boolean tmpState = peakORvalley;
						// int valueDC = (tmpPeak + tmpValley) / 2;
						if (tmpState) {
							// short thresholdNegative = (short)
							// (valueDC - 0.7 * Math
							// .abs(tmpValley - valueDC));
							short thresholdNegative = (short) (0.8 * tmpValley);
							int j = 0;
							for (j = 0; j < 40; j++) {
								if (tmpDyncBuf[j] < thresholdNegative) {
									peakORvalley = false;
									newBuf[dyncBufStart + j] = NPOINT;
									dyncBufStart = dyncBufStart + j;
									ppcount = 40 - j;
									tmpPeak = tmpValley;
									break;
								} else {
									ptimes++;
									ntimes = 0;
									newBuf[dyncBufStart + j] = PPOINT;
								}
							}
							int tmpj = j;
							while (j < 40) {
								short tmp = tmpDyncBuf[j];
								tmpDyncBuf[j - tmpj] = tmp;
								j++;
							}
						} else {
							// short thresholdPositive = (short)
							// (valueDC + 0.7 * Math
							// .abs(tmpPeak - valueDC));
							short thresholdPositive = (short) (0.8 * tmpPeak);
							int j = 0;
							for (j = 0; j < 40; j++) {
								if (tmpDyncBuf[j] > thresholdPositive) {
									peakORvalley = true;
									newBuf[dyncBufStart + j] = PPOINT;
									dyncBufStart = dyncBufStart + j;
									ppcount = 40 - j;
									tmpValley = tmpPeak;
									break;
								} else {
									ptimes = 0;
									ntimes++;
									newBuf[dyncBufStart + j] = NPOINT;
								}
							}
							int tmpj = j;
							while (j < 40) {
								short tmp = tmpDyncBuf[j];
								tmpDyncBuf[j - tmpj] = tmp;
								j++;
							}
						}
					} else {
						// System.out.println("index in <2000 ==>" + count);
						// index = (index + ptimes + ntimes) % bufsize;
						fscount = 0;
						isGotOneFrameStart = false;
						findonepulse = false;
						tmpPeak = 0;
						tmpValley = 0;
						ptimes = 0;
						ntimes = 0;

					}
					// ----------长时间只有峰值或谷值，认为完毕，需要重新找起始------------
					if (ptimes > 40 || ntimes > 40) {
						// index = (index + ptimes + ntimes) % bufsize;
						fscount = 0;
						isGotOneFrameStart = false;
						findonepulse = false;
						tmpPeak = 0;
						tmpValley = 0;
						ptimes = 0;
						ntimes = 0;
					}
				}
			}
		}
		// -----------处理完当前buffer后返回处理上次buffer的结果-------------
		if (bufcount > 1) {
			// TODO
			if (buflen < bufsize) {
				short[] lastResultBuf = new short[bufsize + buflen];
				int start = bufsize * (bufcount - 2);
				System.arraycopy(newBuf, start, lastResultBuf, 0,
						lastResultBuf.length);
				// System.out
				// .println("return==>" + "bufcount:" + bufcount
				// + ";start:" + start + ";length:"
				// + lastResultBuf.length);
				return lastResultBuf;
			} else {
				short[] lastResultBuf = new short[bufsize];
				int start = bufsize * (bufcount - 2);
				System.arraycopy(newBuf, start, lastResultBuf, 0,
						lastResultBuf.length);
				// System.out
				// .println("return==>" + "bufcount:" + bufcount
				// + ";start:" + start + ";length:"
				// + lastResultBuf.length);
				return lastResultBuf;
			}
		} else {
			return null;
		}
	}

	public short[] backResult() {
		short[] result = new short[65535];
		System.arraycopy(newBuf, 0, result, 0, newBuf.length);
		return result;
	}

	public AudioWaveRestorer() {
		// TODO
	}

}
