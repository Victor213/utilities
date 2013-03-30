package com.cloudfordev.security;

/**
 * AESObject stores an AES initialization vector and cipher.
 * 
 * @author u1001
 * @version 1.0
 */
public class AESObject {
	
	private String iv;
	private String cipher;
	
	/**
	 * Create an AESObject using the provided initialization vector and cipher.
	 * 
	 * @param iv The initialization vector
	 * @param cipher The cipher
	 */
	public AESObject(String iv, String cipher) {
		this.iv = iv;
		this.cipher = cipher;
	}
	
	/**
	 * Get the initialization vector.
	 * 
	 * @return String The initialization vector
	 */
	public String getIv() {
		return iv;
	}
	
	/**
	 * Set the initialization vector.
	 * 
	 * @param iv The initialization vector to set
	 */
	public void setIv(String iv) {
		this.iv = iv;
	}
	
	/**
	 * Get the cipher.
	 * 
	 * @return String The cipher
	 */
	public String getCipher() {
		return cipher;
	}
	
	/**
	 * Set the cipher.
	 * 
	 * @param cipher The cipher to set.  
	 */
	public void setCipher(String cipher) {
		this.cipher = cipher;
	}
	
}
