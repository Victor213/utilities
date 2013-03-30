package com.cloudfordev.util;

/**
 * A POJO data structure to hold the result of an SSHCommand execution.
 * 
 * @author u1001
 * @version 1.0
 */
public class SSHCommandResult {
	
	private int errorCode;
	private String output;
	
	/**
	 * Get the error code returned by the SSHCommand.
	 * 
	 * @return int The error code
	 */
	public int getErrorCode() {
		return errorCode;
	}
	
	/**
	 * Set the error code returned by the SSHCommand.
	 * 
	 * @param errorCode The int to set as the SSHCommandResult code
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	/**
	 * Get the output of the SSHCommand.
	 * 
	 * @return String The SSHCommand output
	 */
	public String getOutput() {
		return output;
	}
	
	/**
	 * Set the output returned by the SSHCommand.
	 * 
	 * @param output The String to set as the SSHCommand output
	 */
	public void setOutput(String output) {
		this.output = output;
	}
}
