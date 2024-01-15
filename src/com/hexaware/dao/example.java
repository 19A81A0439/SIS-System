package com.hexaware.dao;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.hexaware.entity.Course;
import com.hexaware.entity.Student;
import com.hexaware.exception.CourseNotFoundException;
import com.hexaware.exception.DuplicateEnrollmentException;
import com.hexaware.exception.InsufficientFundsException;
import com.hexaware.exception.InvalidCourseDataException;
import com.hexaware.exception.InvalidEnrollmentDataException;
import com.hexaware.exception.InvalidStudentDataException;
import com.hexaware.exception.PaymentValidationException;
import com.hexaware.exception.StudentNotFoundException;

public class example {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/sisdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Subbu@999";
	
	public void enrollInCourse(Student student, Course course)
	        throws SQLException, DuplicateEnrollmentException, InvalidEnrollmentDataException,
	        StudentNotFoundException, CourseNotFoundException, InvalidCourseDataException {
	    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	        // Check if the student exists
	        if (!studentExists(connection, student.getStudentId())) {
	            throw new StudentNotFoundException("Student not found for the given ID.");
	        }

	        // Check if the course exists
	        if (!courseExists(connection, course.getCourseId())) {
	            throw new CourseNotFoundException("Course not found for the given ID.");
	        }

	        // Check if the student is already enrolled in the course
	        String checkEnrollmentQuery = "SELECT * FROM enrollments WHERE student_id = ? AND course_id = ?";
	        try (PreparedStatement checkEnrollmentStatement = connection.prepareStatement(checkEnrollmentQuery)) {
	            checkEnrollmentStatement.setLong(1, student.getStudentId());
	            checkEnrollmentStatement.setLong(2, course.getCourseId());
	            try (ResultSet resultSet = checkEnrollmentStatement.executeQuery()) {
	                Date curDate = new Date();
	                java.sql.Date sqlDate = new java.sql.Date(curDate.getTime());

	                if (!resultSet.next()) {
	                    // If not already enrolled, enroll the student
	                    String enrollQuery = "INSERT INTO enrollments (student_id, course_id,enrollment_date) VALUES (?, ?, ?)";
	                    try (PreparedStatement enrollStatement = connection.prepareStatement(enrollQuery)) {
	                        enrollStatement.setLong(1, student.getStudentId());
	                        enrollStatement.setLong(2, course.getCourseId());
	                        enrollStatement.setDate(3, sqlDate);
	                        int rowsInserted = enrollStatement.executeUpdate();

	                        if (rowsInserted > 0) {
	                            System.out.println("Enrolled in the course successfully!");
	                        } else {
	                            throw new InvalidEnrollmentDataException("Failed to enroll the student in the course.");
	                        }
	                    }
	                } else {
	                    throw new DuplicateEnrollmentException("Student is already enrolled in the course.");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace(); // Handle or log the exception appropriately
	        throw e;
	    }
	}

	// Helper method to check if a student exists
	private boolean studentExists(Connection connection, long studentId) throws SQLException {
	    String checkStudentQuery = "SELECT * FROM students WHERE student_id = ?";
	    try (PreparedStatement checkStudentStatement = connection.prepareStatement(checkStudentQuery)) {
	        checkStudentStatement.setLong(1, studentId);
	        try (ResultSet resultSet = checkStudentStatement.executeQuery()) {
	            return resultSet.next();
	        }
	    }
	}

	// Helper method to check if a course exists
	private boolean courseExists(Connection connection, long courseId) throws SQLException {
	    String checkCourseQuery = "SELECT * FROM courses WHERE course_id = ?";
	    try (PreparedStatement checkCourseStatement = connection.prepareStatement(checkCourseQuery)) {
	        checkCourseStatement.setLong(1, courseId);
	        try (ResultSet resultSet = checkCourseStatement.executeQuery()) {
	            return resultSet.next();
	        }
	    }
	}
	public void updateStudentInfo(Student student, String firstName, String lastName, Date dateOfBirth,
            String email, long phoneNumber) throws SQLException, StudentNotFoundException, InvalidStudentDataException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if the student exists
            if (!studentExists(connection, student.getStudentId())) {
                throw new StudentNotFoundException("Student not found for the given ID.");
            }

            // Validate student data
            if (isInvalidStudentData(firstName, lastName, dateOfBirth, email, phoneNumber)) {
                throw new InvalidStudentDataException("Invalid student data provided.");
            }

            String updateQuery = "UPDATE students SET first_name=?, last_name=?, date_of_birth=?, email=?, phone_number=? WHERE student_id=?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                java.sql.Date sqlSpecificDate = new java.sql.Date(dateOfBirth.getTime());
                updateStatement.setString(1, firstName);
                updateStatement.setString(2, lastName);
                updateStatement.setDate(3, sqlSpecificDate);
                updateStatement.setString(4, email);
                updateStatement.setLong(5, phoneNumber);
                updateStatement.setLong(6, student.getStudentId());
                updateStatement.executeUpdate();
                System.out.println("Student information updated successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    

    // Helper method to validate student data
    private boolean isInvalidStudentData(String firstName, String lastName, Date dateOfBirth, String email,
            long phoneNumber) {
        // Validate that no detail is left empty
        if (firstName == null || lastName == null || dateOfBirth == null || email == null || phoneNumber <= 0) {
            return true;
        }

        // Validate that the date of birth is in yyyy-MM-dd format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDateOfBirth = dateFormat.format(dateOfBirth);
        if (!formattedDateOfBirth.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return true;
        }

        // Validate that the phone number has 10 digits
        String phoneNumberString = String.valueOf(phoneNumber);
        if (phoneNumberString.length() != 10) {
            return true;
        }

        // Add any additional validation logic here

        // If all validations pass, return false (i.e., data is valid)
        return false;
    }
    public void makePayment(Student student, double amount, Date paymentDate1)
            throws PaymentValidationException, InsufficientFundsException, InvalidStudentDataException, StudentNotFoundException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if the student exists
            if (!studentExists(connection, student.getStudentId())) {
                throw new StudentNotFoundException("Student not found for the given ID.");
            }

            // Validate payment data
            if (amount <= 0) {
                throw new PaymentValidationException("Invalid payment amount. Amount must be greater than 0.");
            }

         // Validate payment date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false); // Set lenient to false to enforce strict date parsing
            try {
                dateFormat.parse(dateFormat.format(paymentDate1));
            } catch (ParseException e) {
                throw new PaymentValidationException("Invalid payment date format. Should be in yyyy-MM-dd.");
            }

            

            // Make the payment
            String insertPaymentQuery = "INSERT INTO payments (student_id, amount, payment_date) VALUES (?, ?, ?)";
            try (PreparedStatement insertPaymentStatement = connection.prepareStatement(insertPaymentQuery)) {
                java.sql.Date paymentDate = new java.sql.Date(paymentDate1.getTime());
                insertPaymentStatement.setLong(1, student.getStudentId());
                insertPaymentStatement.setDouble(2, amount);
                insertPaymentStatement.setDate(3, paymentDate);
                int rowsInserted = insertPaymentStatement.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("Payment recorded successfully!");
                } else {
                    throw new InvalidStudentDataException("Failed to record the payment.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidStudentDataException("Failed to record the payment.");
        }
    }


         

   
    

	
	public static void main(String[] args) throws ParseException, SQLException, InvalidStudentDataException, StudentNotFoundException, InsufficientFundsException, PaymentValidationException {
	    example studentDao = new example();

		Student student = new Student(1L);
		Course course = new Course(1001L, "Math");

		// Enroll in a course
		try {
		    studentDao.enrollInCourse(student, course);
		} catch (DuplicateEnrollmentException e) {
		    System.out.println(e.getMessage());
		} catch (InvalidEnrollmentDataException e) {
		    System.out.println(e.getMessage());
		} catch (StudentNotFoundException e) {
		    System.out.println(e.getMessage());
		} catch (CourseNotFoundException e) {
		    System.out.println(e.getMessage());
		} catch (InvalidCourseDataException e) {
		    System.out.println(e.getMessage());
		} catch (SQLException e) {
		    System.out.println("Error during enrollment: " + e.getMessage());
		}
		
		
		//updateStudent exception demo
		// Test StudentNotFoundException
        Student nonExistentStudent = new Student(999L);
        try {
            studentDao.updateStudentInfo(nonExistentStudent, "John", "Doe", new Date(), "john.doe@example.com",
                    1234567890L);
        } catch (StudentNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // Test InvalidStudentDataException
        Student studentWithInvalidData = new Student(1L);
        try {
            studentDao.updateStudentInfo(studentWithInvalidData, null, "Doe", new Date(),
                    "john.doe@example.com", 1234567890L);
        } catch (InvalidStudentDataException e) {
            System.out.println(e.getMessage());
        }
        
        //make payment code
     // Test StudentNotFoundException
        Student nonExistentStudent1 = new Student(999L);
        try {
            studentDao.makePayment(nonExistentStudent1, 100.0, new Date());
        } catch (StudentNotFoundException e) {
            System.out.println( e.getMessage());
        }

        // Test PaymentValidationException - Invalid payment amount
        Student validStudent = new Student(1L);
        try {
            studentDao.makePayment(validStudent, -50.0, new Date());
        } catch (PaymentValidationException e) {
            System.out.println(e.getMessage());
        }

     // Test PaymentValidationException (invalid date format)
        
        try {
            studentDao.makePayment(validStudent, 500.0, new SimpleDateFormat("dd-mm-yyyy").parse("01-01-2023"));
        } catch (InvalidStudentDataException | PaymentValidationException | InsufficientFundsException e) {
            System.out.println("Exception caught: " + e.getMessage());
        }
       

        // Test successful payment
        try {
            studentDao.makePayment(validStudent, 50.0, new SimpleDateFormat("yyyy-MM-dd").parse("2024-01-15"));
        } catch (PaymentValidationException | InsufficientFundsException | InvalidStudentDataException | StudentNotFoundException e) {
            e.printStackTrace();
        }
        
        
        
	}


}
