package com.house.control.datastore;


import java.security.InvalidKeyException;
import java.security.Key;   
import java.security.NoSuchAlgorithmException;
//import java.security.SecureRandom;   

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;   
import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.KeyGenerator;   
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;   
import javax.crypto.SecretKeyFactory;   
import javax.crypto.spec.DESKeySpec;   

    public abstract class DEScode {   
    	
    private static final String ALGORITHM = "DES";   
    private static final byte[] key = hexStringToBytes("1F6DAEC7A1D99DB6"); 
  
    private static Key toKey(byte[] key) throws Exception {   
        DESKeySpec dks = new DESKeySpec(key);   
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);   
        SecretKey secretKey = keyFactory.generateSecret(dks);   
  
        // 当使用其他对称加密算法时，如AES、Blowfish等算法时，用下述代码替换上述三行代码   
        // SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);    
  
        return secretKey;   
    }   
  
    public static String decrypt(String ciphertext) {   
    	//传入要解密的字符串（密文），返回解密后的字符串（明文）
    	//注：传入的字符串必须是曾经被DEScode.encrypt()加密过的
    	byte[] input = hexStringToBytes(ciphertext);
        Key k = null;
        Cipher decipher = null;
        byte[] cleartext = null;
        
        try {
            k = toKey( key );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			decipher = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}   
		
        try {
			decipher.init(Cipher.DECRYPT_MODE, k);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}   
  
        try {
        	cleartext =  decipher.doFinal(input);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}   
		
		return ( new String(cleartext) );
    }   
  
    public static String encrypt(String cleartext) {   
    	//传入要加密的字符串（明文），返回加密后的字符串（密文）
    	byte[] input = cleartext.getBytes();
        Key k = null;
        Cipher cipher = null;
        byte[] ciphertext = null;

        try {
            k = toKey( key );
		} catch (Exception e) {
			e.printStackTrace();
		}
   
		try {
			cipher = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}  

        try {
			cipher.init(Cipher.ENCRYPT_MODE, k);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}   
  
        try {
        	ciphertext =  cipher.doFinal(input);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}   
		
        return bytesToHexString(ciphertext);   
    }   
    
//    static byte[] initKey() throws Exception {   
//
//        SecureRandom secureRandom = null;   
//        if (seed != null) {   
//            secureRandom = new SecureRandom(seed.getBytes());   
//        } else {   
//    	      secureRandom = new SecureRandom();   
//        }   
//  
//        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);   
//        kg.init(secureRandom);   
//  
//        SecretKey secretKey = kg.generateKey();   
//        return secretKey.getEncoded();   
//    }   
    
	 private static String bytesToHexString(byte[] src){   
	    StringBuilder stringBuilder = new StringBuilder("");   
	    if (src == null || src.length <= 0) {   
	        return null;   
	    }   
	    for (int i = 0; i < src.length; i++) {   
	        int v = src[i] & 0xFF;   
	        String hv = Integer.toHexString(v);   
	        if (hv.length() < 2) {   
	            stringBuilder.append(0);   
	        }   
	        stringBuilder.append(hv);   
	    }   
	    return stringBuilder.toString().toUpperCase();   
	}   
	
	private static byte charToByte(char c) {   
		    return (byte) "0123456789ABCDEF".indexOf(c);   
		}
	 
	private static byte[] hexStringToBytes(String hexString) {   
	    if (hexString == null || hexString.equals("")) {   
	        return null;   
	    }   
	    hexString = hexString.toUpperCase();   
	    int length = hexString.length() / 2;   
	    char[] hexChars = hexString.toCharArray();   
	    byte[] d = new byte[length];   
	    for (int i = 0; i < length; i++) {   
	        int pos = i * 2;   
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));   
	    }   
	    return d;   
	}   

}