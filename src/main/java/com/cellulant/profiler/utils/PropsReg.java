package com.cellulant.profiler.utils;

/**
 * Loads system properties from a file.
 *
 * @author Brian Ngure
 * @co-author Maina Kabui
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("FinalClass")
public final class PropsReg {

    private int bucketSize;
    /**
     * Info log level. Default = INFO.
     */
    private String infoLogLevel = "INFO";
    /**
     * Error log level. Default = FATAL.
     */
    private String errorLogLevel = "FATAL";
    /**
     * Info log file name.
     */
    private String infoLogFile;
    /**
     * Error log file name.
     */
    private String errorLogFile;
    /**
     * Profile existing exception.
     */
    private String PeException;
    /**
     * Account existing exception.
     */
    private String AeException;
    /**
     * General Exception.
     */
    private String GeException;
    /**
     * Successfully processed status
     */
    private String SuccessCode;
    /**
     * Clients Registration URL.
     */
    private String RegURL;
    /**
     * Clients Deletion URL.
     */
    private String DelURL;
    /**
     * Account Deletion URL.
     */
    private String DelAcURL;
    /**
     * Account Addition URL.
     */
    private String AddAcURL;
    /**
     * MSISDN Addition URL.
     */
    private String addMSISDNURL;
    /**
     * MSISDN Removal URL.
     */
    private String removeMSISDNURL;
    /**
     * Add Nomination URL.
     */
    private String addNominationURL;
    /**
     * Add Beneficiary URL.
     */
    private String addEnrollmentURL;
    /**
     * Clients Registration Username.
     */
    private String RegUser;
    /**
     * Clients Registration Password.
     */
    private String RegPassword;
    /**
     * NodeSystemID.
     */
    private String NodeSystemID;
    /**
     * The TarrifID.
     */
    private String TarrifID;
    /**
     * ISO port.
     */
    private int isoPort;
    /**
     * min number of threads on jpos threadPool.
     */
    private int poolSize;
    /**
     * max number of threads on jpos threadPool.
     */
    private int maxPoolSize;
    /**
     * Thread Sleep time in seconds.
     */
    private int sleepTime;
    /**
     * The Error List
     */
    private List<String> loadErrors;
    /**
     * The properties file.
     */
    // public static final String PROPS_FILE ="/home/patoh/workspace/DTBProfiler/conf/properties.xml";
    public static final String PROPS_FILE = "/home/george/Downloads/Mocha_Wallet_Profiler/conf/properties.xml";

    /**
     * Constructor.
     */
    public PropsReg() {
        loadErrors = new ArrayList<String>(0);
        loadProperties(PROPS_FILE);

    }

    /**
     * Load system properties.
     *
     * @param propsFile the system properties xml file
     */
    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private void loadProperties(final String propsFile) {
        FileInputStream propsStream = null;
        Properties props;

        try {
            System.out.println(new File(".").getCanonicalPath());
            props = new Properties();
            propsStream = new FileInputStream(propsFile);
            props.loadFromXML(propsStream);

            String error1 = "ERROR: %s is <= 0 or may not have been set";
            String error2 = "ERROR: %s may not have been set";

            infoLogFile = props.getProperty("INFO_LOG_FILE");
            if (getInfoLogFile().isEmpty()) {
                getLoadErrors().add(String.format(error2, "INFO_LOG_FILE"));
            }

            errorLogFile = props.getProperty("ERROR_LOG_FILE");
            if (getErrorLogFile().isEmpty()) {
                getLoadErrors().add(String.format(error2, "ERROR_LOG_FILE"));
            }

            isoPort = Integer.parseInt(props.getProperty("ISO_PORT_NO"));
            if (getISOPort() < 0) {
                getLoadErrors().add(String.format(error1, "ISO_PORT_NO"));
            }

            GeException = props.getProperty("GE_EXCEPTION");
            if (getGeException().isEmpty()) {
                getLoadErrors().add(String.format(error2, "GE_EXCEPTION"));
            }

            RegURL = props.getProperty("REG_URL");
            if (getRegURL().isEmpty()) {
                getLoadErrors().add(String.format(error2, "REG_URL"));
            }

            AddAcURL = props.getProperty("ACADD_URL");
            if (getRegURL().isEmpty()) {
                getLoadErrors().add(String.format(error2, "ACADD_URL"));
            }

            DelAcURL = props.getProperty("ACDEL_URL");
            if (getRegURL().isEmpty()) {
                getLoadErrors().add(String.format(error2, "ACDEL_URL"));
            }

            DelURL = props.getProperty("DEL_URL");
            if (getRegURL().isEmpty()) {
                getLoadErrors().add(String.format(error2, "DEL_URL"));
            }

            addMSISDNURL = props.getProperty("ADD_MSISDN_URL");
            if (getAddMSISDNURL().isEmpty()) {
                getLoadErrors().add(String.format(error2, "ADD_MSISDN_URL"));
            }

            removeMSISDNURL = props.getProperty("REMOVE_MSISDN_URL");
            if (getRemoveMSISDNURL().isEmpty()) {
                getLoadErrors().add(String.format(error2, "REMOVE_MSISDN_URL"));
            }

            addNominationURL = props.getProperty("ADD_NOMINATON_URL");
            if (getAddNominationURL().isEmpty()) {
                getLoadErrors().add(String.format(error2, "ADD_NOMINATON_URL"));
            }

            addEnrollmentURL = props.getProperty("ADD_BENEFICIARY_URL");
            if (getAddEnrollmentURL().isEmpty()) {
                getLoadErrors().add(String.format(error2, "ADD_BENEFICIARY_URL"));
            }

            RegUser = props.getProperty("REG_USER");
            if (getRegUser().isEmpty()) {
                getLoadErrors().add(String.format(error2, "REG_USER"));
            }

            RegPassword = props.getProperty("REG_PASSWORD");
            if (getRegPassword().isEmpty()) {
                getLoadErrors().add(String.format(error2, "REG_PASSWORD"));
            }

            NodeSystemID = props.getProperty("NODE_SYSTEM");
            if (getNodeSystemID().isEmpty()) {
                getLoadErrors().add(String.format(error2, "NODE_SYSTEM"));
            }

            TarrifID = props.getProperty("TARRIF_ID");
            if (getTarrifID().isEmpty()) {
                getLoadErrors().add(String.format(error2, "TARRIF_ID"));
            }

            SuccessCode = props.getProperty("SUCCESS_CODE");
            if (getSuccessCode().isEmpty()) {
                getLoadErrors().add(String.format(error2, "SUCCESS_CODE"));
            }

            poolSize = Integer.parseInt(props.getProperty("POOL_SIZE"));
            if (getpoolSize() < 0) {
                getLoadErrors().add(String.format(error1, "POOL_SIZE"));
            }

            maxPoolSize = Integer.parseInt(props.getProperty("MAX_POOL_SIZE"));
            if (getmaxPoolSize() < 0) {
                getLoadErrors().add(String.format(error1, "MAX_POOL_SIZE"));
            }

            sleepTime = Integer.parseInt(props.getProperty("SLEEP_TIME"));
            if (getSleepTime() < 0) {
                getLoadErrors().add(String.format(error1, "SLEEP_TIME"));
            }

            propsStream.close();
        } catch (NumberFormatException ne) {
            System.err.println("Exiting. String value found, Integer is "
                    + "required: " + ne.getMessage());

            try {
                propsStream.close();
            } catch (IOException ex) {
                System.err.println("Failed to close the properties file: "
                        + ex.getMessage());
            }

            System.exit(1);
        } catch (FileNotFoundException ne) {
            System.err.println("Exiting. Could not find the properties file: "
                    + ne.getMessage());

            try {
                propsStream.close();
            } catch (IOException ex) {
                System.err.println("Failed to close the properties file: "
                        + ex.getMessage());
            }

            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("Exiting. Failed to load system properties: "
                    + ioe.getMessage());

            try {
                propsStream.close();
            } catch (IOException ex) {
                System.err.println("Failed to close the properties file");
            }

            System.exit(1);
        }
    }

    /**
     * Bucket size. This is the maximum size of records processed in a single
     * run.
     *
     * @return the bucketSize
     */
    public int getBucketSize() {
        return bucketSize;
    }

    /**
     * Info log level. Default = INFO.
     *
     * @return the infoLogLevel
     */
    public String getInfoLogLevel() {
        return infoLogLevel;
    }

    /**
     * Error log level. Default = FATAL.
     *
     * @return the errorLogLevel
     */
    public String getErrorLogLevel() {
        return errorLogLevel;
    }

    /**
     * Info log file name.
     *
     * @return the infoLogFile
     */
    public String getInfoLogFile() {
        return infoLogFile;
    }

    /**
     * Error log file name.
     *
     * @return the errorLogFile
     */
    public String getErrorLogFile() {
        return errorLogFile;
    }

    /**
     * Contains the name of the database port.
     *
     * @return the name of the database port
     */
    public int getISOPort() {
        return isoPort;
    }

    /**
     * poolSize. This is the minimum number of threads on jpos
     *
     * @return the poolSize
     */
    public int getpoolSize() {
        return poolSize;
    }

    /**
     * maxpoolSize. This is the maximum number of threads on jpos
     *
     * @return the poolSize
     */
    public int getmaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Sleep time in seconds.
     *
     * @return the sleepTime
     */
    public int getSleepTime() {
        return sleepTime;
    }

    /**
     * Profile exists
     *
     * @return the PeException
     */
    public String getPeException() {
        return PeException;
    }

    /**
     * Account exists exception.
     *
     * @return the AeException
     */
    public String getAeException() {
        return AeException;
    }

    /**
     * General Exception
     *
     * @return the GeException
     */
    public String getGeException() {
        return GeException;
    }

    /**
     * Sleep time in seconds.
     *
     * @return the sleepTime
     */
    public String getSuccessCode() {
        return SuccessCode;
    }

    /**
     * The Clients Registration API URL
     *
     * @return the RegURL
     */
    public String getRegURL() {
        return RegURL;
    }

    /**
     * The Accounts Addition URL
     *
     * @return the AddAcURL
     */
    public String getAddAcURL() {
        return AddAcURL;
    }

    /**
     * The Accounts Removal URL
     *
     * @return the DelAcURL
     */
    public String getDelAcURL() {
        return DelAcURL;
    }

    /**
     * The Clients Deletion URL
     *
     * @return the getDelURL
     */
    public String getDelURL() {
        return DelURL;
    }

    /**
     * The MSISDN Addition URL
     *
     * @return the getAddMSISDNURL
     */
    public String getAddMSISDNURL() {
        return addMSISDNURL;
    }

    /**
     * The MSISDN Removal URL
     *
     * @return the getRemoveMSISDNURL
     */
    public String getRemoveMSISDNURL() {
        return removeMSISDNURL;
    }

    /**
     * The Nomination Addition URL
     *
     * @return the getAddNominationURL
     */
    public String getAddNominationURL() {
        return addNominationURL;
    }

    /**
     * The Beneficiary Addition URL
     *
     * @return the getAddEnrollmentURL
     */
    public String getAddEnrollmentURL() {
        return addEnrollmentURL;
    }

    /**
     * The Clients Registration Username
     *
     * @return the RegUser
     */
    public String getRegUser() {
        return RegUser;
    }

    /**
     * The Clients Registration Password
     *
     * @return the RegPassword
     */
    public String getRegPassword() {
        return RegPassword;
    }

    /**
     * The NodeSystem
     *
     * @return the NodeSystemID
     */
    public String getNodeSystemID() {
        return NodeSystemID;
    }

    /**
     * The TarrifID
     *
     * @return the TarrifID
     */
    public String getTarrifID() {
        return TarrifID;
    }

    /**
     * A list of any errors that occurred while loading the properties.
     *
     * @return the loadErrors
     */
    public List<String> getLoadErrors() {
        return loadErrors;
    }
}
