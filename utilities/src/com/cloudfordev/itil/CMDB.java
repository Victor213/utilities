package com.cloudfordev.itil;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Array;
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
 * The Configuration Management Database (CMDB) is a database used to
 * store configuration records throughout their lifecycle. The configuration management
 * system maintains one or more configuration management databases, and each database
 * stores attributes of configuration items, and relationships with other configuration items.
 * 
 * A CI Type is a category that is used to classify configuration items. The CI
 * type identifies the required attributes and relationships for a configuration record.
 * Common CI types include hardware, document, user etc.
 *
 * - Source, ITIL 2011 English Glossary
 *  
 * @author u1001 - Lynn Owens
 * @version 2.0
 */
public class CMDB {
	
	Connection conn = null;
	boolean persistConnection = false;
	
	/**
	 * Instantiate a new instance for access to the CMDB
	 */
	public CMDB() {

	}

	/**
	 * Get the ConfigurationItems that are components of a specified ConfigurationItem.
	 * 
	 * @param ci The parent ConfigurationItem
	 * @return An ArrayList of the component ConfigurationItem objects
	 * @throws ITILException When errors are encountered working with the back-end CMDB
	 */
	public ArrayList<ConfigurationItem> getComponents(ConfigurationItem ci) throws ITILException {
		
		// Specify the SQL of this method
		String sql = "SELECT * FROM cmdb WHERE ARRAY[id] <@ ANY (SELECT dependent_on  FROM cmdb WHERE description = ?)";
		
		// Add the Statement args
		ArrayList<Object> args = new ArrayList<Object>();
		args.add(ci.getDescription());
		
		// Execute the SQL and store the returned rows
		ArrayList<ITILRow> allRows = executeSQL(sql, args);
		
		// Prepare an ArrayList to store ConfigurationItems
		ArrayList<ConfigurationItem> allCIs = new ArrayList<ConfigurationItem>();
		
		// For each returned row, create a ConfigurationItem
		for (ITILRow row : allRows) {
			Integer id = (Integer) row.getObject(0);
			Integer type = (Integer) row.getObject(1);
			String supplier = (String) row.getObject(2);
			BigDecimal cost = (BigDecimal) row.getObject(3);
			Integer wattsUsed = (Integer) row.getObject(4);
			@SuppressWarnings("unchecked")
			ArrayList<Integer> tmpList = (ArrayList<Integer>) row.getObject(5);
			Integer[] dependentOn = (Integer[]) ((ArrayList<Integer>) tmpList).toArray(new Integer[1]);
			BigDecimal shippingCost = (BigDecimal) row.getObject(6);
			Date orderDate = (Date) row.getObject(7);
			Date receiptDate = (Date) row.getObject(8);
			Date installDate = (Date) row.getObject(9);
			Date prodDate = (Date) row.getObject(10);
			Date retireDate = (Date) row.getObject(11);
			String description = (String) row.getObject(12);
			
			// Store the new ConfigurationItem
			allCIs.add(new ConfigurationItem(id, type, supplier, cost, wattsUsed, dependentOn, shippingCost, orderDate, receiptDate, installDate, prodDate, retireDate, description));
		}
		
		// Return all ConfigurationItems
		return allCIs;
	}
	
	/**
	 * Get all the ConfigurationItem objects of a specified type id.  
	 * 
	 * @param typeID The ConifigurationItem Type ID of the ConfigurationItems that are sought. 
	 * @return The ConfigurationItem objects matching the specified type id
	 * @throws ITILException When errors are encountered working with the back-end CMDB
	 */
	public ArrayList<ConfigurationItem> getCIByTypeID(int typeID) throws ITILException {
		
		// Specify the SQL of this method
		String sql = "SELECT * FROM cmdb WHERE type = ? AND retire_date is null ORDER BY description";
		
		// Add the Statement args
		ArrayList<Object> args = new ArrayList<Object>();
		args.add(typeID);
		
		// Execute the SQL and store the returned rows
		ArrayList<ITILRow> allRows = executeSQL(sql, args);
		
		// Prepare an ArrayList to store ConfigurationItems
		ArrayList<ConfigurationItem> allCIs = new ArrayList<ConfigurationItem>();
		
		// For each returned row, create a ConfigurationItem
		for (ITILRow row : allRows) {
			Integer id = (Integer) row.getObject(0);
			Integer type = (Integer) row.getObject(1);
			String supplier = (String) row.getObject(2);
			BigDecimal cost = (BigDecimal) row.getObject(3);
			Integer wattsUsed = (Integer) row.getObject(4);
			@SuppressWarnings("unchecked")
			ArrayList<Integer> tmpList = (ArrayList<Integer>) row.getObject(5);
			Integer[] dependentOn = (Integer[]) ((ArrayList<Integer>) tmpList).toArray(new Integer[1]);
			BigDecimal shippingCost = (BigDecimal) row.getObject(6);
			Date orderDate = (Date) row.getObject(7);
			Date receiptDate = (Date) row.getObject(8);
			Date installDate = (Date) row.getObject(9);
			Date prodDate = (Date) row.getObject(10);
			Date retireDate = (Date) row.getObject(11);
			String description = (String) row.getObject(12);
			
			// Store the new ConfigurationItem
			allCIs.add(new ConfigurationItem(id, type, supplier, cost, wattsUsed, dependentOn, shippingCost, orderDate, receiptDate, installDate, prodDate, retireDate, description));
		}
		
		// Return all ConfigurationItems
		return allCIs;	
	}

	/**
	 * Get a type id by its description. 
	 * 
	 * @param fieldName The description of the ConifigurationItem Type ID 
	 * @return The ConifigurationItem Type ID 
	 * @throws ITILException When errors are encountered working with the back-end CMDB
	 */
	public int getCITypeID(String fieldName) throws ITILException {
		int ciTypeId = 0; 
		
		// Specify the SQL of this method
		String sql = "SELECT id FROM ci_types WHERE retired = false AND type = ?";
		
		// Add the Statement args
		ArrayList<Object> args = new ArrayList<Object>();
		args.add(fieldName);
		
		// Execute the SQL and store the returned rows
		ArrayList<ITILRow> allRows = executeSQL(sql, args);
		
		// We should have at least one result from the lookup
		if (allRows.size() > 0) {
			// If there are results
			// We only want the first result row
			ITILRow row = allRows.get(0);
			// Cast the object in the first field of the row to an integer
			// This is our type id
			ciTypeId = (int) row.getObject(0);
			// Return the found CI Type ID int
			return ciTypeId;
		} else {
			// We did not have any results from our query
			throw new ITILException("CI Type does not exist in CMDB");
		}
	}


	/**
	 * Get all ConfigurationItem objects that match the specified description.
	 * 
	 * @param userDescription The description of the ConfigurationItem objects to be returned
	 * @return All ConfigurationItem objectss matching the description provided
	 * @throws ITILException
	 */
	public ArrayList<ConfigurationItem> getCIByDesc(String userDescription) throws ITILException {

		// Specify the SQL of this method
		String sql = "SELECT * FROM cmdb WHERE retire_date is null AND description = ?";
		
		// Add the Statement args
		ArrayList<Object> args = new ArrayList<Object>();
		args.add(userDescription);
		
		// Execute the SQL and store the returned rows
		ArrayList<ITILRow> allRows = executeSQL(sql, args);
		
		// Prepare an ArrayList to store ConfigurationItems
		ArrayList<ConfigurationItem> allCIs = new ArrayList<ConfigurationItem>();
		
		// For each returned row, create a ConfigurationItem
		for (ITILRow row : allRows) {
			Integer id = (Integer) row.getObject(0);
			Integer type = (Integer) row.getObject(1);
			String supplier = (String) row.getObject(2);
			BigDecimal cost = (BigDecimal) row.getObject(3);
			Integer wattsUsed = (Integer) row.getObject(4);
			@SuppressWarnings("unchecked")
			ArrayList<Integer> tmpList = (ArrayList<Integer>) row.getObject(5);
			Integer[] dependentOn = (Integer[]) ((ArrayList<Integer>) tmpList).toArray(new Integer[1]);
			BigDecimal shippingCost = (BigDecimal) row.getObject(6);
			Date orderDate = (Date) row.getObject(7);
			Date receiptDate = (Date) row.getObject(8);
			Date installDate = (Date) row.getObject(9);
			Date prodDate = (Date) row.getObject(10);
			Date retireDate = (Date) row.getObject(11);
			String description = (String) row.getObject(12);
			
			// Store the new ConfigurationItem
			allCIs.add(new ConfigurationItem(id, type, supplier, cost, wattsUsed, dependentOn, shippingCost, orderDate, receiptDate, installDate, prodDate, retireDate, description));
		}
		
		// Return all ConfigurationItems
		return allCIs;	
	}
	
	/**
	 * Add a ConfigurationItem to the CMDB.
	 * 
	 * @param ci The ConfigurationItem to add to the CMDB
	 * @return The ID of the ConfigurationItem record in the CMDB
	 * @throws ITILException In the case of communications errors to the CMDB
	 */
	public int addCI(ConfigurationItem ci) throws ITILException {
		int ciId = 0;
		
		/*
		 *  The generationHash is simply a unique String to keep track of the new CI we create
		 *  so that later in this method we can query for its CI ID int
		 */
		SecureRandom random = new SecureRandom();
		String generationHash = new BigInteger(130, random).toString(32);

		// Specify the SQL of this insert
		String insertSQL = "INSERT INTO cmdb(type,supplier,cost,watts_used,dependent_on,shipping_cost,order_date,receipt_date,install_date,description,generation_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		// Add the Statement args
		ArrayList<Object> insertArgs = new ArrayList<Object>();
		insertArgs.add(ci.getType());
		insertArgs.add(ci.getSupplier());
		insertArgs.add(ci.getCost());
		insertArgs.add(ci.getWattsUsed());
		insertArgs.add(ci.getDependentOn());
		insertArgs.add(ci.getShippingCost());
		insertArgs.add(ci.getOrderDate());
		insertArgs.add(ci.getReceiptDate());
		insertArgs.add(ci.getInstallDate());
		insertArgs.add(ci.getDescription());
		insertArgs.add(generationHash);
		
		// Execute the SQL
		executeSQL(insertSQL, insertArgs);

		/* 
		 * Now we need to find the newly created CI ID int for return to the user
		 */
		
		// Specify the SQL of this select query
		String querySQL = "SELECT id FROM cmdb WHERE generation_hash = ?";
		
		// Add the Statement args
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
			ciId = (int) row.getObject(0);
			return ciId;
		} else {
			throw new ITILException("Could not find the CI we just inserted in the CMDB");
		}		
		
	}

	/**
	 * Get a ConfigurationItem specified by a CMDB record ID.
	 * 
	 * @param userID The ConfigurationItem CMDB record ID 
	 * @return The ConfigurationItem requested
	 * @throws ITILException In the case of communications errors with the CMDB
	 */
	public ConfigurationItem getCIByID(int userID) throws ITILException {
		ConfigurationItem ci = null;
		
		// Specify the SQL of this method
		String sql = "SELECT * FROM cmdb WHERE retire_date is null AND id = ?";
		
		// Add the Statement args
		ArrayList<Object> args = new ArrayList<Object>();
		args.add(userID);
		
		// Execute the SQL and store the returned rows
		ArrayList<ITILRow> allRows = executeSQL(sql, args);
		
		if (allRows.size() > 0) {
			// If there are results
			// We only want the first result row
			ITILRow row = allRows.get(0);
			
			Integer id = (Integer) row.getObject(0);
			Integer type = (Integer) row.getObject(1);
			String supplier = (String) row.getObject(2);
			BigDecimal cost = (BigDecimal) row.getObject(3);
			Integer wattsUsed = (Integer) row.getObject(4);
			@SuppressWarnings("unchecked")
			ArrayList<Integer> tmpList = (ArrayList<Integer>) row.getObject(5);
			Integer[] dependentOn = (Integer[]) ((ArrayList<Integer>) tmpList).toArray(new Integer[1]);
			BigDecimal shippingCost = (BigDecimal) row.getObject(6);
			Date orderDate = (Date) row.getObject(7);
			Date receiptDate = (Date) row.getObject(8);
			Date installDate = (Date) row.getObject(9);
			Date prodDate = (Date) row.getObject(10);
			Date retireDate = (Date) row.getObject(11);
			String description = (String) row.getObject(12);
			
			// Instantiate the new ConfigurationItem
			ci = new ConfigurationItem(id, type, supplier, cost, wattsUsed, dependentOn, shippingCost, orderDate, receiptDate, installDate, prodDate, retireDate, description);
			
			// And return it
			return ci;
		} else {
			// Or throw an exception
			throw new ITILException("CI Description does not exist in CMDB");
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
				ds = (DataSource)ctx.lookup(Config.cmsJDBCJNDI);
				// Open a connection to the DataSource
				conn = ds.getConnection();
			} catch (Exception jdbcException) {
				// Hello, hum, we may not be in a J2EE container... In that case...
				try {
					// Load the driver
					try {
						Class.forName(Config.cmsDriver);
					} catch (ClassNotFoundException cnfe) {
						Notification n = new Notification(this,cnfe);
						n.alert();
						// And re-throw the exception as a ITILException
						throw new ITILException("Cannot get a connection to the CMDB", cnfe);
					}
					
					// Fire up an EnigmaMachine for database password decrypting
					EnigmaMachine em = new EnigmaMachine(new File(Config.getCpEnigmaKey()));
					String dbPassword = "";
					
					try {
						/*
						 *  This decrypted password is not stored as an instance variable because
						 *  it would live longer in the memory image of the application, therefore
						 *  making it more likely that a core dump would reveal it.   
						 */
						dbPassword = em.decrypt(Config.cmsDBPasswordCipher, Config.cmsDBPasswordIV);
					} catch (Exception decryptionException) {
						/*
						 *  There are numerous possible decryption Exceptions.  Log them.
						 *  See the Connection stanza for knock-on effects.
						 */
						Notification n = new Notification(this,decryptionException);
						n.alert();
						// And re-throw the exception as a ITILException
						throw new ITILException("Cannot get a connection to the CMDB", decryptionException);
					}
	
					conn = DriverManager.getConnection("jdbc:" + Config.cmsDBSoftware + "://" + Config.cmsDBHost + 
							":" + new Integer(Config.cmsDBPort).toString() + "/" + Config.cmsDBName, Config.cmsDBUser, dbPassword);
				} catch (SQLException directConnectionException) {
					// Log any errors
					Notification n = new Notification(this,directConnectionException);
					n.alert();
					// And re-throw the exception as a ITILException
					throw new ITILException("Cannot get a connection to the CMDB", directConnectionException);
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
				}   else {
					Notification n = new Notification(this, "Invalid arg passed to executeSQL()");
					n.log();
					// And throw the exception as a ITILException
					throw new ITILException("Invalid arg [" + cName + "] passed to executeSQL");
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
	                    
	                    // JDBC4Array is a real pain in the butt
	                    ArrayList<Integer> tmpList = new ArrayList<Integer>();
	                    if (o != null) {
		                    if (o.getClass().getSimpleName().endsWith("Array")) {
		                    	// At least at this time, these Arrays are all Integer[]
		                    	Array a = (Array) o;
		                    	Integer[] ints = (Integer[]) a.getArray();
		                    	for (Integer i : ints) {
		                    		tmpList.add(i);
		                    	}
		                    	o = tmpList;
		                    }
	                    }
	                    
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
			throw new ITILException("Could not execute CMDB SQL", e);
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
