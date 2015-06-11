package com.cellulant.profiler;

/*
 * To change this template, choose Tools | Templates
 * AND open the template in the editor.
 */


import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.cellulant.profiler.utils.Constants;
import com.cellulant.profiler.utils.PropsReg;

public class Register {

   // private Logging logger;
    private PropsReg props;
    private Logger logger=Logger.getLogger(getClass());

    /**
     * Constructor
     *
     * @param logger - Logging instance
     * @param props - Props instance
     */
    public Register() {
        //this.logger = logger;
       // this.props = props;
    }

    /**
     * This method invokes the Registration API
     *
     * @param processingCode - ISO Processing Code
     * @param requestMap - Client Details HashMap
     *
     * @return ans - ISO Status Code
     */
    public String postRequest(final String processingCode,final HashMap<String, String> requestMap) {
    	props = new PropsReg();
        String ans = props.getGeException();
        String result;

        String URL =null;// requestMap.get("URL");
        String username = props.getRegUser();
        String password = props.getRegPassword();
        String nodeSystemID = props.getNodeSystemID();
        String tarrifID = props.getTarrifID();
        
        logger.info("username and password " + username +"\t"+ password);

        HttpClient client;
        PostMethod post = null;

        try {
            //log sms
            
            // Create an instance of HttpClient.
            client = new HttpClient();
            if (processingCode.equals("960000")) {
            	URL = props.getRegURL();
                logger.info("The url is \t" + URL);
            	logger.info(Constants.APPLICATION_TITLE
                        + "=>postRequest<=URL=>" + URL);
            post = new PostMethod(URL);
            }
            
            if(processingCode.equals("961000")){
            	URL = props.getAddAcURL();
            	logger.info(Constants.APPLICATION_TITLE
                        + "=>postRequest<=URL=>" + URL);
            	 post = new PostMethod(URL);
            }
            if(processingCode.equals("967000")){
            	URL = props.getAddEnrollmentURL();
            	logger.info(Constants.APPLICATION_TITLE
                        + "=>postRequest<=URL=>" + URL);
            	 post = new PostMethod(URL);
            }
            if(processingCode.equals("966000")){
            	URL = props.getAddNominationURL();
            	logger.info(Constants.APPLICATION_TITLE
                        + "=>postRequest<=URL=>" + URL);
            	 post = new PostMethod(URL);
            }
            
            
            client.getParams().setParameter("http.useragent",
                    "T24RegistrationIntegrator");
            // Add post Parameters
            post.addParameter("USERNAME", username);
            post.addParameter("PASSWORD", password);
            post.addParameter("MSISDN", requestMap.get("MSISDN").toString());

            if (processingCode.equals("960000")) {
                // Add a new customer
                post.addParameter("SURNAME",requestMap.get("firstName") );
                post.addParameter("OTHER_NAME",requestMap.get("otherNames"));
                post.addParameter("SALUTATION", "NULL");
                post.addParameter("ID_NUMBER", requestMap.get("ID"));
                post.addParameter("ID_TYPE", "1");
                post.addParameter("GENDER", "NULL");
                post.addParameter("LANGUAGE_ID", "1");
                post.addParameter("CUSTOMER_REF_NO", requestMap.get("ID"));
                post.addParameter("BANK_BRANCH_ID", requestMap.get("branchID"));
                post.addParameter("POST_OFFICE_ADDRESS", "NULL");
                post.addParameter("NATIONALITY", "NULL");
                post.addParameter("TARIFF",requestMap.get("tariff"));
                post.addParameter("NODE_SYSTEM_ID", "1");
                post.addParameter("ACCOUNT_ALIAS",
                        requestMap.get("accountAlias"));
                post.addParameter("IS_DEFAULT", requestMap.get("isDefault"));
                post.addParameter("ACCOUNT_NUMBER",
                        requestMap.get("accountNumber"));
                post.addParameter("CURRENCY_ID", "KES");
                post.addParameter("CURRENCY_SOURCE", "ISO");
                post.addParameter("RETURN_PIN", "1");
                post.addParameter("SMS_PIN", "1");
                post.addParameter("PIN_TYPE", "OTP");
                post.addParameter("SKIP_PIN_HASH", "0");
                post.addParameter("EMAIL", requestMap.get("email"));
                post.addParameter("NARRATION", "migrated user via API");
            } else if (processingCode.equals("961000")) {
                // Add account
                post.addParameter("ACCOUNTS_DELIMITER", "*");
                post.addParameter("ACCOUNT_NUMBER",requestMap.get("accountNo"));
                post.addParameter("ALIAS",requestMap.get("accountAlias"));
                post.addParameter("IS_DEFAULT", requestMap.get("isDefault"));
                post.addParameter("CURRENCY_ID",requestMap.get("currencyID"));
                post.addParameter("BANK_BRANCH_ID", requestMap.get("branchID"));
                post.addParameter("BANK_BRANCH_CODE", requestMap.get("branchCode"));
                post.addParameter("NODE_SYSTEM_ID", nodeSystemID);
                post.addParameter("TARIFF_ID", requestMap.get("tariff"));
                post.addParameter("CURRENCY_SOURCE", "ISO");
            } else if (processingCode.equals("962000")) {
                // Remove an Account
                post.addParameter("ACCOUNTS_DELIMITER", "*");
                post.addParameter("ACCOUNT_NUMBER",
                        requestMap.get("accountNo"));
            } else if (processingCode.equals("963000")) {
                // Profile Deactivation
                post.addParameter("REASON", requestMap.get("reason"));
            } else if (processingCode.equals("964000")) {
                // Add MSISDN
                post.addParameter("CUSTOMER_ID_NUMBER", requestMap.get("ID"));
            } else if (processingCode.equals("966000")) {
                // Add Nomination
            	            	
               // post.addParameter("PROFILE_ID", requestMap.get("profileID"));
                post.addParameter("ACCOUNT_NUMBER",requestMap.get("accountNo"));
                post.addParameter("ACCOUNTS_DELIMITER", "*");
                post.addParameter("NOMINATION_ALIAS",requestMap.get("accountAlias"));
                post.addParameter("NOMINATION_TYPE_CODE","IFT");         
                post.addParameter("BANK_BRANCH_CODE",requestMap.get("branchCode"));
                
            } else if (processingCode.equals("967000")) {
                // Add Enrollment
                //post.addParameter("PROFILE_ID", requestMap.get("profileID"));
                post.addParameter("MERCHANT_CODE", requestMap.get("merchantID"));
                post.addParameter("REF_NUMBER",requestMap.get("refNumber"));
                //post.addParameter("ACCOUNT_NUMBER",requestMap.get("accountNumber"));
                post.addParameter("ENROLLMENT_ALIAS",requestMap.get("accountAlias"));
            }

            post.addParameter("RESPONSE_TYPE", "HASH_DELIMITED");
            
            client.executeMethod(post);
            result = post.getResponseBodyAsString();
            logger.info("The result is..." + result);
            logger.info(Constants.APPLICATION_TITLE
                    + "=>postRequest<=API Results=>" + result);

            String APIResults[] = result.split("#");

            if (APIResults.length > 0) {
                String status = APIResults[0].toLowerCase().trim();
                if (status.equals("true")) {
                    ans = props.getSuccessCode();
                }
            }
            post.releaseConnection();
        } catch (IllegalArgumentException ex) {
            logger.info(Constants.APPLICATION_TITLE + "=>postRequest<="
                    + "HTTP GET-IllegalArgumentException " + ex.getMessage()
                    + "=>URL=>" + URL);
        } catch (HttpException ex) {
            logger.info(Constants.APPLICATION_TITLE + "=>postRequest<="
                    + "HTTP GET-HttpException " + ex.getMessage()
                    + "=>URL=>" + URL);
        } catch (IOException ex) {
            logger.info(Constants.APPLICATION_TITLE + "=>postRequest<="
                    + "HTTP GET-IOException " + ex.getMessage()
                    + "=>URL=>" + URL);
        } finally {
            if (post != null) {
                post.releaseConnection();
            }
        }
        return ans;
    }
}