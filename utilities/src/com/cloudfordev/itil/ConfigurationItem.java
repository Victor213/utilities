package com.cloudfordev.itil;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * A Configuration Item (CI) is any component or other service asset that needs to be
 * managed in order to deliver an IT service.  Information about each configuration item is
 * recorded in a configuration record within the configuration management system and is
 * maintained throughout its lifecycle by service asset and configuration management.
 * Configuration items are under the control of change management. They typically include
 * IT services, hardware, software, buildings, people and formal documentation such as
 * process documentation and service level agreements.
 * 
 * - Source, ITIL 2011 English Glossary
 * 
 * @author u1001
 * @version 1.0
 */
public class ConfigurationItem {
	private Integer id;
	private Integer type;
	private String supplier;
	private BigDecimal cost;
	private Integer wattsUsed;
	private Integer[] dependentOn;
	private BigDecimal shippingCost;
	private Date orderDate;
	private Date receiptDate;
	private Date installDate;
	private Date prodDate;
	private Date retireDate;
	private String description;
	private boolean isProd = true;
	
	/**
	 * Instantiate a new ConfigurationItem with only the required information.
	 * 
	 * @param type The ConifigurationItem Type ID 
	 * @param description The Description of the ConfigurationItem
	 */
	public ConfigurationItem(Integer type, String description) {
		super();

		this.type = type;
		this.description = description;
	}
	
	/**
	 * Instantiate a new ConfigurationItem with all the possible fields.
	 * 
	 * @param type The ConifigurationItem Type ID 
	 * @param supplier Supplier of the ConfigurationItem, or "" if N/A
	 * @param cost Cost of the ConfigurationItem, or 0.00 if N/A
	 * @param wattsUsed The watts used by this Configuration Item, or 0 if N/A
	 * @param dependentOn An Integer[] of all ConfigurationItem record ids that are components of this ConfigurationItem
	 * @param shippingCost The shipping cost of this ConfigurationItem, or 0.00 if N/A
	 * @param orderDate The date this ConfigurationItem was ordered, or null if N/A
	 * @param receiptDate The date this ConfigurationItem was received, or null if N/A
	 * @param installDate The date this ConfigurationItem was installed, or null if N/A
	 * @param prodDate The date this ConfigurationItem entered production, or null if N/A
	 * @param retireDate The date this ConfigurationItem was retired, or null if N/A
	 * @param description The description of this ConfigurationItem.  This should include version or model information.
	 */
	public ConfigurationItem(Integer type, String supplier, BigDecimal cost,
			Integer wattsUsed, Integer[] dependentOn,
			BigDecimal shippingCost, Date orderDate, Date receiptDate,
			Date installDate, Date prodDate, Date retireDate, String description) {
		super();
		
		this.type = type;
		this.supplier = supplier;
		this.cost = cost;
		this.wattsUsed = wattsUsed;
		this.dependentOn = dependentOn;
		this.shippingCost = shippingCost;
		this.orderDate = orderDate;
		this.receiptDate = receiptDate;
		this.installDate = installDate;
		this.prodDate = prodDate;
		this.retireDate = retireDate;
		this.description = description;
		
		if (prodDate != null) {
			isProd = true;
		} else {
			isProd = false;
		}
	}

	/**
	 * Instantiate a ConfigurationItem that is sourced from an existing record
	 * in the CMDB.
	 * 
	 * @param id The CMDB record ID of this ConfigurationItem
	 * @param type The ConifigurationItem Type ID 
	 * @param supplier Supplier of the ConfigurationItem, or "" if N/A
	 * @param cost Cost of the ConfigurationItem, or 0.00 if N/A
	 * @param wattsUsed The watts used by this Configuration Item, or 0 if N/A
	 * @param dependentOn An Integer[] of all ConfigurationItem record ids that are components of this ConfigurationItem
	 * @param shippingCost The shipping cost of this ConfigurationItem, or 0.00 if N/A
	 * @param orderDate The date this ConfigurationItem was ordered, or null if N/A
	 * @param receiptDate The date this ConfigurationItem was received, or null if N/A
	 * @param installDate The date this ConfigurationItem was installed, or null if N/A
	 * @param prodDate The date this ConfigurationItem entered production, or null if N/A
	 * @param retireDate The date this ConfigurationItem was retired, or null if N/A
	 * @param description The description of this ConfigurationItem.  This should include version or model information.
	 */
	public ConfigurationItem(Integer id, Integer type, String supplier, BigDecimal cost,
			Integer wattsUsed, Integer[] dependentOn,
			BigDecimal shippingCost, Date orderDate, Date receiptDate,
			Date installDate, Date prodDate, Date retireDate, String description) {
		super();
		this.id = id;
		this.type = type;
		this.supplier = supplier;
		this.cost = cost;
		this.wattsUsed = wattsUsed;
		this.dependentOn = dependentOn;
		this.shippingCost = shippingCost;
		this.orderDate = orderDate;
		this.receiptDate = receiptDate;
		this.installDate = installDate;
		this.prodDate = prodDate;
		this.retireDate = retireDate;
		this.description = description;
		
		if (prodDate != null) {
			isProd = true;
		} else {
			isProd = false;
		}
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the supplier
	 */
	public String getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the cost
	 */
	public BigDecimal getCost() {
		return cost;
	}

	/**
	 * @param cost the cost to set
	 */
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	/**
	 * @return the wattsUsed
	 */
	public Integer getWattsUsed() {
		return wattsUsed;
	}

	/**
	 * @param wattsUsed the wattsUsed to set
	 */
	public void setWattsUsed(Integer wattsUsed) {
		this.wattsUsed = wattsUsed;
	}

	/**
	 * @return the dependentOn
	 */
	public Integer[] getDependentOn() {
		return dependentOn;
	}

	/**
	 * @param dependentOn the dependentOn to set
	 */
	public void setDependentOn(Integer[] dependentOn) {
		this.dependentOn = dependentOn;
	}

	/**
	 * @return the shippingCost
	 */
	public BigDecimal getShippingCost() {
		return shippingCost;
	}

	/**
	 * @param shippingCost the shippingCost to set
	 */
	public void setShippingCost(BigDecimal shippingCost) {
		this.shippingCost = shippingCost;
	}

	/**
	 * @return the orderDate
	 */
	public Date getOrderDate() {
		return orderDate;
	}

	/**
	 * @param orderDate the orderDate to set
	 */
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	/**
	 * @return the receiptDate
	 */
	public Date getReceiptDate() {
		return receiptDate;
	}

	/**
	 * @param receiptDate the receiptDate to set
	 */
	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	/**
	 * @return the installDate
	 */
	public Date getInstallDate() {
		return installDate;
	}

	/**
	 * @param installDate the installDate to set
	 */
	public void setInstallDate(Date installDate) {
		this.installDate = installDate;
	}

	/**
	 * @return the prodDate
	 */
	public Date getProdDate() {
		return prodDate;
	}

	/**
	 * @param prodDate the prodDate to set
	 */
	public void setProdDate(Date prodDate) {
		this.prodDate = prodDate;
	}

	/**
	 * @return the retireDate
	 */
	public Date getRetireDate() {
		return retireDate;
	}

	/**
	 * @param retireDate the retireDate to set
	 */
	public void setRetireDate(Date retireDate) {
		this.retireDate = retireDate;
	}

	/**
	 * @return the isProd
	 */
	public boolean isProd() {
		return isProd;
	}

	/**
	 * @param isProd the isProd to set
	 */
	public void setProd(boolean isProd) {
		this.isProd = isProd;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
