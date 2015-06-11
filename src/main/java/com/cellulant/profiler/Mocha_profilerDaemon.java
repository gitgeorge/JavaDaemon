package com.cellulant.profiler;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;

import com.cellulant.profiler.persistence.DatabaseConnection;
import com.cellulant.profiler.utils.Props;
//import com.cellulant.profiler.utils.PropsReg;

public class Mocha_profilerDaemon implements Daemon, Runnable {

    /**
     *
     */
    private Logger logger = Logger.getLogger(getClass());
    /**
     * Logger for this application. Thread initialization
     */
    private transient Thread worker;
    private transient boolean working = false;

    /**
     * Loads system properties.
     */
    private static Props props;

    private ProfileTransaction profileTransaction;
    DataSource dtbMochaSource;

    public static void main(String args[]) throws Exception {
        props = new Props();

        //logger.info(" Initializing Payment Status poller daemon...");
        String nwcURL = props.getNwcmochaURL();

        Thread worker = new Thread(new Mocha_profilerDaemon());
        DataSource dtbMochaSource = new DatabaseConnection().setUp(
                props.getNwcMochaUserName(),
                props.getNwcMochaPassword(),
                props.getNwcMochaDriver(),
                nwcURL,
                "700",
                "500", props, props.getNwc_mocha_validate());

        ProfileTransaction profileTransaction = new ProfileTransaction(dtbMochaSource, props);
        boolean working = true;
        while (true) {
            profileTransaction.fetchAndProcess();
            //worker.start();
            Thread.sleep(2000);
        }
		//logger.info("Starting Payment status poller daemon...");

    }

    public void destroy() {
        logger.info("Destroying Payment status poller daemon...");
        logger.info("Exiting...");

    }

    public void init(DaemonContext arg0) throws DaemonInitException, Exception {
        try {

            props = new Props();

            logger.info(" Initializing Payment Status poller daemon...");
            String nwcURL = props.getNwcmochaURL();

            worker = new Thread(this);
            DataSource dtbMochaSource = new DatabaseConnection().setUp(
                    props.getNwcMochaUserName(),
                    props.getNwcMochaPassword(),
                    props.getNwcMochaDriver(),
                    nwcURL,
                    "700",
                    "500", props, props.getNwc_mocha_validate());

            profileTransaction = new ProfileTransaction(dtbMochaSource,
                    props);
        } catch (SQLException ex) {
            logger.fatal("Exception caught during initialization: "
                    + ex.getLocalizedMessage());
            logger.info("Exiting...");
            System.exit(1);
        } catch (Exception e) {
            logger.fatal(e.getLocalizedMessage());
            //e.printStackTrace();
            System.exit(1);
            logger.info("Exiting...");
        }

    }

    public void stop() throws Exception {
        logger.info("Stopping Payment status poller daemon...");

        working = false;
        while (!profileTransaction.getIsCurrentPoolShutDown()) {
            logger.info("Waiting for current thread pool to complete tasks...");
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                logger.error("InterruptedException occured while waiting for tasks to complete: "
                        + ex.getMessage());
            }
        }

        logger.info("Completed tasks in current thread pool, continuing daemon "
                + "shutdown");

        logger.info("Payment status poller Daemon successfully stopped.");

    }

    /**
     * Starts the daemon.
     */
    public void start() {
        working = true;
        worker.start();
        logger.info("Starting Payment status poller daemon...");
    }

    public void run() {
        while (working) {

            try {
                profileTransaction.fetchAndProcess();
            } catch (Exception ex) {
                logger.error("Error occured: " + ex.getLocalizedMessage());
            }

            try {
                Thread.sleep(props.getSleepTime());
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage());
            }
        }
    }
}
