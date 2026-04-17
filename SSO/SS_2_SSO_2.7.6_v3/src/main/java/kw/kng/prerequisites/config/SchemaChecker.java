package kw.kng.prerequisites.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class SchemaChecker {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void checkSchema() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try 
        {
        	System.out.println("#############################################################################################");
        	System.out.println("--------------------------------- SCHEMA CHECKER -------------------------------------------");
        	System.out.println("#############################################################################################");
        	
        	
            if (dataSource instanceof HikariDataSource) 
            {
                HikariDataSource hk = (HikariDataSource) dataSource;
                System.out.println(">>> JDBC URL         : " + hk.getJdbcUrl());
            } else 
            {
                System.out.println(">>> JDBC URL         : (DataSource is not HikariCP)");
            }
            
            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            
            // Fetch Current Oracle User
            rs = stmt.executeQuery("SELECT USER FROM dual");
            if (rs.next()) 
            {
                System.out.println(">>> Connected User   : " + rs.getString(1));
            }
            rs.close();
            // Fetch Current Schema
            rs = stmt.executeQuery("SELECT SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM dual");
            if (rs.next()) 
            {
                System.out.println(">>> Connected Schema : " + rs.getString(1));
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
        	try 
        	{
        		if (rs != null)  
        		{
                    rs.close();
                    System.out.println(">>> RESULT SET EXECUTED SUCCESSFULLY.");
                }
        		else 
        		{
                    System.out.println(">>> RESULT SET is already null.");
                }
        	} 
        	catch (Exception e) 
        	{
        		System.out.println(">>> RESULT SET ISSUE: " + e);
        	}
            try 
            {
            	if (stmt != null)
            	{
            		stmt.close(); 
            		System.out.println(">>> SQL STATEMENT EXECUTED SUCCESSFULLY.");
            	}
            	else 
        		{
                    System.out.println(">>> SQL STATEMENT is already null.");
                }
            } 
            catch (Exception e) 
            {
            	System.out.println(">>> SQL STATEMENT EXECUTION ISSUE: " + e);
            }
            try 
            {
            	if (conn != null) 
            	{
            		conn.close(); 
                    System.out.println(">>> DATABASE CONNECTION EXECUTED SUCCESSFULLY.");
                }
        		else 
        		{
                    System.out.println(">>> DATABASE CONNECTION is already null.");
                }
            } 
            catch (Exception e) 
            {
            	System.out.println(">>> DATABASE CONNECTION ISSUE: " + e);
            }
            System.out.println("#############################################################################################");
        }
    }
}

/*
 Knowledge Transfer:
 ----------------------------
 1. Check which schema and which user is connected.
 2. Full compatible with Springboot 1.x.x and 2.x.x
 3. Mainly compatibele Java 1.8
  
 */

