package com.medzone.framework.util;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.util.Base64;

public class DES2Utils {
	private static final String ALGORITHM_DES_KEY_FACTORY = "DES";
	private static final String ALGORITHM_DES_CIPHER = "DES/CBC/PKCS5Padding";
	private static final byte[] IV = { 1, 2, 3, 4, 5, 6, 7, 8 };

	/**
	 * DES算法，加密
	 * 
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws CryptException
	 *             异常
	 */
	public static String encode(String key, String data) throws Exception {
		return encode(key, data.getBytes());
	}

	/**
	 * DES算法，加密
	 * 
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws CryptException
	 *             异常
	 */
	public static String encode(String key, byte[] data) throws Exception {
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance(ALGORITHM_DES_KEY_FACTORY);
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES_CIPHER);
			IvParameterSpec iv = new IvParameterSpec(IV);
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);

			byte[] bytes = cipher.doFinal(data);

			return Base64.encodeToString(bytes, 3);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * DES算法，解密
	 * 
	 * @param data
	 *            待解密字符串
	 * @param key
	 *            解密私钥，长度不能够小于8位
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             异常
	 */
	public static byte[] decode(String key, byte[] data) throws Exception {
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance(ALGORITHM_DES_KEY_FACTORY);
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES_CIPHER);
			IvParameterSpec iv = new IvParameterSpec(IV);
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * 获取解码后的值
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String decodeValue(String key, String data) throws Exception {
		return new String(decode(key, Base64.decode(data, 3)));
	}

	/**
	 * 调整密钥，如果输入的密钥不符合加密需求，则将其调整，请在解码与加密时，同时使用
	 * 
	 * @return
	 */
	public static String keyCensorAndAdjust(String key) {
		String string = String.copyValueOf(key.toCharArray());
		int keyLength = key == null ? 0 : key.length();
		while (keyLength < 8) {
			string += '*';
			keyLength++;
		}
		return string;
	}
}
