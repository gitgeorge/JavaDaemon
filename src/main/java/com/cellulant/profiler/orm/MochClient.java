package com.cellulant.profiler.orm;

import java.util.Date;



public class MochClient  {
	 private int 	 custID ;    
	 private String custNames  ;
	 private String email;
	 private String postAddress;
	 private String nationalID;
	    
			 
	 
	public int getCustID() {
		return custID;
	}



	public void setCustID(int custID) {
		this.custID = custID;
	}



	public String getCustNames() {
		return custNames;
	}



	public void setCustNames(String custNames) {
		this.custNames = custNames;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getPostAddress() {
		return postAddress;
	}



	public void setPostAddress(String postAddress) {
		this.postAddress = postAddress;
	}



	public String getNationalID() {
		return nationalID;
	}



	public void setNationalID(String nationalID) {
		this.nationalID = nationalID;
	}



	@Override
	public String toString() {
		return "MochaClient [custID=" + custID + ", custNames=" + custNames
				+ ", email=" + email + ", postAddress="
				+ postAddress + ",nationalID="+nationalID+"]";
	}
	 
}
