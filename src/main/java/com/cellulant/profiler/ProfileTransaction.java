package com.cellulant.profiler;

import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.cellulant.profiler.orm.MochClient;
import com.cellulant.profiler.utils.DaemonStatus;
import com.cellulant.profiler.utils.Props;

public class ProfileTransaction {

    /**
     * System properties class instance.
     */
    private static transient Props props;

    /**
     * Flag to check if current pool is completed.
     */
    private transient boolean isCurrentPoolShutDown = false;

    private transient int daemonState;

    private final int UNPROCESSEDTRANSACTIONSTATE = 0;

    private Logger logger = Logger.getLogger(getClass());
    private DataSource dtbMochaSource;

    public ProfileTransaction(DataSource dtbMochaSource, Props prop) {
        this.daemonState = DaemonStatus.DAEMON_RUNNING;
        this.dtbMochaSource = dtbMochaSource;

        props = prop;
        // Get the list of errors found when loading system properties
        List<String> loadErrors = prop.getLoadErrors();
        int sz = loadErrors.size();

        if (sz > 0) {
            logger.info("There were exactly "
                    + sz + " error(s) during the load operation...");

            for (String err : loadErrors) {
                logger.fatal(err);
            }

            logger.info("Unable to start daemon "
                    + "because " + sz + " error(s) occured during load.");
            System.exit(1);
        } else {
            logger.info("All required properties were loaded successfully");
            daemonState = DaemonStatus.DAEMON_RUNNING;
        }

    }

    /**
     * The method gets the current list of pending transaction
     *
     * processed
     */
    private synchronized void executeTasks() {
        ExecutorService executor = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        Connection conn = null;

        try {
            //execute failed Query

            logger.info("getPendingTransactions(): Fetching pending Transaction");

            conn = dtbMochaSource.getConnection();

            logger.info(" database connection  good ");

            String query = "select * from  customers where processed=0 and statusCode=0 order by 1 asc limit 1000";
            preparedStatement = conn.prepareStatement(query);
            rs = preparedStatement.executeQuery();

            int size = 0;
            if (rs.last()) {
                size = rs.getRow();
                rs.beforeFirst();
            }
            if (size > 0) {
                logger.info("Fetch records using query");
                isCurrentPoolShutDown = false;

                if (size <= props.getNumOfChildren()) {
                    executor = Executors
                            .newFixedThreadPool(size);
                } else {
                    executor = Executors
                            .newFixedThreadPool(props.getNumOfChildren());
                }

                while (rs.next()) {
                    MochClient mochaClient = new MochClient();
                    mochaClient.setCustID(rs.getInt("custID"));
                    mochaClient.setCustNames(rs.getString("CustNames"));
                    mochaClient.setEmail(rs.getString("email"));
                    mochaClient.setPostAddress(rs.getString("postAddress"));
                    mochaClient.setNationalID(rs.getString("ID_Number"));
                    updateTransaction(mochaClient);
                    Runnable task = createTask(mochaClient);
                    executor.execute(task);

                }

                preparedStatement.close();

                /*
                 * This will make the executor accept no new threads and
                 * finish all existing threads in the queue.
                 */
                shutdownAndAwaitTermination(executor);
                //  shutdownAndAwaitTermination(failedTransactionexecutor);
            } else {
                logger.info("No records were fetched from the DB for processing...");
                preparedStatement.close();
            }

        } catch (Exception e) {
            logger.error("getPendingTransactions(): Error while fetching pending transaction:"
                    + e.getLocalizedMessage());
            e.printStackTrace();

        } finally {
            isCurrentPoolShutDown = true;
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Closing connection errror  " + e.getLocalizedMessage());
                    ;
                }
            }
        }

    }

    private synchronized Runnable createTask(MochClient mochaClient) {
        logger.info("Creating a task for message with RequestLogID: "
                + mochaClient);

        return new ProcessRecord(dtbMochaSource, mochaClient);
    }

    /**
     * Process Records.
     */
    public void fetchAndProcess() {
        int pingState = pingDatabaseServer();

        if (pingState == DaemonStatus.PING_SUCCESS) {
            // The database is available, allocate, fetch and reset the bucket
            if (daemonState == DaemonStatus.DAEMON_RUNNING) {
                doWork();
            } else if (daemonState == DaemonStatus.DAEMON_RESUMING) {

                doWait(props.getSleepTime());

                logger.info("Resuming daemon service...");
                // daemonState =  DaemonStatus.DAEMON_RUNNING;              logger.info("Daemon resumed successfully, working...");
            }
        } else {
            logger.info("Resuming daemon service...");
          //  daemonState =  DaemonStatus.DAEMON_INTERRUPTED;

            // Enter a Suspended state
            while (true) {
                if (daemonState == DaemonStatus.DAEMON_INTERRUPTED) {
                    int istate = pingDatabaseServer();

                    if (istate == DaemonStatus.PING_SUCCESS) {
                        daemonState = DaemonStatus.DAEMON_RESUMING;
                        break;
                    }
                }

                doWait(props.getSleepTime());
            }
        }
    }

    /**
     * A better functional logic that ensures secure execution of fetch bucket
     * as well as detailed management of interrupted queries. This will work
     * only when we have a db connection.
     */
    private void doWork() {
        this.executeTasks();
    }

    /* --Sleep-Time -- */
    public void doWait(long time) {

        try {
            logger.info("Thread sleeping ");
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            logger.info("Thread could not sleep fpr the specified time");

        }
    }

    /**
     * Checks if the database server is up.
     *
     * @return state of database connection
     */
    private int pingDatabaseServer() {
        int state;
        try {
            String host = props.getNwcMochaHost();
            int port = Integer.parseInt(props.getNwcMochaPort());

            Socket ps = new Socket(host, port);

            /*
             * Same time we use to sleep is the same time we use for ping wait
             * period.
             */
            ps.setSoTimeout(props.getSleepTime());
            /*
             * Same time we use to sleep is the same time we use for ping wait
             * period.
             */
            ps.setSoTimeout(props.getSleepTime());

            if (ps.isConnected()) {
                state = DaemonStatus.PING_SUCCESS;
                logger.info("Ping Successful proceeding nwc_mocha successfull");

                ps.close();

            } else {
                state = DaemonStatus.PING_FAILED;
                logger.info("Ping to nwc_mocha database failed.");
            }

        } catch (UnknownHostException se) {
            logger.error("UnknownHostException Exception " + se.getMessage());
            state = DaemonStatus.PING_FAILED;
        } catch (SocketException se) {
            logger.error("Socket Exception: Ping to nwc_mocha database failed."
                    + se.getMessage());
            state = DaemonStatus.PING_FAILED;
        } catch (Exception se) {
            logger.error("IOException Exception:  Ping to nwc_mocha database failed."
                    + se.getMessage());
            state = DaemonStatus.PING_FAILED;
        }

        return state;
    }

    /**
     * The following method shuts down an ExecutorService in two phases, first
     * by calling shutdown to reject incoming tasks, and then calling
     * shutdownNow, if necessary, to cancel any lingering tasks (after 6
     * minutes).
     *
     * @param pool the executor service pool
     */
    private void shutdownAndAwaitTermination(final ExecutorService pool) {

        logger.info("Executor pool waiting for tasks to complete");
        try {

            pool.shutdown();
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(1, TimeUnit.HOURS)) {
                logger.error("Executor pool  terminated with tasks "
                        + "unfinished. Unfinished tasks will be retried.");
              //  pool.shutdownNow(); // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(1, TimeUnit.HOURS)) {
                    logger.error("Executor pool terminated with tasks "
                            + "unfinished. Unfinished tasks will be retried.");
                }

            } else {
                logger.info("Executor pool completed all tasks and has shut "
                        + "down normally");
            }

        } catch (Exception ie) {
            logger.error("Executor pool shutdown error: " + ie.getMessage());
            // (Re-)Cancel if current thread also interrupted

        }

        isCurrentPoolShutDown = true;
    }

    public void updateTransaction(MochClient mochaclient) {
        Connection conn = null;

        try {
            logger.info("updating to processed custID : "
                    + mochaclient.getCustID());
            conn = dtbMochaSource.getConnection();
            String query = "UPDATE  customers SET processed=? WHERE custID=? limit ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, mochaclient.getCustID());
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();
            logger.info("Record updated : " + mochaclient);
            preparedStatement.close();

        } catch (Exception e) {
            logger.error("error while updating customer : " + mochaclient
                    + e.getLocalizedMessage());
            //e.printStackTrace();

        } finally {

            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ex) {
                    //logger.error("Failed to close connection object: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Gets whether the current pool has been shut down.
     *
     * @return whether the current pool has been shut down
     */
    public boolean getIsCurrentPoolShutDown() {
        return isCurrentPoolShutDown;
    }

}
