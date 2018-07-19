package org.yanhuang.learning.hashing;

import net.openhft.hashing.LongHashFunction;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CityHash {

	public static void main(String[] args) {
		final long hash = LongHashFunction.city_1_1().hashChars(md5("8669570-3191****"));
		System.out.println(hash);
	}

	public static String md5(String str) {
		if (str == null) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] data = md.digest(str.getBytes("UTF-8"));
			for (int offset = 0; offset < data.length; offset++) {
				int i = data[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			// MD5肯定存在，不会出错，无视
		} catch (UnsupportedEncodingException e) {
			// UTF-8肯定存在，不会出错，无视
		}
		return buf.toString();
	}
}
