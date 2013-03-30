package com.cloudfordev.util;

/**
 * InputScreenerException describes the condition by which an input String failed a screening test.
 * 
 * @author u1001 Lynn Owens
 * @version 1.0
 */
public class InputScreenerException extends Exception {

    /// Serialization
	private static final long serialVersionUID = 1377727886826132648L;

	/**
	 * Instantiate a new, empty InputScreenerException
	 */
	public InputScreenerException(){
        super();
    }

    /**
     * Instantiate a new InputScreenerException
     * 
     * @param message A string message describing the screening failure 
     */
    public InputScreenerException(String message){
        super(message);
    }
    
    /**
     * Instantiate a new InputScreenerException
     * 
     * @param message A string message describing the screening failure  
     * @param t An encapsulated exception
     */
    public InputScreenerException(String message, Throwable t){
        super(message, t);
    }
}
