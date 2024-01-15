package com.hexaware.dao;
	import java.util.Date;
import java.text.ParseException;
	import java.text.SimpleDateFormat;

	public class DateUtils {
	    public static boolean isValidDateFormat(String dateStr, String expectedFormat) {
	        SimpleDateFormat sdf = new SimpleDateFormat(expectedFormat);
	        sdf.setLenient(false);

	        try {
	            sdf.parse(dateStr);
	            return true;
	        } catch (ParseException e) {
	            return false;
	        }
	    }

	    public static void main(String[] args) {
	        // Example usage
	    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	    	Date date=new Date();
	        String givenDate = sdf.format(date);
	        System.out.println(givenDate);
	        
	        String expectedFormat = "yyyy-MM-dd";

	        if (isValidDateFormat(givenDate, expectedFormat)) {
	            System.out.println("The given date format is valid and matches the expected format.");
	        } else {
	            System.out.println("The given date format is not valid or does not match the expected format.");
	        }
	    }
	}


