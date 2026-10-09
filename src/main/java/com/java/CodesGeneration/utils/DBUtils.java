package com.java.CodesGeneration.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("unused")
public class DBUtils {

	private static final Log LOG = LogFactory.getLog(DBUtils.class);
	
	private static String JDBC_DRIVER;
	private static String DB_URL;
	private static String USER;
	private static String PASS;
    
    private static String ATP_JDBC_DRIVER;
    private static String ATP_DB_URL;
    private static String ATP_USER;
    private static String ATP_PASS;
    
    private static String JDBC_DRIVER_UAT;
    private static String DB_URL_UAT;
    private static String USER_UAT;
    private static String PASS_UAT;
    
    private static String OIS_JDBC_DRIVER;
    private static String OIS_DB_URL;
    private static String OIS_USER;
    private static String OIS_PASS;
    
    private static String SEED_JDBC_DRIVER;
    private static String SEED_DB_URL;
    private static String SEED_USER;
    private static String SEED_PASS;
    
    private static String SEED_UAT_JDBC_DRIVER;
    private static String SEED_UAT_DB_URL;
    private static String SEED_UAT_USER;
    private static String SEED_UAT_PASS;
    
    private static String CP_OI_JDBC_DRIVER;
    private static String CP_OI_DB_URL;
    private static String CP_OI_USER;
    private static String CP_OI_PASS;
    
    
    
    static Connection connection = null;
    static
    {
        Properties properties = new Properties();
        try
        {
        	//ATP_JDBC_DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        	//ATP_DB_URL="jdbc:sqlserver://10.162.87.236:1433;DatabaseName=ATP;encrypt=false";
        	//ATP_USER ="CTADMDCW";
        	//ATP_PASS ="E287zYML";
        	
       /* 	ATP_JDBC_DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        	ATP_DB_URL="jdbc:sqlserver://10.162.87.207:1433;DatabaseName=ATP;encrypt=false";
        	ATP_USER ="CTADMDCW";
        	ATP_PASS ="uZLkkENc8BK4";
        	
        	 JDBC_DRIVER_UAT="com.microsoft.sqlserver.jdbc.SQLServerDriver";
             DB_URL_UAT="jdbc:sqlserver://10.162.60.19:1433;DatabaseName=ATP;encrypt=false";
             USER_UAT ="CTADMDCW";
             PASS_UAT ="Pafhvf8L";
             
             OIS_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
             OIS_DB_URL = "jdbc:sqlserver://10.162.87.205:1433;DatabaseName=oi_codes_cp";
             OIS_USER = "corteva_oi";
             OIS_PASS = "cOrteva#9";
             
             SEED_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
             SEED_DB_URL = "jdbc:sqlserver://10.162.87.205:1433;DatabaseName=oi_codes_seeds";
             SEED_USER = "corteva_seeds";
             SEED_PASS = "cOrteva#9";
             
             SEED_UAT_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
             SEED_UAT_DB_URL = "jdbc:sqlserver://10.162.60.19:1433;DatabaseName=OperatorInterface;encrypt=false";
             SEED_UAT_USER = "CTADMDCW";
             SEED_UAT_PASS = "Pafhvf8L";
             
             JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
             DB_URL = "jdbc:sqlserver://10.162.60.19:1433;DatabaseName=OIS;encrypt=false";
             USER = "CTADMDCW";
             PASS = "Pafhvf8L";
             
             CP_OI_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
     	     CP_OI_DB_URL = "jdbc:sqlserver://10.162.87.205:1433;DatabaseName=oi_codes_cp;encrypt=false";
     	     CP_OI_USER = "oi_codes";
     	     CP_OI_PASS = "3DQmBulrX";
     	     
     	    */ 
   //DB URL's Changed  --21-03-2025
        	
        	ATP_JDBC_DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        	ATP_DB_URL="jdbc:sqlserver://10.162.87.204:1433;DatabaseName=ATP;encrypt=false";
        	ATP_USER ="ctadmdcw";
        	ATP_PASS ="eCOap7DUUVBp0jXCr7Gy";
        	
        	 JDBC_DRIVER_UAT="com.microsoft.sqlserver.jdbc.SQLServerDriver";
             DB_URL_UAT="jdbc:sqlserver://10.162.60.35:1433;DatabaseName=ATP;encrypt=false";
             USER_UAT ="ctadmdcw";
             PASS_UAT ="nhfgKR5IoBgRONwM2Mlk";
             
             OIS_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
             OIS_DB_URL = "jdbc:sqlserver://10.162.87.203:1433;DatabaseName=oi_codes_cp";
             OIS_USER = "ctadmdcw";
             OIS_PASS = "8JFfw4UZsBegXtQ0kGvY";
             
             SEED_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
             SEED_DB_URL = "jdbc:sqlserver://10.162.87.203:1433;DatabaseName=oi_codes_seeds";
             SEED_USER = "ctadmdcw";
             SEED_PASS = "8JFfw4UZsBegXtQ0kGvY";
             
             SEED_UAT_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
             //SEED_UAT_DB_URL = "jdbc:sqlserver://10.162.60.35:1433;DatabaseName=OperatorInterface;encrypt=false";
             SEED_UAT_DB_URL = "jdbc:sqlserver://10.162.60.35:1433;DatabaseName=oi_codes_seeds;encrypt=false";
             SEED_UAT_USER = "ctadmdcw";
             SEED_UAT_PASS = "nhfgKR5IoBgRONwM2Mlk";
             
             JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
             DB_URL = "jdbc:sqlserver://10.162.60.35:1433;DatabaseName=OIS;encrypt=false";
             USER = "ctadmdcw";
             PASS = "nhfgKR5IoBgRONwM2Mlk";
             
             CP_OI_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
     	     CP_OI_DB_URL = "jdbc:sqlserver://10.162.87.203:1433;DatabaseName=oi_codes_cp;encrypt=false";
     	     CP_OI_USER = "ctadmdcw";
     	     CP_OI_PASS = "8JFfw4UZsBegXtQ0kGvY";
     	     
             
        } catch (Exception e)
        {
            LOG.info(e.getCause(), e);
        }
    }
    
    public static Connection getATPConnection()
    {
        try
        {
        	LOG.info("ATP_DB_URL==>"+ATP_DB_URL);

			Class.forName(ATP_JDBC_DRIVER);
			connection = DriverManager.getConnection(ATP_DB_URL, ATP_USER, ATP_PASS);
        	//LOG.info("connection==>"+connection);

			return connection;
        }
        catch (Exception e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    
    public static Connection getUATATPConnection()
    {
        try
        {
        	LOG.info("DB_URL_UAT==>"+DB_URL_UAT);

			Class.forName(JDBC_DRIVER_UAT);
			connection = DriverManager.getConnection(DB_URL_UAT, USER_UAT, PASS_UAT);
        	//LOG.info("connection==>"+connection);
			return connection;
        }
        catch (Exception e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    public static Connection getOISConnection()
    {
        try
        {
        	LOG.info("OIS_DB_URL==>"+OIS_DB_URL);
			Class.forName(OIS_JDBC_DRIVER);
			connection = DriverManager.getConnection(OIS_DB_URL, OIS_USER, OIS_PASS);
			return connection;
        }
        catch (Exception e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    public static Connection getLocalDBConnection()
    {
        try
        {
        	LOG.info("DB_URL==>"+DB_URL);
			Class.forName(JDBC_DRIVER);
			connection = DriverManager.getConnection(DB_URL,USER,PASS);
			return connection;
        }
        catch (Exception e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    public static Connection getSeedCodesDBConnection()
    {
        try
        {
        	LOG.info("SEED_DB_URL==>"+SEED_DB_URL);
			Class.forName(SEED_JDBC_DRIVER);
			connection = DriverManager.getConnection(SEED_DB_URL,SEED_USER,SEED_PASS);
			return connection;
        }
        catch (Exception e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    public static Connection getSeedLocalDBConnection()
    {
        try
        {
        	LOG.info("SEED_UAT_DB_URL==>"+SEED_UAT_DB_URL);
			Class.forName(SEED_UAT_JDBC_DRIVER);
			connection = DriverManager.getConnection(SEED_UAT_DB_URL,SEED_UAT_USER,SEED_UAT_PASS);
			return connection;
        }
        catch (Exception e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    
    public static Connection getOICPConnection()
    {
        try
        {
        	 LOG.info("CP_OI_DB_URL===>"+CP_OI_DB_URL);
			Class.forName(CP_OI_JDBC_DRIVER);
			connection = DriverManager.getConnection(CP_OI_DB_URL, CP_OI_USER, CP_OI_PASS);
        	//LOG.info("connection==>"+connection);

			return connection;
        }
        catch (Exception e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
}