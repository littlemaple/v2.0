package com.medzone.cloud.acl.audio.decode;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class AudioDecoder implements AudioDecodeInterface {
	private File audioFile = null;
	private FileInputStream fis;
	private List<String> original = new ArrayList<String>(65535);
	private DataOutputStream ds = null;
	private boolean ifwritetofile = false;
	private final int UNKNOW = 0;
	private final int BIT0 = 1;
	private final int BIT01 = 2;
	private final int BIT011 = 3;
	private final int BIT0111 = 4;
	private final int BIT01111 = 5;
	private final int BIT011111 = 6;
	private final int BIT0111111 = 7;
	private final int BIT01111110 = 8;
	private int SHORT = 24;// defalut 24
	private int LONG = 80;// default 80
	// private final short PPOINT = 25000;
	// private final short NPOINT = -25000;
	private boolean isRecording = false;
	private List<String> decodeResultBinaryDirect = new ArrayList<String>();
	private List<String> decodeResultBinaryOpposite = new ArrayList<String>();
	private List<Short> decodeResultDecimalDirect = new ArrayList<Short>();
	private List<Short> decodeResultDecimalOpposite = new ArrayList<Short>();
	private AudioWaveRestorer mAudioWaveRestorer;
	private Decoder mDecoder;

	public AudioDecoder(File file) {
		super();
		// ------------------
		getSourceDataFromFile(file);
		// -------------------
		mAudioWaveRestorer = new AudioWaveRestorer();
		mDecoder = new Decoder();
	}

	public AudioDecoder() {
		super();
		mAudioWaveRestorer = new AudioWaveRestorer();
		mDecoder = new Decoder();
	}

	private void getSourceDataFromFile(File file) {
		if (file != null) {
			this.audioFile = file;
		}
		if (ifwritetofile) {
			File tmpFile = audioFile;
			String fn = tmpFile.getName();
			String nfn = fn.substring(0, fn.indexOf("."));
			File tmpOutFile = new File(tmpFile.getParentFile(), "/" + nfn
					+ "_O" + ".txt");
			try {
				DataOutputStream dos = new DataOutputStream(
						new BufferedOutputStream(new FileOutputStream(
								tmpOutFile)));
				this.ds = dos;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private static String InputStreamToString(InputStream in) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int count = -1;
		while ((count = in.read(data, 0, 1024)) != -1) {
			outStream.write(data, 0, count);
		}
		data = null;
		String str = new String(outStream.toByteArray(), "GBK");// GBK,ISO-8859-1
		return str;
	}

	private void getDecodeArrayBuf() {
		try {
			if (audioFile != null) {
				fis = new FileInputStream(audioFile);
				try {
					String str = AudioDecoder.InputStreamToString(fis);
					original.clear();
					int length = str.length();
					int index1 = str.indexOf("[") + 1;
					int index2 = 0;
					while (index1 < length) {
						index2 = str.indexOf(",", index1);
						if (index2 != -1) {
							String s = str.substring(index1, index2);
							original.add(s);
							index1 = index2 + 1;
						} else {
							index2 = str.indexOf("]", index1);
							if (index2 != -1) {
								String s = str.substring(index1, index2);
								original.add(s);
							}
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Err: audioFile is null!");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// ----------一边读一边恢复波形并解码------------
	private void doDecoding() {
		// ----------恢复波形参数----------
		getDecodeArrayBuf();
		// --------------------------
		int totallength = original.size();
		int bufsize = 4096;
		short[] tmpBuf = new short[bufsize]; // 待处理的数据
		short[] newBuf = new short[totallength];
		int count = 0;
		int bufcount = 0;
		int start = 0;
		isRecording = true;
		// ------------解码参数-------------
		int decodecountdirect = 0;
		int decodecountopposite = 0;
		int sampledirect = 0;
		int lastsampledirect = 0;
		int sampleopposite = 0;
		int lastsampleopposite = 0;
		int decstate = UNKNOW;
		int decstate2 = UNKNOW;
		int lastcount1 = 0;
		int lastcount2 = 0;
		int diffdirect = 0;
		int diffopposite = 0;
		int recordcountdirect = 0;
		int recordcountopposite = 0;
		boolean isStartBitDirect = false;
		boolean isStartBitOpposite = false;
		boolean isDecodingDirect = false;
		boolean isDecodingOpposite = false;
		boolean isDirect = true;
		boolean isOpposite = true;
		// -------------取得波形数据--------------
		while ((count < totallength) && isRecording) {
			bufcount++;
			int currentcount = 0;
			int tmpcount = bufsize * (bufcount - 1);
			// ---------先取buffer大小数据----------//
			for (currentcount = 0; currentcount < bufsize; currentcount++) {
				tmpcount = bufsize * (bufcount - 1) + currentcount;
				if (tmpcount < totallength) {
					tmpBuf[currentcount] = Short
							.valueOf(original.get(tmpcount));
				} else {
					break;
				}
			}
			// ----------------------------------
			count = bufsize * (bufcount - 1) + currentcount + 1;
			// ----------------结果返回并解码操作-------------------
			// System.out.println("down==>" + "bufcount:" + bufcount + ";count:"
			// + count + ";currentcount:" + currentcount);
			short[] lastBufResult = mAudioWaveRestorer.doWaveRestore(tmpBuf,
					currentcount, bufsize, bufcount);
			if (null != lastBufResult) {
				System.arraycopy(lastBufResult, 0, newBuf, start,
						lastBufResult.length);
				start += lastBufResult.length;
			}

			// -------------解码-------------//
			if (null != lastBufResult) {
				int length = lastBufResult.length;
				short[] buffer = new short[length];
				System.arraycopy(lastBufResult, 0, buffer, 0, length);
				// -----------dangdangdangdangdang -----------
				// ------对每一位进行解码------
				for (int i = 0; i < length; i++) {
					decodecountdirect++;
					decodecountopposite++;
					if (buffer[i] > 0) {
						sampledirect = 1;
						sampleopposite = 1;
					} else if (buffer[i] < 0) {
						sampledirect = 0;
						sampleopposite = 0;
					} else {
						sampledirect = lastsampledirect;
						sampleopposite = lastsampleopposite;
					}

					// ---------如果正向----------//
					if (isDirect) {
						// 找到起始位 并且排除噪声 //
						if ((!isStartBitDirect)
								&& (sampledirect != lastsampledirect)) {
							if (sampledirect == 1) {
								lastcount1 = decodecountdirect;
							} else {
								diffdirect = decodecountdirect - lastcount1;
								if ((SHORT <= diffdirect)
										&& (diffdirect <= LONG)) {
									isStartBitDirect = true;
									decstate = BIT0;
								} else {
									lastcount1 = decodecountdirect;
								}
							}
						}
						// 判断是否有跳变 //
						if ((isStartBitDirect)
								&& (sampledirect != lastsampledirect)) {
							diffdirect = decodecountdirect - lastcount1;
							if (diffdirect > LONG) {
								lastcount1 = decodecountdirect;
								sampledirect = 0;
								lastsampledirect = 0;
								decstate = UNKNOW;
								diffdirect = 0;
								isStartBitDirect = false;
								isDecodingDirect = false;
								decodeResultBinaryDirect.clear();
							}
							// 宽度符合信号范围 //
							if ((SHORT <= diffdirect) && (diffdirect <= LONG)) {
								lastcount1 = decodecountdirect;
								switch (decstate) {
								case UNKNOW:
									if (lastsampledirect == 0
											&& sampledirect == 1) {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									} else {
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									}
									break;
								case BIT0:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										decstate = BIT01;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									} else {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}
									break;
								case BIT01:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										decstate = BIT011;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									} else {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}

									break;
								case BIT011:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										decstate = BIT0111;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									} else {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}

									break;
								case BIT0111:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
										decstate = BIT01111;
									} else {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}

									break;
								case BIT01111:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										decstate = BIT011111;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									} else {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}
									break;
								case BIT011111:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										decstate = BIT0111111;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									} else {
										decstate = UNKNOW;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}
									break;
								case BIT0111111:
									if (lastsampledirect == 0
											&& sampledirect == 1) {
										decstate = BIT01111110;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
										int drbsize = decodeResultBinaryDirect
												.size();
										if (drbsize >= 80) {
											isDecodingDirect = false;
										} else {
											isDecodingDirect = true;
										}
										// isDecoding = !isDecoding;
										System.out
												.println("< D > find 0x7E in Direct:"
														+ decodecountdirect);
										System.out
												.println("< S > decodeResultBinaryDirect size:"
														+ drbsize);
									} else {
										decstate = UNKNOW;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									}
									break;
								case BIT01111110:
									if (lastsampledirect == 0
											&& sampledirect == 1) {
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
										decstate = BIT0;
									} else {
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
										decstate = UNKNOW;
									}
									break;
								}
							}
						}
					}
					// ---------如果反向---------- //
					if (isOpposite) {
						// 找到起始位 //
						if ((!isStartBitOpposite)
								&& (sampleopposite != lastsampleopposite)) {
							if (sampleopposite == 0)
								lastcount2 = decodecountopposite;
							else {
								diffopposite = decodecountopposite - lastcount2;
								if ((SHORT <= diffopposite)
										&& (diffopposite <= LONG)) {
									isStartBitOpposite = true;
									decstate2 = BIT0;
								} else
									lastcount2 = decodecountopposite;
							}
						}
						// 判断是否有跳变 //
						if (isStartBitOpposite
								&& (sampleopposite != lastsampleopposite)) {
							diffopposite = decodecountopposite - lastcount2;
							if (diffopposite > LONG) {
								lastcount2 = decodecountopposite;
								sampleopposite = 0;
								lastsampleopposite = 0;
								decstate2 = UNKNOW;
								diffopposite = 0;
								isStartBitOpposite = false;
								isDecodingOpposite = false;
								decodeResultBinaryOpposite.clear();
							}
							// 宽度符合信号范围 //
							if ((SHORT <= diffopposite)
									&& (diffopposite <= LONG)) {
								lastcount2 = decodecountopposite;
								switch (decstate2) {
								case UNKNOW:
									if (lastsampleopposite == 1
											&& sampleopposite == 0) {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									} else {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
									}
									break;
								case BIT0:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {
										decstate2 = BIT01;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									}

									break;
								case BIT01:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = BIT011;
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");

									}
									break;
								case BIT011:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {

										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = BIT0111;
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									}
									break;
								case BIT0111:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = BIT01111;
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									}
									break;
								case BIT01111:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {

										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = BIT011111;
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									}
									break;
								case BIT011111:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {

										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = BIT0111111;
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									}
									break;
								case BIT0111111:
									if (lastsampleopposite == 1
											&& sampleopposite == 0) {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
										decstate2 = BIT01111110;
										int drbsize = decodeResultBinaryOpposite
												.size();
										if (drbsize >= 80) {
											isDecodingOpposite = false;
										} else {
											isDecodingOpposite = true;

										}
										// isDecoding = !isDecoding;
										System.out
												.println("< D > find 0x7E in Opposite:"
														+ decodecountopposite);
										System.out
												.println("< S > decodeResultBinaryOpposite size:"
														+ drbsize);
									} else {
										decstate2 = UNKNOW;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
									}
									break;
								case BIT01111110:
									if (lastsampleopposite == 1
											&& sampleopposite == 0) {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
										decstate2 = BIT0;
									} else {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = UNKNOW;
									}
									break;
								}
							}
						}
					}
					// ---------走完正向反向状态机-------- //
					lastsampledirect = sampledirect;
					lastsampleopposite = sampleopposite;
					// -----------正向数据解析-------------
					// TODO
					int highlevelcountdirect = 0;
					int decbitcountdirect = 0;
					if ((!isDecodingDirect)
							&& (!decodeResultBinaryDirect.isEmpty())) {
						recordcountdirect++;
						if (recordcountdirect <= 3) {// default:if (recordcount
														// <= 2)
							short tempData = 0;
							// 解码 因为避免帧头01111110而将连续的5位1编码还原成真实的数据；
							// 为什么-8呢，因为需要扣掉帧尾
							for (int bitIndex = 0; bitIndex < decodeResultBinaryDirect
									.size() - 8; bitIndex++) {
								decbitcountdirect++;
								tempData = (short) (tempData + Short
										.valueOf(decodeResultBinaryDirect
												.get(bitIndex))
										* Math.pow(2.0, 8 - decbitcountdirect));

								if (decodeResultBinaryDirect.get(bitIndex) == "1") {
									highlevelcountdirect++;
								} else {
									highlevelcountdirect = 0;
								}

								if (highlevelcountdirect == 5) {
									bitIndex++;
									highlevelcountdirect = 0;
								}

								if (decbitcountdirect == 8) {
									decbitcountdirect = 0;
									// 每一帧的数据转化为对于的10进制数据；
									decodeResultDecimalDirect.add(Short
											.valueOf(tempData));
									tempData = 0;
									highlevelcountdirect = 0;
								}
							}

							// int mComType = this.mCommunicateType;
							int resultListSize = decodeResultDecimalDirect
									.size();
							if (19 <= resultListSize && resultListSize < 22) {
								System.out.println("Query ID");
								System.out.println("Direct size:"
										+ decodeResultDecimalDirect.size()
										+ "==>"
										+ decodeResultDecimalDirect.toString());
								decodeResultBinaryDirect.clear();
								decodeResultDecimalDirect.clear();
							} else if (13 <= resultListSize
									&& resultListSize < 16) {
								System.out
										.println("Start Measure ===> first response");
								System.out.println("Direct size:"
										+ decodeResultDecimalDirect.size()
										+ "==>"
										+ decodeResultDecimalDirect.toString());
								decodeResultBinaryDirect.clear();
								decodeResultDecimalDirect.clear();
							} else if (16 <= resultListSize
									&& resultListSize < 18) {
								System.out
										.println("Start Measure ===> second response");
								System.out.println("Direct size:"
										+ decodeResultDecimalDirect.size()
										+ "==>"
										+ decodeResultDecimalDirect.toString());
								decodeResultBinaryDirect.clear();
								decodeResultDecimalDirect.clear();
							} else {
								System.out.println("Err");
								System.out.println("Direct size:"
										+ decodeResultDecimalDirect.size()
										+ "==>"
										+ decodeResultDecimalDirect.toString());
								decodeResultBinaryDirect.clear();
								decodeResultDecimalDirect.clear();
							}
						} else {
							System.out.println("recordcount > 3");
							System.out.println("Direct:"
									+ decodeResultDecimalDirect.size() + "==>"
									+ decodeResultDecimalDirect.toString());
							decodeResultBinaryDirect.clear();
							decodeResultDecimalDirect.clear();
						}
					}
					// -----------反向数据解析-------------
					// TODO
					int highlevelcountopposite = 0;
					int decbitcountopposite = 0;
					if ((!isDecodingOpposite)
							&& (!decodeResultBinaryOpposite.isEmpty())) {
						recordcountopposite++;
						if (recordcountopposite <= 3) {
							short tempData = 0;
							// 解码 因为避免帧头01111110而将连续的5位1编码还原成真实的数据；
							// 为什么-8呢，因为需要扣掉帧尾
							for (int bitIndex = 0; bitIndex < decodeResultBinaryOpposite
									.size() - 8; bitIndex++) {
								decbitcountopposite++;
								tempData = (short) (tempData + Short
										.valueOf(decodeResultBinaryOpposite
												.get(bitIndex))
										* Math.pow(2.0, 8 - decbitcountopposite));

								if (decodeResultBinaryOpposite.get(bitIndex) == "1") {
									highlevelcountopposite++;
								} else {
									highlevelcountopposite = 0;
								}

								if (highlevelcountopposite == 5) {
									bitIndex++;
									highlevelcountopposite = 0;
								}

								if (decbitcountopposite == 8) {
									decbitcountopposite = 0;
									// 每一帧的数据转化为对于的10进制数据；
									decodeResultDecimalOpposite.add(Short
											.valueOf(tempData));
									tempData = 0;
									highlevelcountopposite = 0;
								}
							}

							// int mComType = this.mCommunicateType;
							int resultListSize = decodeResultDecimalOpposite
									.size();
							if (19 <= resultListSize && resultListSize < 22) {
								System.out.println("Query ID");
								System.out.println("Opposite size:"
										+ decodeResultDecimalOpposite.size()
										+ "==>"
										+ decodeResultDecimalOpposite
												.toString());
								decodeResultBinaryOpposite.clear();
								decodeResultDecimalOpposite.clear();
							} else if (13 <= resultListSize
									&& resultListSize < 16) {
								System.out
										.println("Start Measure ===> first response");
								System.out.println("Opposite size:"
										+ decodeResultDecimalOpposite.size()
										+ "==>"
										+ decodeResultDecimalOpposite
												.toString());
								decodeResultBinaryOpposite.clear();
								decodeResultDecimalOpposite.clear();
							} else if (16 <= resultListSize
									&& resultListSize <= 18) {
								System.out
										.println("Start Measure ===> second response");
								System.out.println("Opposite size:"
										+ decodeResultDecimalOpposite.size()
										+ "==>"
										+ decodeResultDecimalOpposite
												.toString());
								decodeResultBinaryOpposite.clear();
								decodeResultDecimalOpposite.clear();
							} else {
								System.out.println("Err");
								System.out.println("Opposite size:"
										+ decodeResultDecimalOpposite.size()
										+ "==>"
										+ decodeResultDecimalOpposite
												.toString());
								decodeResultBinaryOpposite.clear();
								decodeResultDecimalOpposite.clear();
							}
						} else {
							System.out.println("recordcountopposite > 3");
							System.out.println("Opposite size:"
									+ decodeResultDecimalOpposite.size()
									+ "==>"
									+ decodeResultDecimalOpposite.toString());
							decodeResultBinaryOpposite.clear();
							decodeResultDecimalOpposite.clear();
						}
					}
				}
				// -----------!dangdangdangdangdang ----------- //
			}
		}
		int directresultsize = decodeResultDecimalDirect.size();
		if ((directresultsize > 0 && directresultsize < 12)
				|| directresultsize > 24) {
			System.out.println("Decode Result Error!");
			System.out.println("size:" + decodeResultDecimalDirect.size()
					+ "==>" + decodeResultDecimalDirect.toString());
			decodeResultDecimalDirect.clear();
		} else {
			decodeResultDecimalDirect.clear();
		}
		int oppositeresultsize = decodeResultDecimalOpposite.size();
		if ((oppositeresultsize > 0 && oppositeresultsize < 12)
				|| oppositeresultsize > 24) {
			System.out.println("Decode Result Error!");
			System.out.println("size:" + decodeResultDecimalOpposite.size()
					+ "==>" + decodeResultDecimalOpposite.toString());
			decodeResultDecimalOpposite.clear();
		} else {
			decodeResultDecimalOpposite.clear();
		}
		// TODO
		System.out.println(Arrays.toString(newBuf));
	}

	protected class Decoder {
		@SuppressWarnings("unused")
		private int count = 0;
		private int start = 0;
		private short[] newBuf = new short[480000];
		private int decodecountdirect = 0;
		private int decodecountopposite = 0;
		private int sampledirect = 0;
		private int lastsampledirect = 0;
		private int sampleopposite = 0;
		private int lastsampleopposite = 0;
		private int decstate = UNKNOW;
		private int decstate2 = UNKNOW;
		private int lastcount1 = 0;
		private int lastcount2 = 0;
		private int diffdirect = 0;
		private int diffopposite = 0;
		private int recordcountdirect = 0;
		private int recordcountopposite = 0;
		private boolean isStartBitDirect = false;
		private boolean isStartBitOpposite = false;
		private boolean isDecodingDirect = false;
		private boolean isDecodingOpposite = false;
		private boolean isDirect = true;
		private boolean isOpposite = true;
		private boolean DIRECT = true;
		private boolean OPPOSITE = false;
		private List<AudioDecodeResult> mListAudioDecodeResult;

		public Decoder() {
			isRecording = true;
		}

		public short[] backAllRestoreSamples() {
			int length = start;
			short[] result = new short[start];
			System.arraycopy(newBuf, 0, result, 0, length);
			return result;
		}

		public List<AudioDecodeResult> decode(short[] buff, int buflength,
				int buffersize, int recordcount) {
			// TODO
			int bufsize = buffersize;
			int buflen = buflength;
			int bufcount = recordcount;
			short[] tmpBuf = new short[bufsize]; // 待处理的数据
			mListAudioDecodeResult = new LinkedList<AudioDecodeResult>();
			// -------------取得波形数据--------------
			System.arraycopy(buff, 0, tmpBuf, 0, buflen);
			// ----------------预处理：波形恢复--------------------
			short[] lastBufResult = mAudioWaveRestorer.doWaveRestore(tmpBuf,
					buflen, bufsize, bufcount);
			// --------------//
			if (null != lastBufResult) {
				System.arraycopy(lastBufResult, 0, newBuf, start,
						lastBufResult.length);
				start += lastBufResult.length;
			}
			// ------------------解码--------------------
			if (null != lastBufResult) {
				int length = lastBufResult.length;
				short[] buffer = new short[length];
				System.arraycopy(lastBufResult, 0, buffer, 0, length);
				// -----------dangdangdangdangdang -----------
				// ------对每一位进行解码------
				for (int i = 0; i < length; i++) {
					decodecountdirect++;
					decodecountopposite++;
					if (buffer[i] > 0) {
						sampledirect = 1;
						sampleopposite = 1;
					} else if (buffer[i] < 0) {
						sampledirect = 0;
						sampleopposite = 0;
					} else {
						sampledirect = lastsampledirect;
						sampleopposite = lastsampleopposite;
					}

					// ---------如果正向----------//
					if (isDirect) {
						// 找到起始位 并且排除噪声 //
						if ((!isStartBitDirect)
								&& (sampledirect != lastsampledirect)) {
							if (sampledirect == 1) {
								lastcount1 = decodecountdirect;
							} else {
								diffdirect = decodecountdirect - lastcount1;
								if ((SHORT <= diffdirect)
										&& (diffdirect <= LONG)) {
									isStartBitDirect = true;
									decstate = BIT0;
								} else {
									lastcount1 = decodecountdirect;
								}
							}
						}
						// 判断是否有跳变 //
						if ((isStartBitDirect)
								&& (sampledirect != lastsampledirect)) {
							diffdirect = decodecountdirect - lastcount1;
							if (diffdirect > LONG) {
								lastcount1 = decodecountdirect;
								sampledirect = 0;
								lastsampledirect = 0;
								decstate = UNKNOW;
								diffdirect = 0;
								isStartBitDirect = false;
								isDecodingDirect = false;
								decodeResultBinaryDirect.clear();
							}
							// 宽度符合信号范围 //
							if ((SHORT <= diffdirect) && (diffdirect <= LONG)) {
								lastcount1 = decodecountdirect;
								switch (decstate) {
								case UNKNOW:
									if (lastsampledirect == 0
											&& sampledirect == 1) {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									} else {
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									}
									break;
								case BIT0:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										decstate = BIT01;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									} else {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}
									break;
								case BIT01:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										decstate = BIT011;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									} else {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}

									break;
								case BIT011:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										decstate = BIT0111;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									} else {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}

									break;
								case BIT0111:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
										decstate = BIT01111;
									} else {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}

									break;
								case BIT01111:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										decstate = BIT011111;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									} else {
										decstate = BIT0;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}
									break;
								case BIT011111:
									if (lastsampledirect == 1
											&& sampledirect == 0) {
										decstate = BIT0111111;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									} else {
										decstate = UNKNOW;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
									}
									break;
								case BIT0111111:
									if (lastsampledirect == 0
											&& sampledirect == 1) {
										decstate = BIT01111110;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
										int drbsize = decodeResultBinaryDirect
												.size();
										if (drbsize >= 80) {
											isDecodingDirect = false;
										} else {
											isDecodingDirect = true;
											decodeResultBinaryDirect.clear();
										}
										// isDecoding = !isDecoding;
										System.out
												.println("< D > find 0x7E in Direct:"
														+ decodecountdirect);
										System.out
												.println("< S > decodeResultBinaryDirect size:"
														+ drbsize);
									} else {
										decstate = UNKNOW;
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
									}
									break;
								case BIT01111110:
									if (lastsampledirect == 0
											&& sampledirect == 1) {
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("0");
										decstate = BIT0;
									} else {
										if (isDecodingDirect)
											decodeResultBinaryDirect.add("1");
										decstate = UNKNOW;
									}
									break;
								}
							}
						}
					}
					// ---------如果反向---------- //
					if (isOpposite) {
						// 找到起始位 //
						if ((!isStartBitOpposite)
								&& (sampleopposite != lastsampleopposite)) {
							if (sampleopposite == 0)
								lastcount2 = decodecountopposite;
							else {
								diffopposite = decodecountopposite - lastcount2;
								if ((SHORT <= diffopposite)
										&& (diffopposite <= LONG)) {
									isStartBitOpposite = true;
									decstate2 = BIT0;
								} else
									lastcount2 = decodecountopposite;
							}
						}
						// 判断是否有跳变 //
						if (isStartBitOpposite
								&& (sampleopposite != lastsampleopposite)) {
							diffopposite = decodecountopposite - lastcount2;
							if (diffopposite > LONG) {
								lastcount2 = decodecountopposite;
								sampleopposite = 0;
								lastsampleopposite = 0;
								decstate2 = UNKNOW;
								diffopposite = 0;
								isStartBitOpposite = false;
								isDecodingOpposite = false;
								decodeResultBinaryOpposite.clear();
							}
							// 宽度符合信号范围 //
							if ((SHORT <= diffopposite)
									&& (diffopposite <= LONG)) {
								lastcount2 = decodecountopposite;
								switch (decstate2) {
								case UNKNOW:
									if (lastsampleopposite == 1
											&& sampleopposite == 0) {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									} else {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
									}
									break;
								case BIT0:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {
										decstate2 = BIT01;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									}

									break;
								case BIT01:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = BIT011;
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");

									}
									break;
								case BIT011:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {

										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = BIT0111;
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									}
									break;
								case BIT0111:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = BIT01111;
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									}
									break;
								case BIT01111:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {

										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = BIT011111;
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									}
									break;
								case BIT011111:
									if (lastsampleopposite == 0
											&& sampleopposite == 1) {

										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = BIT0111111;
									} else {
										decstate2 = BIT0;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
									}
									break;
								case BIT0111111:
									if (lastsampleopposite == 1
											&& sampleopposite == 0) {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
										decstate2 = BIT01111110;
										int drbsize = decodeResultBinaryOpposite
												.size();
										if (drbsize >= 80) {
											isDecodingOpposite = false;
										} else {
											isDecodingOpposite = true;
											decodeResultBinaryOpposite.clear();
										}
										// isDecoding = !isDecoding;
										System.out
												.println("< D > find 0x7E in Opposite:"
														+ decodecountopposite);
										System.out
												.println("< S > decodeResultBinaryOpposite size:"
														+ drbsize);
									} else {
										decstate2 = UNKNOW;
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
									}
									break;
								case BIT01111110:
									if (lastsampleopposite == 1
											&& sampleopposite == 0) {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("0");
										decstate2 = BIT0;
									} else {
										if (isDecodingOpposite)
											decodeResultBinaryOpposite.add("1");
										decstate2 = UNKNOW;
									}
									break;
								}
							}
						}
					}
					// ---------走完正向反向状态机-------- //
					lastsampledirect = sampledirect;
					lastsampleopposite = sampleopposite;
					// -----------正向数据解析-------------
					// TODO
					int highlevelcountdirect = 0;
					int decbitcountdirect = 0;
					if ((!isDecodingDirect)
							&& (!decodeResultBinaryDirect.isEmpty())) {
						recordcountdirect++;
						if (recordcountdirect <= 3) {
							short tempData = 0;
							// 解码 因为避免帧头01111110而将连续的5位1编码还原成真实的数据；
							// 为什么-8呢，因为需要扣掉帧尾
							for (int bitIndex = 0; bitIndex < decodeResultBinaryDirect
									.size() - 8; bitIndex++) {
								decbitcountdirect++;
								tempData = (short) (tempData + Short
										.valueOf(decodeResultBinaryDirect
												.get(bitIndex))
										* Math.pow(2.0, 8 - decbitcountdirect));

								if (decodeResultBinaryDirect.get(bitIndex) == "1") {
									highlevelcountdirect++;
								} else {
									highlevelcountdirect = 0;
								}

								if (highlevelcountdirect == 5) {
									bitIndex++;
									highlevelcountdirect = 0;
								}

								if (decbitcountdirect == 8) {
									decbitcountdirect = 0;
									// 每一帧的数据转化为对于的10进制数据；
									decodeResultDecimalDirect.add(Short
											.valueOf(tempData));
									tempData = 0;
									highlevelcountdirect = 0;
								}
							}

							int resultListSize = decodeResultDecimalDirect
									.size();
							if (19 <= resultListSize && resultListSize < 22) {
								System.out.println("Query ID");
								// System.out.println("Direct size:"
								// + decodeResultDecimalDirect.size()
								// + "==>"
								// + decodeResultDecimalDirect.toString());
								// List<Short> tmpList = new ArrayList<Short>();
								// tmpList.addAll(decodeResultDecimalDirect);
								// AudioDecodeResult tmpAudioDecodeResult = new
								// AudioDecodeResult(
								// 1, tmpList);
								// decodeResultBinaryDirect.clear();
								// decodeResultDecimalDirect.clear();
								AudioDecodeResult tmpADR = returnDecodeResult(
										DIRECT, 1);
								mListAudioDecodeResult.add(tmpADR);
							} else if (13 <= resultListSize
									&& resultListSize < 16) {
								System.out
										.println("Start Measure ===> first response");
								// System.out.println("Direct size:"
								// + decodeResultDecimalDirect.size()
								// + "==>"
								// + decodeResultDecimalDirect.toString());
								AudioDecodeResult tmpADR = returnDecodeResult(
										DIRECT, 21);
								mListAudioDecodeResult.add(tmpADR);
							} else if (16 <= resultListSize
									&& resultListSize < 18) {
								System.out
										.println("Start Measure ===> second response");
								// System.out.println("Direct size:"
								// + decodeResultDecimalDirect.size()
								// + "==>"
								// + decodeResultDecimalDirect.toString());
								AudioDecodeResult tmpADR = returnDecodeResult(
										DIRECT, 22);
								mListAudioDecodeResult.add(tmpADR);
							} else if (11 <= resultListSize
									&& resultListSize <= 12) {
								System.out.println("reverberate:");
								// System.out.println("Opposite size:"
								// + decodeResultDecimalOpposite.size()
								// + "==>"
								// + decodeResultDecimalOpposite
								// .toString());
								AudioDecodeResult tmpADR = returnDecodeResult(
										DIRECT, 255);
								mListAudioDecodeResult.add(tmpADR);
							} else {
								System.out.println("Err");
								// System.out.println("Direct size:"
								// + decodeResultDecimalDirect.size()
								// + "==>"
								// + decodeResultDecimalDirect.toString());
								AudioDecodeResult tmpADR = returnDecodeResult(
										DIRECT, -1);
								mListAudioDecodeResult.add(tmpADR);
							}
						} else {
							System.out.println("recordcount > 3");
							// System.out.println("Direct size:"
							// + decodeResultDecimalDirect.size()
							// + "==>"
							// + decodeResultDecimalDirect.toString());
							AudioDecodeResult tmpADR = returnDecodeResult(
									DIRECT, 0);
							mListAudioDecodeResult.add(tmpADR);
						}
					}
					// -----------反向数据解析-------------
					// TODO
					int highlevelcountopposite = 0;
					int decbitcountopposite = 0;
					if ((!isDecodingOpposite)
							&& (!decodeResultBinaryOpposite.isEmpty())) {
						recordcountopposite++;
						if (recordcountopposite <= 3) {
							short tempData = 0;
							// 解码 因为避免帧头01111110而将连续的5位1编码还原成真实的数据；
							// 为什么-8呢，因为需要扣掉帧尾
							for (int bitIndex = 0; bitIndex < decodeResultBinaryOpposite
									.size() - 8; bitIndex++) {
								decbitcountopposite++;
								tempData = (short) (tempData + Short
										.valueOf(decodeResultBinaryOpposite
												.get(bitIndex))
										* Math.pow(2.0, 8 - decbitcountopposite));

								if (decodeResultBinaryOpposite.get(bitIndex) == "1") {
									highlevelcountopposite++;
								} else {
									highlevelcountopposite = 0;
								}

								if (highlevelcountopposite == 5) {
									bitIndex++;
									highlevelcountopposite = 0;
								}

								if (decbitcountopposite == 8) {
									decbitcountopposite = 0;
									// 每一帧的数据转化为对于的10进制数据；
									decodeResultDecimalOpposite.add(Short
											.valueOf(tempData));
									tempData = 0;
									highlevelcountopposite = 0;
								}
							}

							// int mComType = this.mCommunicateType;
							int resultListSize = decodeResultDecimalOpposite
									.size();
							if (19 <= resultListSize && resultListSize < 22) {
								System.out.println("Query ID");
								// System.out.println("Opposite size:"
								// + decodeResultDecimalOpposite.size()
								// + "==>"
								// + decodeResultDecimalOpposite
								// .toString());
								AudioDecodeResult tmpADR = returnDecodeResult(
										OPPOSITE, 1);
								mListAudioDecodeResult.add(tmpADR);
							} else if (13 <= resultListSize
									&& resultListSize < 16) {
								System.out
										.println("Start Measure ===> first response");
								// System.out.println("Opposite size:"
								// + decodeResultDecimalOpposite.size()
								// + "==>"
								// + decodeResultDecimalOpposite
								// .toString());
								AudioDecodeResult tmpADR = returnDecodeResult(
										OPPOSITE, 21);
								mListAudioDecodeResult.add(tmpADR);
							} else if (16 <= resultListSize
									&& resultListSize <= 18) {
								System.out
										.println("Start Measure ===> second response");
								// System.out.println("Opposite size:"
								// + decodeResultDecimalOpposite.size()
								// + "==>"
								// + decodeResultDecimalOpposite
								// .toString());
								AudioDecodeResult tmpADR = returnDecodeResult(
										OPPOSITE, 22);
								mListAudioDecodeResult.add(tmpADR);
							} else if (11 <= resultListSize
									&& resultListSize <= 12) {
								System.out.println("reverberate:");
								// System.out.println("Opposite size:"
								// + decodeResultDecimalOpposite.size()
								// + "==>"
								// + decodeResultDecimalOpposite
								// .toString());
								AudioDecodeResult tmpADR = returnDecodeResult(
										OPPOSITE, 255);
								mListAudioDecodeResult.add(tmpADR);
							} else {
								System.out.println("Err");
								// System.out.println("Opposite size:"
								// + decodeResultDecimalOpposite.size()
								// + "==>"
								// + decodeResultDecimalOpposite
								// .toString());
								AudioDecodeResult tmpADR = returnDecodeResult(
										OPPOSITE, -1);
								mListAudioDecodeResult.add(tmpADR);
							}
						} else {
							System.out.println("recordcountopposite > 3");
							// System.out.println("Opposite size:"
							// + decodeResultDecimalOpposite.size()
							// + "==>"
							// + decodeResultDecimalOpposite
							// .toString());
							AudioDecodeResult tmpADR = returnDecodeResult(
									OPPOSITE, 0);
							mListAudioDecodeResult.add(tmpADR);
						}
					}
				}
				count = bufsize * (bufcount - 1) + length;
				// -----------!dangdangdangdangdang ----------- //
			}
			return mListAudioDecodeResult;
		}

		private AudioDecodeResult returnDecodeResult(boolean directORopposite,
				int type) {
			List<Short> tmpList = new ArrayList<Short>();
			if (directORopposite) {
				tmpList.addAll(decodeResultDecimalDirect);
				decodeResultBinaryDirect.clear();
				decodeResultDecimalDirect.clear();
			} else {
				tmpList.addAll(decodeResultDecimalOpposite);
				decodeResultBinaryOpposite.clear();
				decodeResultDecimalOpposite.clear();
			}
			AudioDecodeResult tmpAudioDecodeResult = new AudioDecodeResult(
					type, tmpList);
			return tmpAudioDecodeResult;
		}
	}

	private List<AudioDecodeResult> decodeFromFiles(boolean directresult) {
		// TODO
		getDecodeArrayBuf();
		// ------------------------
		int totallength = original.size();
		int bufsize = 4096;
		short[] tmpBuf = new short[bufsize]; // 待处理的数据
		// short[] newBuf = new short[totallength];
		int count = 0;
		int bufcount = 0;
		// int start = 0;
		isRecording = true;
		List<AudioDecodeResult> tmpListAudioDecodeResult = null;
		// -----------------------------------
		while ((count < totallength) && isRecording) {
			bufcount++;
			int currentcount = 0;
			int tmpcount = bufsize * (bufcount - 1);
			// ---------先取buffer大小数据----------//
			for (currentcount = 0; currentcount < bufsize; currentcount++) {
				tmpcount = bufsize * (bufcount - 1) + currentcount;
				if (tmpcount < totallength) {
					tmpBuf[currentcount] = Short
							.valueOf(original.get(tmpcount));
				} else {
					break;
				}
			}
			// ----------------恢复波形---------------
			// short[] lastBufResult = mAudioWaveRestorer.doWaveRestore(tmpBuf,
			// currentcount, bufsize, bufcount);
			// if (null != lastBufResult) {
			// System.arraycopy(lastBufResult, 0, newBuf, start,
			// lastBufResult.length);
			// start += lastBufResult.length;
			// }
			// --------------恢复波形并解码----------------
			tmpListAudioDecodeResult = mDecoder.decode(tmpBuf, currentcount,
					bufsize, bufcount);
			count = bufsize * (bufcount - 1) + currentcount;
			// ------------------------------------
			if (directresult && (tmpListAudioDecodeResult != null)) {
				int size = tmpListAudioDecodeResult.size();
				for (int i = 0; i < size; i++) {
					AudioDecodeResult tmpADR = tmpListAudioDecodeResult.get(i);
					System.out.println("type:" + tmpADR.type + ";size:"
							+ tmpADR.decimalResultList.size() + "==>"
							+ toHexStringWithSpace(tmpADR.decimalResultList));
				}

			}
			// --------------------------------
			if ((currentcount == bufsize) && (count == totallength)) {
				bufcount++;
				currentcount = bufsize;
				tmpBuf = new short[bufsize]; // 待处理的数据
				tmpListAudioDecodeResult = mDecoder.decode(tmpBuf,
						currentcount, bufsize, bufcount);
				// ---------------------------
				if (directresult && (tmpListAudioDecodeResult != null)) {
					int size = tmpListAudioDecodeResult.size();
					for (int i = 0; i < size; i++) {
						AudioDecodeResult tmpADR = tmpListAudioDecodeResult
								.get(i);
						System.out
								.println("type:"
										+ tmpADR.type
										+ ";size:"
										+ tmpADR.decimalResultList.size()
										+ "==>"
										+ toHexStringWithSpace(tmpADR.decimalResultList));
					}
				}

			}
			// --------------------------------
		}
		if (!directresult) {
			System.out
					.println(Arrays.toString(mDecoder.backAllRestoreSamples()));
		}
		return tmpListAudioDecodeResult;
	}

	private synchronized String toHexStringWithSpace(List<Short> list) {
		String str = "[";
		if (list != null) {
			for (int k = 0; k < list.size(); k++) {
				str += Integer.toHexString(
						(list.get(k) & 0x000000FF) | 0xFFFFFF00).substring(6)
						+ " ";
			}
		}
		str += "]";
		return str;
	}

	@SuppressWarnings("unused")
	private void writeFile(String str) {
		String s = str;
		if (ds != null) {
			try {
				ds.write(s.getBytes());
				ds.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void doDecode(File file) {
		// TODO Auto-generated method stub
		// decodeFromFiles();
		doDecoding();
	}

	@Override
	public List<AudioDecodeResult> doDecode(short[] buff, int buflength,
			int buffersize, int recordcount) {
		// TODO Auto-generated method stub
		if ((mDecoder != null) && isRecording) {
			List<AudioDecodeResult> tmpListADR = mDecoder.decode(buff,
					buflength, buffersize, recordcount);
			return tmpListADR;
		} else {
			return null;
		}
	}

	@Override
	public void doDecodeWithoutDirectResult(short[] buff, int buflength,
			int buffersize, int recordcount) {
		// TODO Auto-generated method stub
		if (mDecoder != null) {
			List<AudioDecodeResult> tmpListADR = mDecoder.decode(buff,
					buflength, buffersize, recordcount);
			if (tmpListADR != null) {
				int size = tmpListADR.size();
				for (int i = 0; i < size; i++) {
					AudioDecodeResult tmpADR = tmpListADR.get(i);
					System.out.println("type:" + tmpADR.type + ";size:"
							+ tmpADR.decimalResultList.size() + "==>"
							+ tmpADR.decimalResultList.toString());
				}
			}
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		mAudioWaveRestorer = new AudioWaveRestorer();
		mDecoder = new Decoder();
		isRecording = true;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		isRecording = false;
		mAudioWaveRestorer = null;
		mDecoder = null;
	}

	@Override
	public List<AudioDecodeResult> doDecode(File file, boolean b) {
		return decodeFromFiles(b);
	}
}
