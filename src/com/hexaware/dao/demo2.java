package com.hexaware.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hexaware.entity.Course;
import com.hexaware.entity.Payment;
import com.hexaware.entity.Student;

public class demo2 {

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
                    if (!resultSet.next()) {
                        // If not already enrolled, enroll the student
                        String enrollQuery = "INSERT INTO enrollments (student_id, course_id,enrollment_date) VALUES (?, ?, ?)";
                        try (PreparedStatement enrollStatement = connection.prepareStatement(enrollQuery)) {
                            enrollStatement.setLong(1, student.getStudentId());
                            enrollStatement.setLong(2, course.getCourseId());
                            enrollStatement.setDate(3, new java.sql.Date(new Date().getTime()));
                            enrollStatement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Updates the student's information
    public void updateStudentInfo(Student student, String firstName, String lastName, Date dateOfBirth,
                                  String email, String phoneNumber) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateQuery = "UPDATE students SET first_name=?, last_name=?, date_of_birth=?, email=?, phone_number=? WHERE student_id=?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, firstName);
                updateStatement.setString(2, lastName);
                updateStatement.setDate(3, new java.sql.Date(dateOfBirth.getTime()));
                updateStatement.setString(4, email);
                updateStatement.setString(5, phoneNumber);
                updateStatement.setLong(6, student.getStudentId());
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Records a payment made by the student
    public void makePayment(Student student, double amount, Date paymentDate) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertPaymentQuery = "INSERT INTO payments (student_id, amount, payment_date) VALUES (?, ?, ?)";
            try (PreparedStatement insertPaymentStatement = connection.prepareStatement(insertPaymentQuery)) {
                insertPaymentStatement.setLong(1, student.getStudentId());
                insertPaymentStatement.setDouble(2, amount);
                insertPaymentStatement.setDate(3, new java.sql.Date(paymentDate.getTime()));
                insertPaymentStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Displays detailed information about the student
    public void displayStudentInfo(Student student) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM students WHERE student_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, student.getStudentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Student Information:");
                        System.out.println("ID: " + resultSet.getInt("id"));
                        System.out.println("First Name: " + resultSet.getString("first_name"));
                        System.out.println("Last Name: " + resultSet.getString("last_name"));
                        System.out.println("Date of Birth: " + resultSet.getDate("date_of_birth"));
                        System.out.println("Email: " + resultSet.getString("email"));
                        System.out.println("Phone Number: " + resultSet.getString("phone_number"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieves a list of courses in which the student is enrolled
    public List<Course> getEnrolledCourses(Student student) {
        List<Course> enrolledCourses = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT courses.course_id, courses.course_name FROM courses " +
                    "JOIN enrollments ON courses.course_id = enrollments.course_id WHERE enrollments.student_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, student.getStudentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int courseId = resultSet.getInt("id");
                        String courseName = resultSet.getString("course_name");
                        Course course = new Course(courseId, courseName);
                        enrolledCourses.add(course);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrolledCourses;
    }

    // Retrieves a list of payment records for the student
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
        return paymentHistory;
    }

    public static void main(String[] args) {
        // Example usage
        demo2 studentDAO = new demo2();
        Student student = new Student( "John", "Doe", new Date(), "john.doe@example.com", 1234567890L);

        // Enroll in a course
        Course course = new Course(1001, "Math");
        studentDAO.enrollInCourse(student, course);

        // Update student info
        studentDAO.updateStudentInfo(student, "John", "Doe", new Date(), "john.doe@example.com", "9876543210");

        // Make a payment
        studentDAO.makePayment(student, 100.00, new Date());

        // Display student info
        studentDAO.displayStudentInfo(student);

        // Get enrolled courses
        List<Course> enrolledCourses = studentDAO.getEnrolledCourses(student);
        System.out.println("Enrolled Courses:");
        for (Course enrolledCourse : enrolledCourses) {
            System.out.println(enrolledCourse.getCourseName());
        }

        // Get payment history
        List<Payment> paymentHistory = studentDAO.getPaymentHistory(student);
        System.out.println("Payment History:");
        for (Payment payment : paymentHistory) {
            System.out.println("Amount: " + payment.getAmount() + ", Date: " + payment.getPaymentDate());
        }
    }
}
