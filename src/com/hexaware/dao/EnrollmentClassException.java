package com.hexaware.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hexaware.entity.Course;
import com.hexaware.entity.Enrollment;
import com.hexaware.entity.Student;
import com.hexaware.exception.CourseNotFoundException;
import com.hexaware.exception.StudentNotFoundException;
import com.hexaware.util.DBConnUtil;
import com.hexaware.util.DBPropertyUtil;

public class EnrollmentClassException {
	private static final String fileName="src/com/hexaware/util/db.properties"; 
    private static final String URL = DBPropertyUtil.getConnectionString(fileName);

    public Student getStudent(Enrollment enrollment) throws StudentNotFoundException, FileNotFoundException, ClassNotFoundException, IOException {
        Student student = null;
        try (Connection connection =DBConnUtil.getConnection(URL)) {
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
                    } else {
                        throw new StudentNotFoundException("Student not found for the given enrollment ID.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public Course getCourse(Enrollment enrollment) throws CourseNotFoundException, FileNotFoundException, ClassNotFoundException, IOException {
        Course course = null;
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            String selectQuery = "SELECT * FROM courses c JOIN enrollments e ON c.course_id = e.course_id WHERE e.enrollment_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, enrollment.getEnrollmentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        course = new Course(
                                resultSet.getLong("course_id"),
                                resultSet.getString("course_name")
                        );
                    } else {
                        throw new CourseNotFoundException("Course not found for the given enrollment ID.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course;
    }

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
        // Example usage
        EnrollmentClassException enrollmentDao = new EnrollmentClassException();

        // Assuming you have an Enrollment object with a valid enrollmentId
        Enrollment enrollment = new Enrollment(10002L);

        // Get the student associated with the enrollment
        try {
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
        } catch (StudentNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // Assuming you have another Enrollment object with an invalid enrollmentId
        Enrollment invalidEnrollment = new Enrollment(99999L);

        // This should throw a StudentNotFoundException
        try {
            Student invalidStudent = enrollmentDao.getStudent(invalidEnrollment);

            if (invalidStudent != null) {
                // The following code won't be reached if the exception is thrown
                System.out.println("Student Information:");
                System.out.println("Student ID: " + invalidStudent.getStudentId());
            } else {
                System.out.println("Student not found for the given enrollment ID.");
            }
        } catch (StudentNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // Get the course associated with the enrollment
        try {
            Course course = enrollmentDao.getCourse(enrollment);

            if (course != null) {
                System.out.println("Course Information:");
                System.out.println("Course ID: " + course.getCourseId());
                System.out.println("Course Name: " + course.getCourseName());
            } else {
                System.out.println("Course not found for the given enrollment ID.");
            }
        } catch (CourseNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // Assuming you have another Enrollment object with an invalid enrollmentId
        Enrollment invalidEnrollmentForCourse = new Enrollment(99999L);

        // This should throw a CourseNotFoundException
        try {
            Course invalidCourse = enrollmentDao.getCourse(invalidEnrollmentForCourse);

            if (invalidCourse != null) {
                // The following code won't be reached if the exception is thrown
                System.out.println("Course Information:");
                System.out.println("Course ID: " + invalidCourse.getCourseId());
            } else {
                System.out.println("Course not found for the given enrollment ID.");
            }
        } catch (CourseNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
