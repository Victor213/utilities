package com.cloudfordev.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * SSH as a user to a host and run a command.
 * 
 * @author u1001
 * @version 1.0
 */
public class SSHCommand {
	private String user;
	private String hostname;
	private String command;
	
	/**
	 * Create a new SSHCommand, specifying the username, hostname, and command.
	 * 
	 * @param user The user to SSH as
	 * @param hostname The hostname to SSH to
	 * @param command The command to run on the remote host
	 */
	public SSHCommand(String user, String hostname, String command) {
		this.user = user;
		this.hostname = hostname;
		this.command = command;
	}
	
	/**
	 * Execute the prepared SSHCommand.
	 * 
	 * @return SSHCommandResult The Result of the SSHCommand execution
	 * @throws IOException
	 */
	public SSHCommandResult execute() throws IOException {
		SSHCommandResult scr = new SSHCommandResult();
		
		// The SSH command is described here, using batchmode to avoid certain tty issues
		// Also the username and hostname is specified
		String[] sshPrefixArray = { "ssh", "-o", "BatchMode=yes", user + "@" + hostname };
		
		// Split the command String into a String[]
		String[] commandArray = command.split(" ");
			
		// Append the command array on to the ssh prefix
		String[] sshCommandArray = concat(sshPrefixArray, commandArray);
		
		// Execute the full ssh command 
		Process p = Runtime.getRuntime().exec(sshCommandArray);
		
		// Get the stdout and stderr
		InputStream stdoutIS = p.getInputStream();
		InputStream stderrIS = p.getErrorStream();
		
		// Convert the stdout and stderr to String objects
		String stdout = convertStreamToString(stdoutIS);
		String stderr = convertStreamToString(stderrIS);
		
		// Smoosh the stdout and stderr together into "output"
		String output = stdout + "\n" + stderr;
		
		try {
			// Wait for the command to finish executing
			p.waitFor();
		} catch (InterruptedException e) {
			// The command was interrupted
			// Set the SSHCommandResult error code to 1 and note in the output that it was interrupted
			scr.setErrorCode(1);
			scr.setOutput("InterruptedException");
			return scr;
		}
		
		// Set the SSHCommandResult error code to the exit value of the Process
		scr.setErrorCode(p.exitValue());
		// Set the SSHCommandResult outputg to the output obtained from the Process
		scr.setOutput(output);
		
		return scr;
	}
	
	/**
	 * A helper method to convert Streams to Strings
	 * 
	 * @param is The InputStream to convert to a String
	 * @return String the String value
	 */
	private String convertStreamToString(InputStream is) {
	    @SuppressWarnings("resource")
		Scanner s = new Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	/**
	 * A helper method to concatenate two String[]
	 * 
	 * @param A String[] A
	 * @param B String[] B
	 * @return String[] The concatenation of String[] A and String[] B
	 */
	private String[] concat(String[] A, String[] B) {
	   int aLen = A.length;
	   int bLen = B.length;
	   String[] C= new String[aLen+bLen];
	   System.arraycopy(A, 0, C, 0, aLen);
	   System.arraycopy(B, 0, C, aLen, bLen);
	   return C;
	}

	/**
	 * Get the user this SSHCommand will use to authenticate into the remote host.
	 * 
	 * @return String The username
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the username to use for authentication into the remote host.
	 * 
	 * @param user The username to authenticate into the remote host.  
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Get the command that is intended for execution on the remote host.
	 * 
	 * @return String The remote command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Set the command to be executed on the remote host.
	 * 
	 * @param command The remote command
	 */
	public void setCommand(String command) {
		this.command = command;
	}
}
