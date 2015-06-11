
package com.cellulant.profiler.orm;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Sylvester Timona
 * Class represents customer object
 *
 */

public class Customer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int customerId;
	private String firstName;
	private String lastName;
	private String middleName;
	private int nationalId;
	private Date dateCreated;
	private String email;
	private String address;
	private Date dateModified;
	
	public int getNationalId() {
		return nationalId;
	}
	public void setNationalId(int nationalId) {
		this.nationalId = nationalId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public Date getDateModified() {
		return dateModified;
	}
	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}
	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", firstName="
				+ firstName + ", lastName=" + lastName + ", middleName="
				+ middleName + ", nationalId=" + nationalId + ", dateCreated="
				+ dateCreated + ", email=" + email + ", address=" + address
				+ ", dateModified=" + dateModified + "]";
	}
	

}
