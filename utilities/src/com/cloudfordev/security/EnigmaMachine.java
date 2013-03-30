package com.cloudfordev.security;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * The EnigmaMachine class is intended to simplify strong encryption and decryption
 * so that developers and administrators are more likely to encrypt sensitive data.  
 * 
 * @author u1001 - Lynn Owens
 * @version 1.2
 */
public class EnigmaMachine {
	
	// This is the password the EnigmaMachine will use to access the keystore
	final static String keystorePassword = "Pu$3lkj%#kb3im52420@$!~";
	// Although the keystore is not static
	private File keyStoreFile = null;
	
	/**
	 * Create a new EnigmaMachine by specifying the location of the keystore
	 * 
	 * @param keyStoreFile The keystore file location that contains the AES SecretKey 
	 */
	public EnigmaMachine(File keyStoreFile) {
		this.keyStoreFile = keyStoreFile;
	}

	/**
	 * Encrypt clear text into cipher text
	 * 
	 * @param clearText The clear text to encrypt
	 * @return AESObject An AESObject that contains the cipher text and initialization vector
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws UnrecoverableEntryException
	 * @throws IOException
	 * @throws InvalidKeyException
	 */
	public AESObject encrypt(String clearText) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, KeyStoreException, CertificateException, UnrecoverableEntryException, IOException, InvalidKeyException {
		// Ensure the keystore is set
		if (keyStoreFile == null) {
			throw new KeyStoreException("KeyStore not yet defined");
		}
		
		// Get the key from the keystore
		SecretKey secretKey = loadExistingKeyFromStore();
		
		// Initialize the cipher as AES with Cipher-block chaining (CBC) and PKCS5 padding
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		
		// Get the initialization vector from the cipher
		byte[] iv = cipher.getIV(); 
		
		/* 
		 * Encrypt UTF-8 clear text into the cipher text.
		 * The encryption returns a byte[], which is not portable across CPU platforms
		 */
		byte[] cipherText = cipher.doFinal(clearText.getBytes("UTF-8"));
		
		// Encode the byte arrays into base64 for portability
		String ivString = Base64.encodeBase64String(iv);
		String cipherTextString = Base64.encodeBase64String(cipherText);
		
		// Store and return the cipherText and the IV to the user
		AESObject ao = new AESObject(ivString, cipherTextString);
		return ao;
	}
	
	/**
	 * Decrypt Base64 encoded AES/CBC/PKCS5Padding cipher text created with this EnigmaMachine's 
	 * secret key into clear text.  The initialization vector is required for decryption.
	 * 
	 * @param cipherTextString The encrypted, base64-encoded cipher text
	 * @param ivString The initialization vector of the cipher used during encryption
	 * @return String The clear text
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws UnrecoverableEntryException
	 * @throws IOException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public String decrypt(String cipherTextString, String ivString) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		// Ensure the keystore is set
		if (keyStoreFile == null) {
			throw new KeyStoreException("KeyStore not yet defined");
		}
		
		// Get the SecretKey from the keystore
		SecretKey secretKey = loadExistingKeyFromStore();
		
		// Decode the base64 string to a byte array
		byte[] iv = Base64.decodeBase64(ivString);
		byte[] cipherText = Base64.decodeBase64(cipherTextString);
		
		// Initialize the cipher with the provided initialization vector
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv) );
		
		// Decrypt the cipher text into a UTF-8 String
		String clearText = new String(cipher.doFinal(cipherText), "UTF-8");
		return clearText;
	}
	
	/**
	 * Blank the keystore and populate it with a new SecretKey  
	 * 
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public void initKeystore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		// Ensure the keystore is set
		if (keyStoreFile == null) {
			throw new KeyStoreException("KeyStore not yet defined");
		}
		
		// Create a new SecretKey and write it to the keystore
		createStoreAndWriteKey(generateKey());
	}
	    
    /**
     * Create a new keystore in memory and write it to the keystore file location provided at instantiation time.
     * 
     * @param secretKey The SecretKey to write into the new keystore
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    private void createStoreAndWriteKey(SecretKey secretKey) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
    	// Our alias is always enigma, and we need to use a jceks store because we're storing a secret key
    	String alias = "enigma";
    	
    	// Create a new JCEKS keystore in memory.  JCEKS is required to store a secret key.
    	KeyStore ks = KeyStore.getInstance("jceks"); 
    	
    	// We stomp on any existing keystore
    	ks.load(null,null);
    	
    	// Store our key and the keystore password in the keystore
     	KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(secretKey);
     	KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keystorePassword.toCharArray());
    	ks.setEntry(alias, skEntry, protParam);
    	
    	// Write the keystore to disk
    	java.io.FileOutputStream fos = null;
    	try {
    	    fos = new java.io.FileOutputStream(keyStoreFile);
    	    ks.store(fos, keystorePassword.toCharArray());
    	} finally {
    	    if (fos != null) { 
    	        fos.close();
    	    }
    	}
    }    
    
    /**
     * Load a SecretKey from the keystore location provided at instantion time.
     * 
     * @return SecretKey The SecretKey found in the keystore under the alias 'enigma'
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     * @throws UnrecoverableEntryException
     */
    private SecretKey loadExistingKeyFromStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableEntryException {
    	// Our alias is always enigma, and we need to use a jceks store because we're storing a secret key
    	String alias = "enigma";
    	KeyStore ks = KeyStore.getInstance("jceks");
    	
    	// Use this password parameter to access the keystore
    	KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keystorePassword.toCharArray());
    	
    	// Read the keystore from disk    	
    	java.io.FileInputStream fis = null;
    	try {
    	    fis = new java.io.FileInputStream(keyStoreFile);
    	    ks.load(fis, keystorePassword.toCharArray());
    	} finally {
    	    if (fis != null) { 
    	        fis.close();
    	    }
    	}
    	
    	// Fetch out the SecretKey from the keystore
    	KeyStore.Entry kEntry = ks.getEntry(alias,protParam);
    	KeyStore.SecretKeyEntry skEntry = (KeyStore.SecretKeyEntry)kEntry; 
    	return skEntry.getSecretKey();
    }
    
    /**
     * Generate a new AES 256-bit SecretKey.
     * 
     * @return SecretKey The new SecretKey
     * @throws NoSuchAlgorithmException
     */
    private SecretKey generateKey() throws NoSuchAlgorithmException {
    	KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    	keyGen.init(256);
    	return keyGen.generateKey();
    }

}