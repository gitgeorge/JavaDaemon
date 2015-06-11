package com.cellulant.profiler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.cellulant.profiler.orm.MochClient;
import com.cellulant.profiler.utils.PropsReg;

public class ProcessRecord implements Runnable {
    /* The MySQL connection object.
     */

    private DataSource mochaDatasource;
    private MochClient mochaClient;
    private Register register;
    private PropsReg props;

    private Logger logger = Logger.getLogger(getClass());

    public ProcessRecord(DataSource dtbMochaSource, MochClient mochaClient) {
        this.mochaDatasource = dtbMochaSource;
        this.mochaClient = mochaClient;
        register = new Register();
    }

    public void processRequest() {
        logger.info("Starting processing Record: " + mochaClient);
        processRecord();
        logger.info("Finished Processing Record: " + mochaClient);
    }

    /**
     * Send Payments
     *
     *
     */
    public void processRecord() {
        Connection conn = null;
        PreparedStatement preparedStmt;
        String response = null;
        try {
            conn = mochaDatasource.getConnection();
            String profileQuery = "SELECT *  FROM profiles where custID=?";
            PreparedStatement preparedStatement = conn
                    .prepareStatement(profileQuery);

            preparedStatement.setInt(1, mochaClient.getCustID());
            ResultSet rs = preparedStatement.executeQuery();
            rs.last();
            logger.info("checking for existing profile for customer ID:" + mochaClient.getCustID());

            int count = rs.getRow();

            if (count == 0) {
                logger.info("No record found for customer ID:" + mochaClient.getCustID());
                updatecustomerStatus(mochaClient.getCustID(), 5);

            }
            if (count == 1) {
                logger.info("One profile found for customer ID:" + mochaClient.getCustID() + "about to post to wallet");

                int profileID = 0;
                String MSISDN = null;
                rs.first();

                profileID = rs.getInt("profileID");
                MSISDN = rs.getString("mobileNumber");
                logger.info("This is the profileID" + profileID);

                response = registerCustomer(profileID, MSISDN);
                	

            }
            if (count > 1) {
                updatecustomerStatus(mochaClient.getCustID(), 11);
                logger.info("This customer has more than one profile" + mochaClient);
                logger.error("This customer has more than one profile" + mochaClient);
            }

            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.error("Error while checking accountNumber: " + mochaClient + "Error Message  " + e.getLocalizedMessage());
        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    // logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }

    }

    public String registerCustomer(int profID, String mSISDN) {
        Connection conn = null;
        PreparedStatement preparedStmt;
        String response = null;
        try {
            conn = mochaDatasource.getConnection();
            String profileQuery = "SELECT *  FROM accounts where profileID=? and statusCode=0 order by 1 asc";
            PreparedStatement preparedStatement = conn
                    .prepareStatement(profileQuery);

            preparedStatement.setInt(1, profID);
            ResultSet rs = preparedStatement.executeQuery();
            rs.last();
            logger.info("checking for existing account for profile ID:" + profID);

            int count = rs.getRow();
            int accountID = 0;
            String accountNumber = null;
            String branchID = null;
            String accountAlias = null;
            String tariff = null;
            String isDefault = null;
            if (count == 0) {
                logger.info("No record found for  profile ID:" + profID);

                updatecustomerStatus(mochaClient.getCustID(), 6);
                updateprofileStatus(profID, 6);

            }
            if (count == 1) {
                logger.info("One account found for  profile ID:" + profID + "about to post to wallet");

                rs.beforeFirst();
                while (rs.next()) {
                    accountID = rs.getInt("accID");
                    accountNumber = rs.getString("accountNumber");
                    branchID = rs.getString("branch");
                    accountAlias = rs.getString("alias");
                    tariff = rs.getString("tariffID");
                    isDefault = rs.getString("isDefault");
                    //isDefault="1";
                    logger.info("This is the accountNumber" + accountNumber);
                }
                response = registerCustomerAPICall(accountNumber, mSISDN, profID, mochaClient.getCustID(), accountID, accountAlias, branchID, tariff, isDefault);

            }
            if (count > 1) {

                rs.first();
                accountID = rs.getInt("accID");
                accountNumber = rs.getString("accountNumber");
                accountAlias = rs.getString("alias");
                branchID = rs.getString("branchID");
                tariff = rs.getString("tariffID");
                isDefault = rs.getString("isDefault");
                //isDefault="0";
                response = registerCustomerAPICall(accountNumber, mSISDN, profID, mochaClient.getCustID(), accountID, accountAlias, branchID, tariff, isDefault);

            } else {
                logger.info(" new record for profile:");
            }
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.error("Error while checking accountNumber: " + mochaClient + "Error Message  " + e.getLocalizedMessage());
        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    // logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }
        return response;
    }

    public String registerCustomerAPICall(String accountNumber, String mSISDN, int profID, int custID, int accountID, String accountAlias, String branchID, String tariff, String isDefault) {

        String processingCode = "960000";
        HashMap<String, String> requestMap = new HashMap<String, String>();
        String[] CustNames = mochaClient.getCustNames().split("\\s+");
        logger.info("This is the Surname:" + CustNames[0]);
        String firstName = CustNames[0];
        // String OtherNames=CustNames[1]+CustNames[2];
        StringBuffer result = new StringBuffer();
        for (int i = 1; i < CustNames.length; i++) {
            result.append(CustNames[i] + " ");
        }
        String otherNames = result.toString();

        requestMap.put("firstName", firstName);
        requestMap.put("otherNames", otherNames);
        requestMap.put("ID", mochaClient.getNationalID());
        // requestMap.put("ID","789099");
        requestMap.put("email", mochaClient.getEmail());
        requestMap.put("postAddress", mochaClient.getPostAddress());
        requestMap.put("MSISDN", mSISDN);
        requestMap.put("branchID", branchID);
        //requestMap.put("MSISDN","254724865367");
        requestMap.put("accountNumber", accountNumber);
        requestMap.put("tariff", tariff);
        requestMap.put("isDefault", "1");
        requestMap.put("accountAlias", accountAlias);
        logger.info("posting to hub with processing code ---->" + processingCode +"\t"+ requestMap);
        String RG = register.postRequest(processingCode, requestMap);
        logger.info("This is the response we got" + RG.trim());
        if (RG.trim().equals("03")) {
            logger.info("Exception while posting to wallet");
            requestMap.clear();
            updatecustomerStatus(custID, 3);
            updateprofileStatus(profID, 3);
            updateAccountStatus(accountID, 3);
            // return RG;
        }
        //String success="00";
        if (RG.trim().equals("00")) {
            logger.info("Posting to wallet successful" + requestMap);

            updatecustomerStatus(custID, 1);
            logger.info("***************customer update done");
            updateprofileStatus(profID, 1);
            logger.info("***************profiles update done");
            updateAccountStatus(accountID, 1);
            logger.info("***************accounts update done");
            processProfileAccounts(profID, mSISDN, 1);
            logger.info("***************profile accounts done");
            processProfileEnrolments(profID, mSISDN, 1);
            logger.info("***************profile enrolments done");
            processProfileNominations(profID, mSISDN, 1);
            logger.info("***************profile nominations done");

            requestMap.clear();
        } else {
            logger.info("dont know what todo");
            requestMap.clear();
            requestMap.clear();
            updatecustomerStatus(custID, 3);
            updateprofileStatus(profID, 3);
            updateAccountStatus(accountID, 3);
        }

        return RG;
    }
    /* public void attachProfileAPICall(int profID,String mSISDN){
     String processingCode="962000";
     HashMap<String, String> requestMap =new HashMap<String, String>();
	   
     requestMap.put("ID","27529909");
	   
     //
     updateprofileStatus(profID,1);
	  
     }*/

    public void attachAccountAPICall(String accountNumber, String accountAlias, int profID, int accountID, String mSISDN, String branchID, String tariff, String isDefault, String branchCode, String currency) {
        String processingCode = "961000";
        HashMap<String, String> requestMap = new HashMap<String, String>();
        logger.info("ADDING EXTRA ACCOUNTS");

        requestMap.put("MSISDN", mSISDN);
        requestMap.put("accountNo", accountNumber);
        requestMap.put("accountAlias", accountAlias);
        requestMap.put("branchID", branchID);
        requestMap.put("branchCode", branchCode);
        requestMap.put("currencyID", currency);
        requestMap.put("tariff", tariff);
        requestMap.put("isDefault", "0");
        String RG = register.postRequest(processingCode, requestMap);
        logger.info("Adding accountt got this" + RG.trim());

        if (RG.trim().equals("00")) {
            logger.info("Posting extra account to wallet successful" + requestMap);
            requestMap.clear();
            updateAccountStatus(accountID, 1);
        } else {
            logger.info("posting the extras account:" + accountID + " failed");
            updateAccountStatus(accountID, 3);
        }

    }

    private void attachEnrolmentsAPICall(String payeeName, int profID,
            String enrolmentAlias, String mSISDN, int enrolmentID, String payeeNumber) {
        String processingCode = "967000";
        HashMap<String, String> requestMap = new HashMap<String, String>();
        String merchantCode = payeeName.trim();
        requestMap.put("MSISDN", mSISDN);
        requestMap.put("merchantID", merchantCode);
        requestMap.put("refNumber", payeeNumber);
        requestMap.put("accountAlias", enrolmentAlias);
        requestMap.put("currencyID", "70");
        String RG = register.postRequest(processingCode, requestMap);
        logger.info("This is the enrol response we got" + RG.trim());

        if (RG.trim().equals("00")) {
            logger.info("Posting enrolment to wallet successful" + requestMap);
            requestMap.clear();
            updateEnrolmentStatus(enrolmentID, 1);
        } else {
            logger.info("posting enrolment:" + enrolmentID + " failed");
            updateEnrolmentStatus(enrolmentID, 3);
        }
    }

    private void attachNominationsAPICall(String accountNumber,
            String accountAlias, int profID, String mSISDN, int nominationID, String branchID, String branchCode) {
        String processingCode = "966000";
        HashMap<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("MSISDN", mSISDN);
        requestMap.put("accountAlias", accountAlias);
        requestMap.put("accountNo", accountNumber);
        requestMap.put("branchID", branchID);
        requestMap.put("branchCode", branchCode);
        String RG = register.postRequest(processingCode, requestMap);
        logger.info("This is the nomination response we got" + RG.trim());

        if (RG.trim().equals("00")) {
            logger.info("Posting nomination to wallet successful" + requestMap);
            requestMap.clear();
            updateNominationStatus(nominationID, 1);
        } else {
            logger.info("posting nomination:" + nominationID + " failed");
            updateNominationStatus(nominationID, 3);
        }

		// TODO Auto-generated method stub
    }

    public void processProfileAccounts(int profID, String mSISDN, int type) {
        Connection conn = null;
        PreparedStatement preparedStmt;
        try {

            conn = mochaDatasource.getConnection();
            String profileQuery = "SELECT a.accID,a.currency,b.branchID,b.branchCode,a.accountNumber,a.alias,a.tariffID,a.isDefault  FROM accounts a inner join branches b using(branchID) where a.profileID=? and a.statusCode=0";
            PreparedStatement preparedStatement = conn
                    .prepareStatement(profileQuery);

            preparedStatement.setInt(1, profID);
            ResultSet rs = preparedStatement.executeQuery();
            rs.last();
            logger.info("checking for extra account for profile ID:" + profID);

            int count = rs.getRow();

            if (count == 0) {
                logger.info("No extra account found for  profile ID:" + profID);

            }
            if (count > 0) {
                logger.info("we found an extra for  profile ID:" + profID);
                int accountID = 0;
                String accountNumber = null;
                String accountAlias = null;
                String branchID = null;
                String tariff = null;
                String branchCode = null;
                String isDefault = null;
                String currency = null;

                rs.beforeFirst();

                while (rs.next()) {
                    logger.info("updating tick tock ");
                    accountID = rs.getInt("accID");
                    accountNumber = rs.getString("accountNumber");
                    accountAlias = rs.getString("alias");
                    branchID = rs.getString("branchID");
                    branchCode = rs.getString("branchCode");
                    tariff = rs.getString("tariffID");
                    isDefault = rs.getString("isDefault");
                    currency = rs.getString("currency");

                    //get alias
                    accountAlias = getAlias(profID, accountAlias, accountID, "accounts", "alias");
                    attachAccountAPICall(accountNumber, accountAlias, profID, accountID, mSISDN, branchID, tariff, isDefault, branchCode, currency);

                }

            }

            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.info("Error while checking accountNumber: " + mochaClient + "Error Message  " + e.getLocalizedMessage());
        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    // logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }

    }

    public String getAlias(int profID, String accalias, int accountID, String tableName, String columnName) {
        String alias = accalias;

        Connection conn = null;
        PreparedStatement preparedStmt;
        try {

            conn = mochaDatasource.getConnection();
            String profileQuery = "Select * from " + tableName + " where profileID=? and " + columnName + "  =? and statuscode =1";
            PreparedStatement preparedStatement = conn
                    .prepareStatement(profileQuery);
            preparedStatement.setInt(1, profID);
            preparedStatement.setString(2, accalias);
            ResultSet rs = preparedStatement.executeQuery();
            rs.last();
            logger.info("checking for existing account  alias for profile ID:" + profID);

            int count = rs.getRow();

            if (count == 0) {
                logger.info("No record found for  profile ID:" + profID + " with alias " + accalias);
                return alias;

            }
            if (count > 0) {
                logger.info("record found for  profile ID:" + profID + " with alias " + accalias + "new alias");
                return alias + "_" + accountID;
            }

            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.error("Error while checking accountNumber: " + mochaClient + "Error Message  " + e.getLocalizedMessage());
        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    // logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }
        return alias;

    }

    public void processProfileNominations(int profID, String MSISDN, int type) {
        Connection conn = null;
        PreparedStatement preparedStmt;
        try {

            conn = mochaDatasource.getConnection();
            String profileQuery = "SELECT nominatedAccounts.accountNumber,nominatedAccounts.nominatedAccountID,nominatedAccounts.accountNames,branches.branchID,branches.branchcode FROM nominatedAccounts inner join accounts using (accountNumber) inner join branches using(branchID) where nominatedAccounts.profileID=? and nominatedAccounts.statusCode=0";
            PreparedStatement preparedStatement = conn
                    .prepareStatement(profileQuery);

            preparedStatement.setInt(1, profID);
            ResultSet rs = preparedStatement.executeQuery();
            rs.last();
            logger.info("checking for existing account for profile ID:" + profID);

            int count = rs.getRow();

            if (count == 0) {
                logger.info("No record found for  profile ID:" + profID);

            }
            if (count > 0) {
                String accountNumber = null;
                String accountAlias = null;
                int nominationID = 0;
                String branchCode = null;
                String branchID = null;
                rs.beforeFirst();

                while (rs.next()) {
                    accountNumber = rs.getString("accountNumber");
                    accountAlias = rs.getString("accountNames");
                    nominationID = rs.getInt("nominatedaccountID");
                    branchCode = rs.getString("branchCode");
                    branchID = rs.getString("branchID");
                    accountAlias = getAlias(profID, accountAlias, nominationID, "nominatedAccounts", "accountNames");

                    attachNominationsAPICall(accountNumber, accountAlias, profID, MSISDN, nominationID, branchID, branchCode);
                }

            } else {
                logger.info(" new record for profile:");
            }
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.error("Error while checking accountNumber: " + mochaClient + "Error Message  " + e.getLocalizedMessage());
        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    // logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }

    }

    public void processProfileEnrolments(int profID, String mSISDN, int type) {
        Connection conn = null;
        PreparedStatement preparedStmt;
        try {

            conn = mochaDatasource.getConnection();
            String profileQuery = "SELECT *  FROM enrolments where profileID=? and statusCode=0 order by 1 asc";
            PreparedStatement preparedStatement = conn
                    .prepareStatement(profileQuery);

            preparedStatement.setInt(1, profID);
            ResultSet rs = preparedStatement.executeQuery();
            rs.last();
            logger.info("checking for existing account for profile ID:" + profID);

            int count = rs.getRow();

            if (count == 0) {
                logger.info("No enrolments found for  profile ID:" + profID);

            }
            if (count > 0) {
                if (type == 0) {
                    rs.beforeFirst();
                }
                if (type == 1) {
                    rs.first();
                }
                String payeeName = null;
                String payeeNumber = null;
                int enrolmentID = 0;
                String enrolmentAlias = null;
					//String accountAlias=null;

                while (rs.next()) {
                    payeeName = rs.getString("payeeName");
                    payeeNumber = rs.getString("payeeNumber");
                    enrolmentID = rs.getInt("enrolmentID");
                    enrolmentAlias = rs.getString("alias");
                    enrolmentAlias = getAlias(profID, enrolmentAlias, enrolmentID, "enrolments", "alias");

                    attachEnrolmentsAPICall(payeeName, profID, enrolmentAlias, mSISDN, enrolmentID, payeeNumber);

                }

            }
            /*else{
             logger.info(" new record for profile:");
             }*/
            preparedStatement.close();
            conn.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            logger.error("Error while checking accountNumber: " + mochaClient + "Error Message  " + e.getLocalizedMessage());
        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    // logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }

    }

    private int getKey(ResultSet rs) throws SQLException {
        rs.next();
        return rs.getInt(1);
    }

    private void updatecustomerStatus(int custID, int updateToCode) {
        Connection conn = null;

        try {
            logger.info("updating customer : "
                    + mochaClient);
            conn = mochaDatasource.getConnection();
            String query = "UPDATE  customers SET statusCode=? WHERE custID=? limit ?";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, updateToCode);

            preparedStatement.setInt(2, mochaClient.getCustID());
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();
            logger.info("customer successfully transferred to wallet : " + mochaClient);
            preparedStatement.close();
            //conn.close();

        } catch (Exception e) {
            logger.error("error while updating transaction : " + mochaClient
                    + e.getLocalizedMessage());
            e.printStackTrace();

        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    // logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }
    }

    private void updateprofileStatus(int profileID, int updateToCode) {
        Connection conn = null;

        try {
            logger.info("updating profile with payload : "
                    + mochaClient);
            conn = mochaDatasource.getConnection();
            String query = "UPDATE  profiles SET statusCode=? WHERE profileID=? limit ?";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, updateToCode);

            preparedStatement.setInt(2, profileID);
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();
            logger.info("customer successfully transferred to wallet : " + profileID);
            preparedStatement.close();
            //conn.close();

        } catch (Exception e) {
            logger.error("error while updating transaction : " + mochaClient
                    + e.getLocalizedMessage());
            e.printStackTrace();

        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    // logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }
    }

    private void updateAccountStatus(int accID, int updateToCode) {
        Connection conn = null;

        try {
            logger.info("updating account : "
                    + mochaClient);
            conn = mochaDatasource.getConnection();
            String query = "UPDATE  accounts SET statusCode=? WHERE accID=? limit ?";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, updateToCode);

            preparedStatement.setInt(2, accID);
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();
            logger.info("update success  : " + accID);
            preparedStatement.close();
            //conn.close();

        } catch (Exception e) {
            logger.error("error while updating transaction : " + mochaClient
                    + e.getLocalizedMessage());
            e.printStackTrace();

        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    // logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }
    }

    private void updateEnrolmentStatus(int enrolmentID, int updateToCode) {
        Connection conn = null;

        try {
            logger.info("updating  : "
                    + mochaClient);
            conn = mochaDatasource.getConnection();
            String query = "UPDATE  enrolments SET statusCode=? WHERE enrolmentID=? limit ?";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, updateToCode);

            preparedStatement.setInt(2, enrolmentID);
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();
            logger.info("update success : " + enrolmentID);
            preparedStatement.close();
            //conn.close();

        } catch (Exception e) {
            logger.error("error while updating transaction : " + mochaClient
                    + e.getLocalizedMessage());
            e.printStackTrace();

        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    // logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }
    }

    private void updateNominationStatus(int nomninationID, int updateToCode) {
        Connection conn = null;

        try {
            logger.info("updating  : "
                    + mochaClient);
            conn = mochaDatasource.getConnection();
            String query = "UPDATE  nominatedAccounts SET statusCode=? WHERE nominatedaccountID=? limit ?";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, updateToCode);

            preparedStatement.setInt(2, nomninationID);
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();
            logger.info("update success : " + nomninationID);
            preparedStatement.close();
            //conn.close();

        } catch (Exception e) {
            logger.error("error while updating transaction : " + mochaClient
                    + e.getLocalizedMessage());
            e.printStackTrace();

        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    // logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Runs the task.
     */
    public void run() {
        this.processRequest();
    }

}
