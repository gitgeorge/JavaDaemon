package com.cellulant.profiler.orm;

import java.io.Serializable;
import java.util.Date;

public class Profile implements Serializable {

private static final long serialVersionUID = 1L;
private long profileId;
private Customer customer;
private String MSISDN;
private Date dateCreated;
private Date dateModified;


public Date getDateCreated() {
	return dateCreated;
}
public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
}
public Date getDateModified() {
	return dateModified;
}
public void setDateModified(Date dateModified) {
	this.dateModified = dateModified;
}
public long getProfileId() {
	return profileId;
}
public void setProfileId(long profileId) {
	this.profileId = profileId;
}
public String getMSISDN() {
	return MSISDN;
}
public void setMSISDN(String mSISDN) {
	MSISDN = mSISDN;
}

public Customer getCustomer() {
	return customer;
}
public void setCustomer(Customer customer) {
	this.customer = customer;
}



}
