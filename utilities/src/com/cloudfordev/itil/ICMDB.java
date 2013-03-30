package com.cloudfordev.itil;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.cloudfordev.security.EnigmaMachine;
import com.cloudfordev.util.Notification;

/**
 * The Incident Management Database (ICMDB) warehouses all Incidents.  
 * 
 * @author u1001
 * @version 1.1
 */
public class ICMDB {
	
	Connection conn = null;
	boolean persistConnection = false;
	
	/**
	 * Instantiate a new instance for access to the ICMDB
	 */
	public ICMDB() {
		
	}

	/**
	 * Add an Incident to the ICMDB.  
	 * 
	 * @param i The Incident to add to the ICMDB
	 * @return The int of the newly generated Incident record in the ICMDB
	 * @throws ITILException If the Incident record cannot be created in the ICMDB
	 */
	public int addIncident(Incident i) throws ITILException {
		int incId = 0;
		int severity = i.getSeverity();
		String source = i.getSource();
		String errorMsg = i.getErrorMsg();
		String contactEmail = i.getContactEmail();
		
		/*
		 *  The generationHash is simply a unique String to keep track of the new Incident we create
		 *  so that if this object later has it's close method called, we can easily find it in the db
		 */
		SecureRandom random = new SecureRandom();
		String generationHash = new BigInteger(130, random).toString(32);
		
		// Specify the insert SQL
		String sql = "INSERT INTO incidents (ci,start_date,resolve_date,source,severity,error_no,error_msg,contact_email,generation_hash,resolution) values (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
		
		// Add the Statement args
		ArrayList<Object> insertArgs = new ArrayList<Object>();
		insertArgs.add(i.getCi().getId());
		insertArgs.add(i.getStartDate());
		insertArgs.add(i.getResolveDate());
		insertArgs.add(source);
		insertArgs.add(severity);
		insertArgs.add(i.getErrorNo());
		insertArgs.add(errorMsg);
		insertArgs.add(contactEmail);
		insertArgs.add(generationHash);
		insertArgs.add(i.getResolution());
		
		// Execute the SQL 
		executeSQL(sql, insertArgs);
		
        /*
         * Auto notifications
         */
		if (severity < 4) {
			String msg = "";
			if (i.isResolved()) {
				msg = "Doc Inc: " + i.getCi().getDescription() + ": " + errorMsg;
			} else {
				msg = "Err Inc: " + i.getCi().getDescription() + ": " + errorMsg;
			}
			
	        Notification n = new Notification(source, msg);
	
	        if (severity == 3) {
	            n.email(contactEmail);
	            n.log();
	        } else if (severity < 3) {
	            n.alert();
	        }
		}
		
		/* 
		 * Now we need to find the newly created Incident int for return to the user
		 */
				
		// Specify the select query SQL
		String querySQL = "SELECT id FROM incidents WHERE generation_hash = ?";
		
		// Specify the insert SQL
		ArrayList<Object> queryArgs = new ArrayList<Object>();
		queryArgs.add(generationHash);
		
		// Execute the SQL and store the returned rows
		ArrayList<ITILRow> allRows = executeSQL(querySQL, queryArgs);
		
		if (allRows.size() > 0) {
			// If there are results
			// We only want the first result row
			ITILRow row = allRows.get(0);
			// Cast the object in the first field of the row to an integer
			// This is our type id
			incId = (int) row.getObject(0);
			return incId;
		} else {
			throw new ITILException("Could not find the Incident we just inserted into the ICMDB");
		}	
	}
	
	/**
	 * Get all Incidents for a specified ConfigurationItem
	 * 
	 * @param userCI The ConfigurationItem for which all Incidents are sought
	 * @return All Incidents in the ICMDB for the specified ConfigurationItem
	 * @throws ITILException If there is a failure while working with the back-end ICMDB
	 */
	public ArrayList<Incident> getIncByCI(ConfigurationItem userCI) throws ITILException {
		// Specify the SQL of this method
		String sql = "SELECT * FROM incidents WHERE ci = ?";
		
		// Add the Statement args
		ArrayList<Object> args = new ArrayList<Object>();
		args.add(userCI.getId());
		
		// Execute the SQL and store the returned rows
		ArrayList<ITILRow> allRows = executeSQL(sql, args);
		
		// Prepare an ArrayList to store the Incident objects
		ArrayList<Incident> allIncs = new ArrayList<Incident>();
		
		// For each returned row, create an Incident
		for (ITILRow row : allRows) {
			Integer id = (Integer) row.getObject(0);
			Timestamp startDate = (Timestamp) row.getObject(2);
			Timestamp resolveDate = (Timestamp) row.getObject(3);
			String source = (String) row.getObject(4);
			Integer severity = (Integer) row.getObject(5);
			Integer errorNo = (Integer) row.getObject(6);
			String errorMsg = (String) row.getObject(7);
			String contactEmail = (String) row.getObject(8);
			String resolution = (String) row.getObject(10);
			
			// Create the Incident
			Incident i = new Incident(userCI, startDate, resolveDate, source, severity, errorNo, errorMsg, contactEmail, resolution);
			i.setId(id);
			
			// Store it
			allIncs.add(i);
		}
		
		// And return all the Incident objects
		return allIncs;			
	}
	
	/**
	 * Get an Incident specified by its ICMDB record number.  
	 * 
	 * @param userIncID The ICMDB record number of the Incident that is sought
	 * @return The requested Incident 
	 * @throws ITILException If there is a failure while working with the back-end ICMDB
	 */
	public Incident getIncByID(int userIncID) throws ITILException {
		CMDB cmdb = new CMDB();
		
		// Specify the SQL of this method
		String sql = "SELECT * FROM incidents WHERE id = ?";
		
		// Add the Statement args
		ArrayList<Object> args = new ArrayList<Object>();
		args.add(userIncID);
		
		// Execute the SQL and store the returned rows
		ArrayList<ITILRow> allRows = executeSQL(sql, args);

		if (allRows.size() > 0) {
			// If there are results
			// We only want the first result row
			ITILRow row = allRows.get(0);
						
			Integer id = (Integer) row.getObject(0);
			Integer ciID = (Integer) row.getObject(1);
			Timestamp startDate = (Timestamp) row.getObject(2);
			Timestamp resolveDate = (Timestamp) row.getObject(3);
			String source = (String) row.getObject(4);
			Integer severity = (Integer) row.getObject(5);
			Integer errorNo = (Integer) row.getObject(6);
			String errorMsg = (String) row.getObject(7);
			String contactEmail = (String) row.getObject(8);
			String resolution = (String) row.getObject(10);
			
			// Fetch the ConfigurationItem specified by this CMDB record ID
			ConfigurationItem ci = cmdb.getCIByID(ciID);
			
			// Create an Incident
			Incident i = new Incident(ci, startDate, resolveDate, source, severity, errorNo, errorMsg, contactEmail, resolution);
			i.setId(id);
			
			// Return that Incident
			return i;
		} else {
			// Or throw an exception if that Incident doesn't exist
			throw new ITILException("Incident does not exist in ICMDB");
		}
	}
	
	/**
	 * Retrieve all unresolved Incident objects from the ICMDB.
	 * 
	 * @return All unresolved Incident objects from the ICMDB
	 * @throws ITILException If there is a failure while working with the back-end ICMDB
	 */
	public ArrayList<Incident> getOpenIncidents() throws ITILException {
		CMDB cmdb = new CMDB();
		
		// Specify the SQL of this method
		String sql = "SELECT * FROM incidents WHERE resolve_date is null ORDER BY start_date";
		
		// Add the Statement args
		ArrayList<Object> args = new ArrayList<Object>();
		
		// Execute the SQL and store the returned rows
		ArrayList<ITILRow> allRows = executeSQL(sql, args);
		
		// Prepare an ArrayList to store the Incident objects
		ArrayList<Incident> allIncs = new ArrayList<Incident>();
		
		// For each returned row, create an Incident
		for (ITILRow row : allRows) {
			Integer id = (Integer) row.getObject(0);
			Integer ciID = (Integer) row.getObject(1);
			Timestamp startDate = (Timestamp) row.getObject(2);
			String source = (String) row.getObject(4);
			Integer severity = (Integer) row.getObject(5);
			Integer errorNo = (Integer) row.getObject(6);
			String errorMsg = (String) row.getObject(7);
			String contactEmail = (String) row.getObject(8);
			
			// Fetch the ConfigurationItem specified by this CMDB record ID
			ConfigurationItem ci = cmdb.getCIByID(ciID);
			
			// Create the Incident
			Incident i = new Incident(ci, startDate, source, severity, errorNo, errorMsg, contactEmail);
			i.setId(id);
			
			// Add it to the ArrayList
			allIncs.add(i);
		}
		
		// And return the list of Incidents
		return allIncs;	
	}
	
	/**
	 * Get all Incident records from the ICMDB within the last n hours.
	 * 
	 * @param hours Specified number of hours 
	 * @return All Incidents in the ICMDB for the specified ConfigurationItem
	 * @throws ITILException If there is a failure while working with the back-end ICMDB
	 */
	public ArrayList<Incident> getRecentIncidents(int hours) throws ITILException {
		CMDB cmdb = new CMDB();
		
		// Specify the SQL of this method
		// single quote is required by INTERVAL, but causes the PreparedStatement ? to bomb
		String sql = "SELECT * FROM incidents WHERE start_date >= date_trunc('hour', now()) - INTERVAL '" + hours + " hour' ORDER BY start_date";
		
		// Add the Statement args
		ArrayList<Object> args = new ArrayList<Object>();
		//args.add(hours);
		
		// Execute the SQL and store the returned rows
		ArrayList<ITILRow> allRows = executeSQL(sql, args);
		
		// Prepare an ArrayList to store the Incident objects
		ArrayList<Incident> allIncs = new ArrayList<Incident>();
		
		// For each returned row, create an Incident
		for (ITILRow row : allRows) {
			Integer id = (Integer) row.getObject(0);
			Integer ciID = (Integer) row.getObject(1);
			Timestamp startDate = (Timestamp) row.getObject(2);
			Timestamp resolveDate = (Timestamp) row.getObject(3);
			String source = (String) row.getObject(4);
			Integer severity = (Integer) row.getObject(5);
			Integer errorNo = (Integer) row.getObject(6);
			String errorMsg = (String) row.getObject(7);
			String contactEmail = (String) row.getObject(8);
			String resolution = (String) row.getObject(10);
			
			// Fetch the ConfigurationItem specified by this CMDB record ID
			ConfigurationItem ci = cmdb.getCIByID(ciID);
			
			// Create the Incident
			Incident i = new Incident(ci, startDate, resolveDate, source, severity, errorNo, errorMsg, contactEmail, resolution);
			i.setId(id);
			
			// Add it to the ArrayList
			allIncs.add(i);
		}
		
		// And return the list of Incidents
		return allIncs;	
	}
	
	/**
	 * Mark an ICMDB Incident record as resolved.  
	 * 
	 * @param i The Incident object that represents the existing ICMDB record, including it's resolved timestamp.
	 */
	public void resolveIncident(Incident i) {
		if (! i.isResolved()) {
			Notification n = new Notification(this,"Cannot resolve an open Incident: " + i.getCi().getDescription());
			n.log();
		}
		
		if (i.getResolveDate() == null) {
			Notification n = new Notification(this,"Resolve date is null: " + i.getCi().getDescription());
			n.log();
		}
		
		if (i.getId() == 0) {
			Notification n = new Notification(this,"Incident CI ID is 0: " + i.getCi().getDescription());
			n.log();
		}
		
		if (i.getResolution() == "") {
			Notification n = new Notification(this,"Incident does not have a define resolution: " + i.getCi().getDescription());
			n.log();
		}
		
		// Specify the SQL of this method
		String sql = "UPDATE incidents SET resolve_date = ?, resolution = ? WHERE id = ?";
		
		// Add the Statement args
		ArrayList<Object> args = new ArrayList<Object>();
		args.add(i.getResolveDate());
		args.add(i.getResolution());
		args.add(i.getId());
		
		// Execute the SQL
		try {
			executeSQL(sql, args);
		} catch (ITILException e) {
			Notification n = new Notification(this,e);
			n.log();
		}
		
		// Notify
		int severity = i.getSeverity();
		if (severity < 4) {
			String msg = "";
			msg = "Resolved: " + i.getCi().getDescription() + ": " + i.getErrorMsg();
	        Notification n = new Notification(i.getSource(), msg);
	
	        if (severity == 3) {
	            n.email(i.getContactEmail());
	            n.log();
	        } else if (severity < 3) {
	            n.alert();
	        }
		}
	}
	
	/**
	 * Takes a string SQL statement meant to prepare a statement, and the arguments for that statement.
	 * 
	 * @param sql SQL Statement String
	 * @param args Arguments to be passed into the Statement
	 * @return ResultSet The results of the SQL
	 * @throws ITILException If the SQL could not be executed
	 */
	private ArrayList<ITILRow> executeSQL(String sql, @SuppressWarnings("rawtypes") ArrayList args) throws ITILException {
		ArrayList<ITILRow> allRows = new ArrayList<ITILRow>();
		InitialContext ctx = null;
		DataSource ds = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		if (conn == null) {
			/*
			 * Get a connection
			 */
			try {
				// Lookup the context of the JNDI provided at this object's instantiation
				ctx = new InitialContext();
				// Obtain the DataSource object 
				ds = (DataSource)ctx.lookup(Config.icmJDBCJNDI);
				// Open a connection to the DataSource
				conn = ds.getConnection();
			} catch (Exception jdbcException) {
				// Hello, hum, we may not be in a J2EE container... In that case...
				try {
					// Load the driver
					try {
						Class.forName(Config.icmDriver);
					} catch (ClassNotFoundException cnfe) {
						Notification n = new Notification(this,cnfe);
						n.alert();
						// And re-throw the exception as a ITILException
						throw new ITILException("Cannot get a connection to the ICMDB", cnfe);
					}
					
					// Fire up an EnigmaMachine for database password decrypting
					EnigmaMachine em = new EnigmaMachine(new File(Config.enigmaKeystoreFilePath));
					String dbPassword = "";
					
					try {
						/*
						 *  This decrypted password is not stored as an instance variable because
						 *  it would live longer in the memory image of the application, therefore
						 *  making it more likely that a core dump would reveal it.   
						 */
						dbPassword = em.decrypt(Config.icmDBPasswordCipher, Config.icmDBPasswordIV);
					} catch (Exception decryptionException) {
						/*
						 *  There are numerous possible decryption Exceptions.  Log them.
						 *  See the Connection stanza for knock-on effects.
						 */
						Notification n = new Notification(this,decryptionException);
						n.alert();
						// And re-throw the exception as a ITILException
						throw new ITILException("Cannot get a connection to the ICMDB", decryptionException);
					}
	
					conn = DriverManager.getConnection("jdbc:" + Config.icmDBSoftware + "://" + Config.icmDBHost + 
							":" + new Integer(Config.icmDBPort).toString() + "/" + Config.icmDBName, Config.icmDBUser, dbPassword);
				} catch (SQLException directConnectionException) {
					// Log any errors
					Notification n = new Notification(this,directConnectionException);
					n.alert();
					// And re-throw the exception as a ITILException
					throw new ITILException("Cannot get a connection to the ICMDB", directConnectionException);
				}
			} 
		}

		/*
		 * Wrap everything in a try catch finally block to cleanup resources no matter what
		 */
		try 
		{
			// Prepare a statement based on the JDBC connection using the provided query
			ps = conn.prepareStatement(sql);
			
			for (int i=1; i<=args.size(); i++) {
				Object o = args.get(i-1);
				
				if (o == null) {
					ps.setNull(i, Types.NULL);
					continue;
				}
				
				String cName = args.get(i-1).getClass().getSimpleName();
				if (cName.equals("String")) {
					ps.setString(i, (String)o);
				} else if (cName.equals("Integer")) {
					ps.setInt(i, (Integer)o);
				} else if (cName.equals("Boolean")) {
					ps.setBoolean(i, (Boolean)o);
				} else if (cName.equals("Array")) {
					ps.setArray(i, conn.createArrayOf("int", (Object[])o));
				} else if (cName.equals("Double")) {
					ps.setDouble(i, (Double)o);
				} else if (cName.equals("Date")) {
					ps.setDate(i, (Date)o);
				} else if (cName.equals("Timestamp")) {
					ps.setTimestamp(i, (Timestamp)o);
				} else if (cName.equals("BigDecimal")) {
					ps.setBigDecimal(i, (BigDecimal)o);
				} else if (cName.equals("Integer[]")) {
					ps.setArray(i, conn.createArrayOf("int", (Object[])o));
				} else {
					Notification n = new Notification(this, "Invalid arg passed to executeSQL()");
					n.log();
					// And throw the exception as a ITILException
					throw new ITILException("Invalid arg passed to executeSQL");
				}
			}
			
			// Execute the PreparedStatement and receive a ResultSet
			if (sql.toUpperCase().startsWith("SELECT")) {
				rs = ps.executeQuery();
				
	            // Initialize the number of columns.  Since we don't know this value yet, leave it at 0
	            int numCols = 0;

	            // We need to know the column count.  That info is in the ResultSet meta data
	            numCols = rs.getMetaData().getColumnCount();

	            // For every row in the ResultSet
	            while (rs.next()) {
	            	// Initialize a ITILRow for this ResultSet row
	                ITILRow row = new ITILRow();

	                // For each column in this row, add that object to the ITILRow
	                for (int colNum=1; colNum<=numCols; colNum++) {
	                    Object o = rs.getObject(colNum);
	                    row.add(o);
	                }

	                // Add the ITILRow to allRows
	                allRows.add(row);
	            }
			} else {
				ps.executeUpdate();
			}
		} catch (Exception e) {
			// Log any errors
			Notification n = new Notification(this,e);
			n.alert();
			// And re-throw the exception as a ITILException
			throw new ITILException("Could not execute ICMDB SQL", e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (SQLException e) {
				Notification n = new Notification(this,e);
				n.log();
			}
			try {
				if (ps != null) {
					ps.close();
					ps = null;
				}
			} catch (SQLException e) {
				Notification n = new Notification(this,e);
				n.log();
			}
			try {
				if (conn != null && ! persistConnection) {
					conn.close();
					conn = null;
				}
			} catch (SQLException e) {
				Notification n = new Notification(this,e);
				n.log();
			}
			try {
				if (ctx != null) {
					ctx.close();
					ctx = null;
				}
			} catch (NamingException e) {
				Notification n = new Notification(this,e);
				n.log();
			}
		}
		
		return allRows; 
	}
	
	/**
	 * Provide a DB connection to the CMDB.  This is optional.  If not set, 
	 * one will be automatically generated.  
	 * 
	 * @param conn
	 */
	public void setConnection(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * Get the connection used by this CMDB object.  If the returned Connection
	 * is null, then the CMDB object will generate a new one on demand.
	 * 
	 * @return
	 */
	public Connection getConnection() {
		return conn;
	}
	
	/**
	 * Return the persistConnection value.  If true, the CMDB object will hold
	 * open its connection to the CMDB DB.  If false, it will close the connection
	 * automatically and generate it as needed.  
	 * 
	 * @return the persistConnection
	 */
	public boolean isPersistConnection() {
		return persistConnection;
	}

	/**
	 * Set the persistConnection value.  If true, the CMDB object will hold
	 * open its connection to the CMDB DB.  If false, it will close the connection
	 * automatically and generate it as needed.  
	 * 
	 * @param persistConnection The persistConnection to set
	 */
	public void setPersistConnection(boolean persistConnection) {
		this.persistConnection = persistConnection;
	}
}
