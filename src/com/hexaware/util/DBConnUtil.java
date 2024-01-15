package com.hexaware.util;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnUtil {
	
    public static Connection getConnection(String connectionString) throws SQLException, FileNotFoundException, IOException, ClassNotFoundException 
    {
    	
    	Properties properties = new Properties();
    	String fileName = "src/com/hexaware/util/db.properties";
        try(FileInputStream input = new FileInputStream(fileName)) {
            // Load the JDBC driver (not needed for JDBC 4.0 and later)
        	Class.forName("com.mysql.cj.jdbc.Driver");
        	properties.load(input);
        	String connectionString1=connectionString;
        	String username = properties.getProperty("db.username");
        	String password=properties.getProperty("db.password");
            
            
            
            // Establish the connection
            return DriverManager.getConnection(connectionString1, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            // Handle exceptions related to driver loading or connection establishment
            throw new SQLException("Error establishing database connection: " + e.getMessage(), e);
        }
    }
/*
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        // Example usage
    	String fileName = "src/com/hexaware/util/db.properties";
        String connectionString = DBPropertyUtil.getConnectionString(fileName);
        try {
            Connection connection = getConnection(connectionString);
            System.out.println("Connected to the database.");
            // Perform database operations using the 'connection' object
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }*/
}
