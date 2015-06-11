package com.cellulant.profiler.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;



public class CipherUtils {
private Props props;
	
	public CipherUtils(Props props){
		this.props=props;
	}


	/**
	 * 
	 * @param plainText
	 * @return
	 * @throws Exception
	 */
	public  String encrypt(String plainText) throws Exception {
		String secret = props.getKey();
		SecretKey key = new SecretKeySpec(secret.getBytes(), "AES");
		Base64 coder = new Base64();
		String iv = "0a1b2c3d4e5f6g7h";
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
		byte[] cipherText = cipher.doFinal(plainText.getBytes());
		return bytesToHex(new String(coder.encode(cipherText)).getBytes());
	}

	/**
	 * 
	 * @param codedText
	 * @return
	 * @throws Exception
	 */
	public  String decrypt(String codedText) throws Exception {
		String secret = props.getKey();
		SecretKey key = new SecretKeySpec(secret.getBytes(), "AES");
		Base64 coder = new Base64();
		String iv = "0a1b2c3d4e5f6g7h";
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		byte[] encypted = coder.decode(hexToBytes(codedText));
		cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
		byte[] decrypted = cipher.doFinal(encypted);
		return new String(decrypted);
	}

	/**
	 * Converts a byte array to a hex string.
	 * 
	 * @param data
	 *            the byte array
	 * 
	 * @return the hex string
	 */
	public static String bytesToHex(final byte[] data) {
		if (data == null || data.length == 0) {
			throw new IllegalArgumentException("Empty byte array");
		}

		int len = data.length;

		StringBuilder sb = new StringBuilder(2);
		for (int i = 0; i < len; i++) {
			if ((data[i] & 0xFF) < 16) {
				sb.append("0").append(Integer.toHexString(data[i] & 0xFF));
			} else {
				sb.append(Integer.toHexString(data[i] & 0xFF));
			}
		}

		return sb.toString();
	}

	/**
	 * Converts a hex string to a byte array.
	 * 
	 * @param str
	 *            the hex string
	 * 
	 * @return the byte array
	 */
	public static byte[] hexToBytes(final String str) {
		if (str == null) {
			throw new IllegalArgumentException("Empty string");
		} else if (str.length() < 2) {
			throw new IllegalArgumentException("Invalid hex string");
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];

			for (int i = 0; i < len; i++) {
				buffer[i] = (byte) Integer.parseInt(
						str.substring(i * 2, i * 2 + 2), 16);
			}

			return buffer;
		}
	}
}
