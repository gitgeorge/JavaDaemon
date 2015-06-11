package com.cellulant.profiler.persistence;
import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import com.cellulant.profiler.utils.Props;


public class DatabaseConnection {
	
	Logger logger=Logger.getLogger(getClass());
	private GenericObjectPool connectionPool = null;
	
	 
    public DataSource setUp(String username,String password,String driver,String url,String maxConnections,String minconnection,Props props,String validateQuery) throws Exception {
       //String pass=new CipherUtils(props).decrypt(password)=================;
    	String pass="root";
    	//
        // Load JDBC Driver class.
        //
        Class.forName(driver).newInstance();
 
        //
        // Creates an instance of GenericObjectPool that holds our
        // pool of connections object.
        //
        connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(Integer.parseInt(minconnection));
        connectionPool.setTestOnBorrow(true);
        connectionPool.setTestOnReturn(true);
       
       // connectionPool.
        //
        // Creates a connection factory object which will be use by
        // the pool to create the connection object. We passes the
        // JDBC url info, username and password.
        //
       // String pass="r000t";
        ConnectionFactory cf = new DriverManagerConnectionFactory(url,username,pass);
 
        //
        // Creates a PoolableConnectionFactory that will wraps the
        // connection object created by the ConnectionFactory to add
        // object pooling functionality.
        //
        PoolableConnectionFactory pcf =
                new PoolableConnectionFactory(cf, connectionPool,
                        null, validateQuery, false, true);
        
        return new PoolingDataSource(pcf.getPool());
    }
 
    public GenericObjectPool getConnectionPool() {
        return connectionPool;
    }
	
  	
}
