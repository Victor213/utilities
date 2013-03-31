package com.cloudfordev.util;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.cloudfordev.security.EnigmaMachine;

/**
 * Authenticator is a convenience class for authenticating a user.  It provides 
 * AES 256-bit cipher support and strict user input screening.  
 * 
 * @author u1001
 * @version 1.3
 */
public class Authenticator {
	
	/**
	 * Attempt to authenticate a user.
	 * 
	 * @param app The application the user is attempting to access.  It is assumed that a JDBC JNDI name exists which is the same name as this parameter.
	 * @param username The user's name
	 * @param password The user's supplied password
	 * @return boolean True if the user is authenticated
	 */
	public boolean authenticate(String app, String username, String password) {
		return authenticate(app, username, password, false);
	}
	
	public boolean authenticate(String app, String username, String password, boolean needEmailValidation) {
		// Assume the user is not authenticated
		boolean isAuthenticated = false;
		
		// Create an InputScreener to accept only good inputs and refuse bad ones
		InputScreener screener = new InputScreener();
		
		try {
			// Screen that the user name provided is safe
			screener.screenUserName(username);
			// Screen that the user password provided is safe
			screener.screenPassword(password);
		} catch (InputScreenerException ise) {
			// Any funny stuff and the user automatically fails authentication
			isAuthenticated = false;
			return isAuthenticated;
		}
		
		// Prep for the JDBC connection
		InitialContext ctx = null;
		Connection conn = null;
		DataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String encryptedPwd = "";
		String iv = "";
		String sql = "";
		
		if (needEmailValidation) {
			sql = "SELECT pwd,iv FROM users WHERE uid = ? AND is_validated = TRUE AND is_deleted = FALSE";
		} else {
			sql = "SELECT pwd,iv FROM users WHERE uid = ? AND is_deleted = FALSE";
		}
		
		try {
			// Lookup the context of the JNDI provided at this object's instantiation
			ctx = new InitialContext();
			// Obtain the DataSource object 
			ds = (DataSource)ctx.lookup("java:/" + app);
			// Open a connection to the DataSource
			conn = ds.getConnection();
			// Prepare a statement to fetch the password and initialization vector of the stored user from the application database
			ps = conn.prepareStatement(sql);
			// Set the username in the PreparedStatement
			ps.setString(1, username);
			ps.setMaxRows(1); 
			// Execute the query
			rs = ps.executeQuery();
			// Get the encrypted password and initialization vector
			while (rs.next()) {
				encryptedPwd = rs.getString("pwd");
				iv = rs.getString("iv");			
			}
		} catch (Exception e) {
			Notification n = new Notification(this,e);
			n.log();
			isAuthenticated = false;
			return isAuthenticated;
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				Notification n = new Notification(this,e);
				n.log();
			}
			try {
				ps.close();
			} catch (SQLException e) {
				Notification n = new Notification(this,e);
				n.log();
			}
			try {
				conn.close();
			} catch (SQLException e) {
				Notification n = new Notification(this,e);
				n.log();
			}
			try {
				ctx.close();
			} catch (NamingException e) {
				Notification n = new Notification(this,e);
				n.log();
			}
		}
		
		/*
		 * Decrypt pwd and compare
		 * DO NOT KEEP A POINTER TO THE DECRYPTED PWD 
		 * WE DO NOT WANT A BINARY HEAP DUMP TO BE ABLE TO RETRIEVE CLEAR TEXT PWDS
		 */
		String keyLocation = "";
		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			keyLocation = "C:\\Users\\jboss\\keys\\enigma";
		} else {
			keyLocation = "/home/jboss/keys/enigma";
		}
		File keyStoreFile = new File(keyLocation);
		EnigmaMachine em = new EnigmaMachine(keyStoreFile);
		try {
			// If the stored password equals the user supplied password, the user is authenticated
			if (em.decrypt(encryptedPwd,iv).equals(password)) {
				isAuthenticated = true;
			}
		} catch(Exception e) {
			// Any funny stuff and they're not authenticated
			isAuthenticated = false;
			return isAuthenticated;
		}
		
		return isAuthenticated;
	}
}
