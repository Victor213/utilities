package com.cloudfordev.itil;

import java.sql.Timestamp;

/**
 * An unplanned interruption to an IT service or reduction in
 * the quality of an IT service. Failure of a configuration item that has not yet affected
 * service is also an incident – for example, failure of one disk from a mirror set.
 * 
 * - Source, ITIL 2011 English Glossary
 * 
 * @author u1001
 * @version 1.0     
 */
public class Incident {
	
	private Integer id = 0;
	private ConfigurationItem ci = null;
	private Timestamp startDate = null;
	private Timestamp resolveDate = null;
	private String source = "";
	private int severity = 0;
	private int errorNo = 0;
	private String errorMsg = "";
	private String contactEmail = "";
	private boolean isResolved = false;
	private String resolution = "";
	
	/**
	 * Create an unresolved Incident</br>
	 * </br>
	 * Incidents of severity 3 will email and log.</br>
	 * Incidents of severity 1 or 2 will page and log.</br>
	 * 
	 * @param ci The ConfigurationItem impacted by this Incident
	 * @param startDate The start time of this Incident
	 * @param source The monitor that reported this Incident
	 * @param severity The severity of this Incident
	 * @param errorNo An associated error number
	 * @param errorMsg An associated error message
	 * @param contactEmail An email address that should receive Notification of this Incident
	 */
	public Incident(ConfigurationItem ci, Timestamp startDate,
			String source, int severity, int errorNo,
			String errorMsg, String contactEmail) {
		
		super();
		
		this.ci = ci;
		this.startDate = startDate;
		this.source = source;
		this.severity = severity;
		this.errorNo = errorNo;
		this.errorMsg = errorMsg;
		this.contactEmail = contactEmail;
		
	}

	/**
	 * Create an Incident, which may be resolved.  Optionally, nulls may be passed in to resolveDate and resolution.</br>
	 * 
	 * @param ci The ConfigurationItem impacted by this Incident
	 * @param startDate The start time of this Incident
	 * @param resolveDate The end time of this Incident
	 * @param source The monitor that reported this Incident
	 * @param severity The severity of this Incident
	 * @param errorNo An associated error number
	 * @param errorMsg An associated error message
	 * @param contactEmail An email address that should receive Notification of this Incident
	 */
	public Incident(ConfigurationItem ci, Timestamp startDate,
			Timestamp resolveDate, String source, int severity, int errorNo,
			String errorMsg, String contactEmail, String resolution) {
		
		super();
		
		this.ci = ci;
		this.startDate = startDate;
		this.resolveDate = resolveDate;
		this.source = source;
		this.severity = severity;
		this.errorNo = errorNo;
		this.errorMsg = errorMsg;
		this.contactEmail = contactEmail;
		this.resolution = resolution;
		
		if (resolveDate != null) {
			isResolved = true;
		}
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the ci
	 */
	public ConfigurationItem getCi() {
		return ci;
	}

	/**
	 * @param ci the ci to set
	 */
	public void setCi(ConfigurationItem ci) {
		this.ci = ci;
	}

	/**
	 * @return the startDate
	 */
	public Timestamp getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the resolveDate
	 */
	public Timestamp getResolveDate() {
		return resolveDate;
	}

	/**
	 * @param resolveDate the resolveDate to set
	 */
	public void setResolveDate(Timestamp resolveDate) {
		this.resolveDate = resolveDate;
		isResolved = true;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the severity
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * @param severity the severity to set
	 */
	public void setSeverity(int severity) {
		this.severity = severity;
	}

	/**
	 * @return the errorNo
	 */
	public int getErrorNo() {
		return errorNo;
	}

	/**
	 * @param errorNo the errorNo to set
	 */
	public void setErrorNo(int errorNo) {
		this.errorNo = errorNo;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * @return the contactEmail
	 */
	public String getContactEmail() {
		return contactEmail;
	}

	/**
	 * @param contactEmail the contactEmail to set
	 */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	/**
	 * @return the isResolved
	 */
	public boolean isResolved() {
		return isResolved;
	}

	/**
	 * @return
	 */
	public String getResolution() {
		return resolution;
	}

	/**
	 * @param resolution
	 */
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
}
