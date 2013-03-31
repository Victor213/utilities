package com.cloudfordev.itil;

import com.cloudfordev.util.ConfigClass;

/**
 * Configuration of the com.cloudfordev.itil package 
 * 
 * @author u1001
 * @version 1.0
 */
public class Config extends ConfigClass {
	/*
	 * ITIL core configuration
	 */
	// Misc
	//public static String enigmaKeystoreFilePath = "/home/jboss/keys/enigma";
	private static String defaultEnigmaKey = "/home/jboss/keys/enigma";
	
	// Configuration Management System (CMS) Configuration
	public static String cmsDriver = "org.postgresql.Driver";
	public static String cmsDBSoftware = "postgresql";
	public static String cmsDBHost = "club-intsvc-01.int.club.cloudfordev.com";
	public static int cmsDBPort = 5432;
	public static String cmsDBName = "cms";
	public static String cmsDBUser = "cms_user";
	public static String cmsDBPasswordCipher = "dNbIZjIHnxXFtCKQTm+8fQ==";
	public static String cmsDBPasswordIV = "3OAcfaPbtYQm8DHYE7sADw==";
	public static String cmsJDBCJNDI = "java:/cms";
	
	// Incident Management (ICM) Configuration
	public static String icmDriver = "org.postgresql.Driver";
	public static String icmDBSoftware = "postgresql";
	public static String icmDBHost = "club-intsvc-01.int.club.cloudfordev.com";
	public static int icmDBPort = 5432;
	public static String icmDBName = "icm";
	public static String icmDBUser = "icm_user";
	public static String icmDBPasswordCipher = "dNbIZjIHnxXFtCKQTm+8fQ==";
	public static String icmDBPasswordIV = "3OAcfaPbtYQm8DHYE7sADw==";
	public static String icmJDBCJNDI = "java:/icm";
	
	public static String getCpEnigmaKey() {
		String Os = System.getProperty("os.name").toLowerCase();
		String keyLocation = "";
		
		if (Os.startsWith("win")) {
			keyLocation = "C:\\Users\\jboss\\keys\\enigma";
		} else {
			keyLocation = defaultEnigmaKey;
		}
		
		return keyLocation;
	}

}
