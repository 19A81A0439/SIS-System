package com.hexaware.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hexaware.entity.Course;
import com.hexaware.entity.Payment;
import com.hexaware.entity.Student;

public class StudentDaoImpl implements StudentDao{

    private static final String DB_URL = "jdbc:mysql://localhost:3306/sisdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Subbu@999";

    // Enrolls the student in a course
    public void enrollInCourse(Student student, Course course) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if the student is already enrolled in the course
            String checkEnrollmentQuery = "SELECT * FROM enrollments WHERE student_id = ? AND course_id = ?";
            try (PreparedStatement checkEnrollmentStatement = connection.prepareStatement(checkEnrollmentQuery)) {
                checkEnrollmentStatement.setLong(1, student.getStudentId());
                checkEnrollmentStatement.setLong(2, course.getCourseId());
                try (ResultSet resultSet = checkEnrollmentStatement.executeQuery()) {
                	Date curDate=new Date();
                	java.sql.Date sqlDate = new java.sql.Date(curDate.getTime());

                    if (!resultSet.next()) {
                        // If not already enrolled, enroll the student
                        String enrollQuery = "INSERT INTO enrollments (student_id, course_id,enrollment_date) VALUES (?, ?, ?)";
                        try (PreparedStatement enrollStatement = connection.prepareStatement(enrollQuery)) {
                            enrollStatement.setLong(1, student.getStudentId());
                            enrollStatement.setLong(2, course.getCourseId());
                            enrollStatement.setDate(3,sqlDate);
                            enrollStatement.executeUpdate();
                            System.out.println("Enrolled in the course successfully!");
                        }
                    } else {
                        System.out.println("Student is already enrolled in the course.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateStudentInfo(Student student, String firstName, String lastName, Date dateOfBirth,
		            String email, long phoneNumber) {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
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
				}
}
    public void makePayment(Student student, double amount, Date paymentDate1) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertPaymentQuery = "INSERT INTO payments (student_id, amount, payment_date) VALUES (?, ?, ?)";
            try (PreparedStatement insertPaymentStatement = connection.prepareStatement(insertPaymentQuery)) {
            	java.sql.Date paymentDate = new java.sql.Date(paymentDate1.getTime());
                insertPaymentStatement.setLong(1, student.getStudentId());
                insertPaymentStatement.setDouble(2, amount);
                insertPaymentStatement.setDate(3,paymentDate);
                insertPaymentStatement.executeUpdate();
                System.out.println("Payment recorded successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void displayStudentInfo(Student student) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM students WHERE student_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, student.getStudentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Student Information:");
                        System.out.println("Student ID: " + resultSet.getLong("student_id"));
                        System.out.println("First Name: " + resultSet.getString("first_name"));
                        System.out.println("Last Name: " + resultSet.getString("last_name"));
                        System.out.println("Date of Birth: " + resultSet.getDate("date_of_birth"));
                        System.out.println("Email: " + resultSet.getString("email"));
                        System.out.println("Phone Number: " + resultSet.getLong("phone_number"));
                    } else {
                        System.out.println("Student not found for the given ID.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Course> getEnrolledCourses(Student student) {
        List<Course> enrolledCourses = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT courses.course_id, courses.course_name " +
                    "FROM courses JOIN enrollments ON courses.course_id = enrollments.course_id " +
                    "WHERE enrollments.student_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, student.getStudentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int courseId = resultSet.getInt("course_id");
                        String courseName = resultSet.getString("course_name");
                        Course course = new Course(courseId, courseName);
                        enrolledCourses.add(course);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (enrolledCourses.isEmpty()) {
            System.out.println("Student is not enrolled in any courses.");
        }
        return enrolledCourses;
    }
    public List<Payment> getPaymentHistory(Student student) {
        List<Payment> paymentHistory = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM payments WHERE student_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, student.getStudentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                        double amount = resultSet.getDouble("amount");
                        Date paymentDate = resultSet.getDate("payment_date");
                        Payment payment = new Payment(amount, paymentDate);
                        paymentHistory.add(payment);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(paymentHistory.isEmpty()) {
        	System.out.println("student has no payment history");
        }
        return paymentHistory;
    }
    public static void main(String[] args) throws ParseException {
        // Example usage
        StudentDaoImpl studentDao = new StudentDaoImpl();
        
       
        Student student = new Student(1L);
        Course course = new Course(1001L, "Math");

        // Enroll in a course
        studentDao.enrollInCourse(student, course);
        String specificDateString = "2003-09-01";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date specificDate;
        specificDate=dateFormat.parse(specificDateString);
        // Update student info
        studentDao.updateStudentInfo(student, "Pr", "Bala", specificDate, "updated.email@example.com", 9897979696L);
        double paymentAmount=560.00;
        Date curDate=new Date();
    	
        Date paymentDate=curDate;
        studentDao.makePayment(student, paymentAmount,paymentDate);
        studentDao.displayStudentInfo(student);
     // Get enrolled courses
        Student student2=new Student(1l);
        List<Course> enrolledCourses = studentDao.getEnrolledCourses(student2);
        
        if (enrolledCourses.isEmpty()) {
            System.out.println("Student is not enrolled in any courses.");
        } else {
            System.out.println("Enrolled Courses:");
            for (Course enrolledCourse : enrolledCourses) {
                System.out.println(enrolledCourse.getCourseName());
            }
        }
        
     // Get payment history
        Student student10=new Student(10L);
        List<Payment> paymentHistory = studentDao.getPaymentHistory(student10);

        if (paymentHistory.isEmpty()) {
            System.out.println("No payment history available for the student.");
        } else {
            System.out.println("Payment History:");
            for (Payment payment : paymentHistory) {
                System.out.println("Amount: " + payment.getAmount() + ", Date: " + payment.getPaymentDate());
            }
        }
    }
    }






