package com.cellulant.profiler.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Props {

	

	
		Logger log=Logger.getLogger(getClass());
		/**
	     * A list of any errors that occurred while loading the properties.
	     */
	    private transient List<String> loadErrors;
	    
	    /**
	     * Database user name.
	     */
	    private String nwcMochaUserName;
	    /**
	     * Database password.
	     */
	    private String nwcMochaPassword;
	    /**
	     * Database host.
	     */
	    private String nwcMochaHost;
	    /**
	     * Database port.
	     */
	    private String nwcMochaPort;
	    /**
	     * Database name.
	     */
	    private String nwcMochaDBName;
	    private String nwcMochaDriver;
	    private String nwcmochaURL;
	 
	  private String  nwc_mocha_validate;
	 private String  oraclevalidate;
	   
	    
	    /**
	     * No of threads that will be created in the thread pool to process
	     * payments.
	     */
	    private transient int numOfChildren;
	    
	    /**
	     * Sleep time.
	     */
	    private transient int sleepTime;
	    /**
	     * The properties file.
	     */
	    //private static String fileseparator=System.getProperty("file.separator");
	    //private static final String PROPS_FILE = "/apps/daemons/NwcMobilePaymentsApps/NwcBeepIntergrator/conf/appprop.properties";
	    
	   // private static final String PROPS_FILE = "/home/patoh/workspace/DTBProfiler/conf/appprop.properties";
	    private static final String PROPS_FILE = "/home/george/Downloads/Mocha_Wallet_Profiler/conf/appprop.properties";

	    /**
	     * Maximum number of times to retry executing the failed text Query.
	     */
	    private int maxFailedQueryRetries;
	    /**
	     * Size of the messages to be fetched at one go.
	     */
	    private int bucketSize;
	   
	    /**
	     * Unprocessed Status.
	     */
	    private transient int unprocessedStatus;
	    /**
	     * Escalated Status.
	     */
	    private transient int escalatedStatus;
	    /**
	     * Success Status.
	     */
	    private transient int processedStatus;
	    private transient String  key;
	    
	    
	    public Props() {
	        loadErrors = new ArrayList<String>(0);
	        try{
	        loadProperties(PROPS_FILE );
	        }
	        catch(Exception e){
	        	e.printStackTrace();
	        }
	    }

	    /**
	     * Load system properties.
	     *
	     * @param propsFile the system properties xml file
	     */
	   
	    private void loadProperties(String propsFile) {
	    	try {
	    	/*InputStream propsStream = getClass()
	    	        .getResourceAsStream("/com/cellulant/nwcbeeb/appprop.properties");;*/
	    		FileInputStream propsStream = new FileInputStream(propsFile);
	        Properties props;

	        System.out.println(new File(".").getCanonicalPath());
	            props = new Properties();
	           
	            props.load(propsStream);

	            String error1 = "ERROR: %s is <= 0 or may not have been set";
	            String error2 = "ERROR: %s may not have been set";
	      
	            

	            String noc = props.getProperty("NumberOfThreads");
	            if (noc.isEmpty()) {
	                getLoadErrors().add(String.format(error1, "NumberOfThreads"));
	            } else {
	                numOfChildren = Integer.parseInt(noc);
	                if (numOfChildren <= 0) {
	                    getLoadErrors().add(String.format(error1, "NumberOfThreads"));
	                }
	            }
	    
	       

	            String sleep = props.getProperty("SleepTime");
	            if (sleep.isEmpty()) {
	                getLoadErrors().add(String.format(error1, "SleepTime"));
	            } else {
	                sleepTime = Integer.parseInt(sleep);
	                if (sleepTime < 0) {
	                    getLoadErrors().add(String.format(error1, "SleepTime"));
	                }
	            }

	            String maxFQretiries =
	                props.getProperty("MaximumFailedQueryRetries");
	            if (!maxFQretiries.isEmpty()) {
	                maxFailedQueryRetries = Integer.parseInt(maxFQretiries);
	                if (maxFailedQueryRetries <= 0) {
	                    getLoadErrors().add(String.format(error1, "MaximumFailedQueryRetries"));
	                }
	            } else {
	                getLoadErrors().add(String.format(error1, "MaximumFailedQueryRetries"));
	            }

	            String bucket = props.getProperty("BucketSize");
	            if (!bucket.isEmpty()) {
	                bucketSize = Integer.parseInt(bucket);
	                if (bucketSize <= 0) {
	                    getLoadErrors().add(String.format(error1, "BucketSize"));
	                }
	            } else {
	                getLoadErrors().add(String.format(error1, "BucketSize"));
	            }
	          
	           
	   
	            nwcMochaUserName = props.getProperty("nwcmochausername");
	            if (getNwcMochaUserName().isEmpty()) {
	                getLoadErrors().add(String.format(error2, "nwcmochausername"));
	            }

	            nwcMochaPassword = props.getProperty("nwcmochapassword");
	            if (getNwcMochaPassword().isEmpty()) {
	                getLoadErrors().add(String.format(error2, "nwcmochapassword"));
	            }

	            nwcMochaHost = props.getProperty("nwcmochahost");
	            if (getNwcMochaHost().isEmpty()) {
	                getLoadErrors().add(String.format(error2, "nwcmochapassword"));
	            }
	          
	             nwc_mocha_validate = props.getProperty("nwc_mocha_validate");
	            if (getNwc_mocha_validate().isEmpty()) {
	                getLoadErrors().add(String.format(error2, "nwc_mocha_validate"));
	            }
	            oraclevalidate = props.getProperty("oraclevalidate");
	            if (getOraclevalidate().isEmpty()) {
	                getLoadErrors().add(String.format(error2, "oraclevalidate"));
	            }
	            nwcMochaPort = props.getProperty("nwcmochaport");
	            if (getNwcMochaPort().isEmpty()) {
	                getLoadErrors().add(String.format(error2, "nwcmochaport"));
	            }

	            nwcMochaDBName = props.getProperty("nwcmochadbname");
	            if (getNwcMochaDBName().isEmpty()) {
	                getLoadErrors().add(String.format(error2, "nwcmochadbname"));
	            }
	            nwcMochaDriver = props.getProperty("nwcmochadriver");
	            if (getNwcMochaDBName().isEmpty()) {
	                getLoadErrors().add(String.format(error2, "nwcmochadbname"));
	            }
	            nwcmochaURL = props.getProperty("nwcmochaURL");
	            if (getNwcMochaDBName().isEmpty()) {
	                getLoadErrors().add(String.format(error2, "nwcmochaURL"));
	            }
	            key=props.getProperty("key.value");
	            if (getKey().isEmpty()) {
	                getLoadErrors().add(String.format(error2, "key.value"));
	            }
	                     

	            String unprocessed = props.getProperty("UnprocessedStatus");
	            if (unprocessed.isEmpty()) {
	                getLoadErrors().add(String.format(error1, "UnprocessedStatus"));
	            } else {
	                unprocessedStatus = Integer.parseInt(unprocessed);
	                if (unprocessedStatus < 0) {
	                    getLoadErrors().add(String.format(error1, "UnprocessedStatus"));
	                }
	            }

	            String processed = props.getProperty("ProcessedStatus");
	            if (processed.isEmpty()) {
	                getLoadErrors().add(String.format(error1, "ProcessedStatus"));
	            } else {
	                processedStatus = Integer.parseInt(processed);
	                if (processedStatus < 0) {
	                    getLoadErrors().add(String.format(error1, "ProcessedStatus"));
	                }
	            }

	            String escalated = props.getProperty("EscalatedStatus");
	            if (escalated.isEmpty()) {
	                getLoadErrors().add(String.format(error1, "EscalatedStatus"));
	            } else {
	                escalatedStatus = Integer.parseInt(escalated);
	                if (escalatedStatus < 0) {
	                    getLoadErrors().add(String.format(error1, "EscalatedStatus"));
	                }
	            }

	            propsStream.close();
	        } catch (NumberFormatException ne) {
	            log.error("Exiting. String value found, Integer is "
	                + "required: " + ne.getMessage());
	           // System.exit(1);
	        } catch (FileNotFoundException ne) {
	           log.error("Exiting. Could not find the properties file: "
	                + ne.getMessage());
	           ne.printStackTrace();
	           // System.exit(1);
	        } catch (IOException ioe) {
	            log.error("Exiting. Failed to load system properties: "
	                + ioe.getMessage());
	            ioe.printStackTrace();
	            //System.exit(1);
	        } 
	    }

	    


	    /**
	     * No of threads that will be created in the thread pool to process
	     * payments.
	     *
	     * @return the numOfChildren
	     */
	    public int getNumOfChildren() {
	        return numOfChildren;
	    }


	    /**
	     * Gets the sleep time.
	     *
	     * @return the sleep time
	     */
	    public int getSleepTime() {
	        return sleepTime;
	    }



	    public static String getPROPS_FILE() {
	        return PROPS_FILE;
	    }

	    public int getMaxFailedQueryRetries() {
	        return maxFailedQueryRetries;
	    }

	    public int getBucketSize() {
	        return bucketSize;
	    }

	    public int getUnprocessedStatus() {
	        return unprocessedStatus;
	    }

	    public int getEscalatedStatus() {
	        return escalatedStatus;
	    }

	    public int getProcessedStatus() {
	        return processedStatus;
	    }

	 



	   public String getNwcMochaUserName() {
			return nwcMochaUserName;
		}

		public String getNwcMochaPassword() {
			return nwcMochaPassword;
		}

		public String getNwcMochaHost() {
			return nwcMochaHost;
		}

		public String getNwcMochaPort() {
			return nwcMochaPort;
		}

		public String getNwcMochaDBName() {
			return nwcMochaDBName;
		}

		public String getNwcMochaDriver() {
			return nwcMochaDriver;
		}

		

		public static String getPropsFile() {
			return PROPS_FILE;
		}

	public Logger getLog() {
			return log;
		}

		public void setNwcMochaUserName(String nwcMochaUserName) {
			this.nwcMochaUserName = nwcMochaUserName;
		}

		public void setNwcMochaPassword(String nwcMochaPassword) {
			this.nwcMochaPassword = nwcMochaPassword;
		}

		public void setNwcMochaHost(String nwcMochaHost) {
			this.nwcMochaHost = nwcMochaHost;
		}

		public void setNwcMochaPort(String nwcMochaPort) {
			this.nwcMochaPort = nwcMochaPort;
		}

		public void setNwcMochaDBName(String nwcMochaDBName) {
			this.nwcMochaDBName = nwcMochaDBName;
		}

		public void setNwcMochaDriver(String nwcMochaDriver) {
			this.nwcMochaDriver = nwcMochaDriver;
		}

		

		public void setSleepTime(int sleepTime) {
			this.sleepTime = sleepTime;
		}

		public void setUnprocessedStatus(int unprocessedStatus) {
			this.unprocessedStatus = unprocessedStatus;
		}

		public String getNwcmochaURL() {
			return nwcmochaURL;
		}

		public void setNwcmochaURL(String nwcmochaURL) {
			this.nwcmochaURL = nwcmochaURL;
		}

		public String getNwc_mocha_validate() {
			return nwc_mocha_validate;
		}

		public void setNwc_mocha_validate(String nwc_mocha_validate) {
			this.nwc_mocha_validate = nwc_mocha_validate;
		}

		public String getOraclevalidate() {
			return oraclevalidate;
		}

		public void setOraclevalidate(String oraclevalidate) {
			this.oraclevalidate = oraclevalidate;
		}

		public void setProcessedStatus(int processedStatus) {
			this.processedStatus = processedStatus;
		}

	public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

	/**
	     * A list of any errors that occurred while loading the properties.
	     *
	     * @return the loadErrors
	     */
	    public List<String> getLoadErrors() {
	        return Collections.unmodifiableList(loadErrors);
	    }
	}


