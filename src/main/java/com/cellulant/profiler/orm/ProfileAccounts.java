package com.cellulant.profiler.orm;

import java.io.Serializable;
import java.util.Date;

public class ProfileAccounts implements Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private long profileAccountID;
	private String accountNummber;
	private Profile profile;
	private Date paymentDate;
	private Date DateModified;
	public long getProfileAccountID() {
		return profileAccountID;
	}
	public void setProfileAccountID(long profileAccountID) {
		this.profileAccountID = profileAccountID;
	}
	public String getAccountNummber() {
		return accountNummber;
	}
	public void setAccountNummber(String accountNummber) {
		this.accountNummber = accountNummber;
	}


	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Date getDateModified() {
		return DateModified;
	}

	public void setDateModified(Date dateModified) {
		DateModified = dateModified;
	}

}
