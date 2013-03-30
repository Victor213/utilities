package com.cloudfordev.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * InputScreener performs various screenings, each one accepting only a certain regex 
 * pattern of input and refusing numerous dangerous characters.
 * 
 * @author u1001
 * @version 1.3
 * 
 * TODO Improve so that it doesn't rely on throwing costly exceptions
 */
public class InputScreener {
	
	/*
	 * The order of tests for any public screen method are as follows:
	 * 
	 * 1.  Specific white list tests
	 * 2.  Generic white list tests
	 * 3.  Specific black list tests
	 * 4.  Generic black list tests
	 */

	/**
	 * Screen a CFD hostname.
	 * 
	 * @param input The String to be screened as a CFD hostname
	 * @throws InputScreenerException
	 */
	public void screenHostname(String input) throws InputScreenerException {
		// Characters unacceptable for a hostname
		char[] badCharacters = { '`','!','@','#','$','%','^','&','*','(',')','~','{','[','}',']','|','\\','"','\'','<','>','?','/' };
		
		// Screen that this is a hostname
		whitelistHostname(input);
		// Screen that this is a single word
		whitelistSingleWord(input);
		// Screen that this String does not contain any dangerous characters
		blacklistBadChars(input, badCharacters);
		// Screen that the length of this String is reasonable
		blacklistLength(input);
	}
	
	public void screenInteger(String input) throws InputScreenerException {
		try {  
		    Integer.parseInt(input);  
		} catch(NumberFormatException nfe) {  
		    throw new InputScreenerException("Not an integer");
		}  
	}
	
	/**
	 * Screen that the input is a word
	 * 
	 * @param input The String to be screened as a word
	 * @throws InputScreenerException
	 */
	public void screenWord(String input) throws InputScreenerException {
		// Characters unacceptable for a username
		char[] badCharacters = { '`','!','#','$','%','^','&','*','(',')','~','{','[','}',']','|','\\','"','\'','<','>','?','/','-' };
		
		// Screen that this is a single word
		whitelistSingleWord(input);
		// Screen that this String does not contain any dangerous characters
		blacklistBadChars(input, badCharacters);
		// Screen that the length of this String is reasonable
		blacklistLength(input);
	}
	
	/**
	 * Screen a username, which must be in the format of an email address
	 * 
	 * @param input The String to be screened as a username
	 * @throws InputScreenerException
	 */
	public void screenUserName(String input) throws InputScreenerException {
		// Characters unacceptable for a username
		char[] badCharacters = { '`','!','#','$','%','^','&','*','(',')','~','{','[','}',']','|','\\','"','\'','<','>','?','/','-' };
		
		// Screen that this is an email address
		whitelistEmailAddress(input);
		// Screen that this is a single word
		whitelistSingleWord(input);
		// Screen that this String does not contain any dangerous characters
		blacklistBadChars(input, badCharacters);
		// Screen that the length of this String is reasonable
		blacklistLength(input);
	}

	/**
	 * Screen a password.
	 * 
	 * @param input The String to be screened as a password
	 * @throws InputScreenerException
	 */
	public void screenPassword(String input) throws InputScreenerException {
		// Characters unacceptable for a hostname
		char[] badCharacters = { '\'', ';', '-', '"', ')', '(', '&', '*', '=', '|', '$' };
		
		// Screen that it's over a minimum length
		whitelistLength(input);
		// Screen that this is a single word
		whitelistSingleWord(input);
		// Screen that it's strong enough
		whitelistStrength(input);
		// Screen that this String does not contain any dangerous characters
		blacklistBadChars(input, badCharacters);
		// Screen that it's under a maximum length
		blacklistLength(input);
	}
	
	/**
	 * Screen an email address
	 * 
	 * @param input
	 * @throws InputScreenerException
	 */
	public void screenEmail(String input) throws InputScreenerException {
		// Characters unacceptable for a hostname
		char[] badCharacters = { '`','!','#','$','%','^','&','*','(',')','~','{','[','}',']','|','\\','"','\'','<','>','?','/','-' };
		
		// Screen that this is a username
		whitelistEmailAddress(input);
		// Screen that this is a single word
		whitelistSingleWord(input);
		// Screen that this String does not contain any dangerous characters
		blacklistBadChars(input, badCharacters);
		// Screen that the length of this String is reasonable
		blacklistLength(input);		
	}
	
	private void whitelistStrength(String input) throws InputScreenerException {
		String UPPER = ".*[A-Z].*";
		String LOWER = ".*[a-z].*";
		String NUMBER = ".*[0-9].*";
		
		Pattern upperPat = Pattern.compile(UPPER);
		Pattern lowerPat = Pattern.compile(LOWER);
		Pattern numberPat = Pattern.compile(NUMBER);
		
		Matcher upperMat = upperPat.matcher(input);
		Matcher lowerMat = lowerPat.matcher(input);
		Matcher numberMat = numberPat.matcher(input);
		
		if (!upperMat.matches() || !lowerMat.matches() || !numberMat.matches()) {
			throw new InputScreenerException("Input does not match minimum strength patterns");
		}
	}

	/**
	 * Ensure the provided String is an email address
	 * 
	 * @param input The email address String
	 * @throws InputScreenerException
	 */
	private void whitelistEmailAddress(String input) throws InputScreenerException {
		// Ensure the input matches a regex description of an email address pattern
		String EMAIL_PATTERN = 
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(input);
		
		// If it doesn't match the email address regular expression, throw an exception
		if (!matcher.matches()) {
			throw new InputScreenerException("Input does not match the standard email pattern");
		}
	}

	/**
	 * Ensure that a String is not an unreasonable length.
	 * 
	 * @param input The String to ensure is a reasonable length
	 * @throws InputScreenerException
	 */
	private void blacklistLength(String input) throws InputScreenerException {
		// A reasonable length is defined as under 40 characters
		int maxLength = 40;
		
		// If the input is longer than the reasonable length, throw an exception
		if (input.length() > maxLength) {
			throw new InputScreenerException("Input length is outside boundaries");
		}
	}
	
	/**
	 * Ensure that a String is a reasonable length.
	 * 
	 * @param input The String to ensure is a reasonable length
	 * @throws InputScreenerException
	 */
	private void whitelistLength(String input) throws InputScreenerException {
		// A reasonable length is defined as under 40 characters
		int minLength = 8;
		
		// If the input is longer than the reasonable length, throw an exception
		if (input.length() < minLength) {
			throw new InputScreenerException("Input length is outside boundaries");
		}		
	}
	
	/**
	 * Ensure the input String does not contain any dangerous characters.
	 * 
	 * @param input The String to test for dangerous characters
	 * @param badCharacters A char[] of known dangerous characters
	 * @throws InputScreenerException
	 */
	private void blacklistBadChars(String input, char[] badCharacters) throws InputScreenerException {
		// Test for bad characters
		for (char c : badCharacters) {
			if (input.indexOf(new Character(c).toString()) != -1) {
				// A dangerous character has been found.  Throw an exception.
				throw new InputScreenerException("Input contains an invalid character");
			}
		}
	}
	
	/**
	 * Ensure the String is a single word.
	 * 
	 * @param input The String to ensure is a single word
	 * @throws InputScreenerException
	 */
	private void whitelistSingleWord(String input) throws InputScreenerException {
		// Test that the input does not contain a space
		char[] badCharacters = { ' ' };
		for (char c : badCharacters) {
			if (input.indexOf(new Character(c)) != -1) {
				// The String contains a space; it is not a single word.  Throw an exception.
				throw new InputScreenerException("Input contains an invalid character");
			}
		}
	}
	
	/**
	 * Ensure that the String provided is a CFD hostname
	 * 
	 * @param input The String to ensure is a CFD hostname
	 * @throws InputScreenerException
	 */
	private void whitelistHostname(String input) throws InputScreenerException {
		// Our hostnames follow a pattern like club-admin-01
		Pattern pattern = Pattern.compile("[A-Za-z]+-[A-Za-z]+-[0-9]+");
		Matcher matcher = pattern.matcher(input);
		
		if (!matcher.matches()) {
			// This string does not match the regex pattern that defines our hostname convention.
			throw new InputScreenerException("Input does not match the standard hostname pattern");
		}
	}
}
