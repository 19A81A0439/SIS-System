package com.hexaware.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBPropertyUtil {
	
	//static String fileName="src/com/hexaware/util/db.properties";

	 public static String getConnectionString(String fileName) {
	        Properties properties = new Properties();

	        try (FileInputStream input = new FileInputStream(fileName)) {
	            // Load properties from the file
	            properties.load(input);

	            // Retrieve the database URL by key
	            String databaseUrl = properties.getProperty("db.url");

	            if (databaseUrl != null && !databaseUrl.isEmpty()) {
	                return databaseUrl;
	            } else {
	                throw new RuntimeException("Database URL not found in the properties file.");
	            }
	        } catch (IOException e) {
	            throw new RuntimeException("Error reading properties file: " + e.getMessage());
	        }
}
	 
	 
	 public static void main(String[] args) {
	        // Example usage
	        String fileName = "src/com/hexaware/util/db.properties";
	        String connectionString = getConnectionString(fileName);

	        System.out.println("Connection String: " + connectionString);
	    }
}
