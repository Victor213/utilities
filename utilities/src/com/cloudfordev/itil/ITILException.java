package com.cloudfordev.itil;

/**
 * ITILException describes an encapsulated exception state encountered while 
 * perorming ITIL operations.
 * 
 * @author u1001 Lynn Owens
 * @version 1.0
 */
public class ITILException extends Exception {

	// Serialization
	private static final long serialVersionUID = -4480759266752735633L;

	/**
	 * Instantiate a new, empty ITILException
	 */
	public ITILException(){
        super();
    }

	
    /**
     * Instantiate a new ITILException
     * 
     * @param message A string message describing the exception 
     */
    public ITILException(String message){
        super(message);
    }
    
    /**
     * Instantiate a new ITILException
     * 
     * @param message A string message describing the exception 
     * @param t The root cause Exception
     */
    public ITILException(String message, Throwable t){
        super(message, t);
    }
    
    /**
     * Instantiate a new ITILException
     * 
     * @param t The root cause Exception
     */
    public ITILException(Throwable t){
        super(t);
    }
}
