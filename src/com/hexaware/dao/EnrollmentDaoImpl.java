package com.hexaware.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hexaware.entity.Course;
import com.hexaware.entity.Enrollment;
import com.hexaware.entity.Student;

public class EnrollmentDaoImpl implements EnrollmentDao {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/sisdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Subbu@999";
    
	 @Override
	    public Student getStudent(Enrollment enrollment) {
	        Student student = null;
	        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	            String selectQuery = "SELECT * FROM students s JOIN enrollments e ON s.student_id = e.student_id WHERE e.enrollment_id = ?";
	            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
	                selectStatement.setLong(1, enrollment.getEnrollmentId());
	                try (ResultSet resultSet = selectStatement.executeQuery()) {
	                    if (resultSet.next()) {
	                        student = new Student(
	                                resultSet.getLong("student_id"),
	                                resultSet.getString("first_name"),
	                                resultSet.getString("last_name"),
	                                resultSet.getDate("date_of_birth"),
	                                resultSet.getString("email"),
	                                resultSet.getLong("phone_number")
	                        );
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return student;
	    }
	 	@Override
	    public Course getCourse(Enrollment enrollment) {
	        Course course = null;
	        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
	            String selectQuery = "SELECT * FROM courses c JOIN enrollments e ON c.course_id = e.course_id WHERE e.enrollment_id = ?";
	            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
	                selectStatement.setLong(1, enrollment.getEnrollmentId());
	                try (ResultSet resultSet = selectStatement.executeQuery()) {
	                    if (resultSet.next()) {
	                        course = new Course(
	                                resultSet.getLong("course_id"),
	                                resultSet.getString("course_name")
	                        );
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return course;
	    }
	 public static void main(String[] args) {
	        // Example usage
	        EnrollmentDao enrollmentDao = new EnrollmentDaoImpl();

	        // Assuming you have an Enrollment object with a valid enrollmentId
	        Enrollment enrollment = new Enrollment(10002L); 

	        // Get the student associated with the enrollment
	        Student student = enrollmentDao.getStudent(enrollment);

	        if (student != null) {
	            System.out.println("Student Information:");
	            System.out.println("Student ID: " + student.getStudentId());
	            System.out.println("First Name: " + student.getFirstName());
	            System.out.println("Last Name: " + student.getLastName());
	            System.out.println("Date of Birth: " + student.getDateOfBirth());
	            System.out.println("Email: " + student.getEmail());
	            System.out.println("Phone Number: " + student.getPhoneNumber());
	        } else {
	            System.out.println("Student not found for the given enrollment ID.");
	        }
	     // Get the course associated with the enrollment
	        Course course = enrollmentDao.getCourse(enrollment);

	        if (course != null) {
	            System.out.println("Course Information:");
	            System.out.println("Course ID: " + course.getCourseId());
	            System.out.println("Course Name: " + course.getCourseName());
	        } else {
	            System.out.println("Course not found for the given enrollment ID.");
	        }

	       
	    }

}
