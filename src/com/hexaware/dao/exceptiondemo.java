package com.hexaware.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
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
import com.hexaware.exception.DuplicateEnrollmentException;
import com.hexaware.exception.InvalidStudentDataException;
import com.hexaware.exception.PaymentValidationException;
import com.hexaware.exception.StudentNotFoundException;
import com.hexaware.util.DBConnUtil;
import com.hexaware.util.DBPropertyUtil;


public class exceptiondemo  {

	private static final String fileName="src/com/hexaware/util/db.properties"; 
    private static final String URL = DBPropertyUtil.getConnectionString(fileName);
    
    
    
    
   
    // Enrolls the student in a course
    public void enrollInCourse(Student student, Course course) throws DuplicateEnrollmentException, FileNotFoundException, ClassNotFoundException, IOException {
        try (Connection connection =DBConnUtil.getConnection(URL)) {
            // Check if the student is already enrolled in the course
            String checkEnrollmentQuery = "SELECT * FROM enrollments WHERE student_id = ? AND course_id = ?";
            try (PreparedStatement checkEnrollmentStatement = connection.prepareStatement(checkEnrollmentQuery)) {
                checkEnrollmentStatement.setLong(1, student.getStudentId());
                checkEnrollmentStatement.setLong(2, course.getCourseId());
                try (ResultSet resultSet = checkEnrollmentStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        // If not already enrolled, enroll the student
                        String enrollQuery = "INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES (?, ?, ?)";
                        try (PreparedStatement enrollStatement = connection.prepareStatement(enrollQuery)) {
                            enrollStatement.setLong(1, student.getStudentId());
                            enrollStatement.setLong(2, course.getCourseId());
                            enrollStatement.setDate(3, new java.sql.Date(new Date().getTime()));
                            enrollStatement.executeUpdate();
                            System.out.println("Enrolled in the course successfully!");
                        }
                    } else {
                        // Student is already enrolled, throw exception
                        throw new DuplicateEnrollmentException("Student is already enrolled in the course.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStudentInfo(Student student, String firstName, String lastName, Date dateOfBirth, String email,
            long phoneNumber) throws StudentNotFoundException, InvalidStudentDataException, FileNotFoundException, ClassNotFoundException, IOException {
        if (isInvalidStudentData(firstName, lastName, dateOfBirth, email, phoneNumber)) {
            throw new InvalidStudentDataException("Invalid data provided for updating student. All fields are required.");
        }
        

        if(!isValidDateFormat(dateOfBirth)) {
        	  throw new InvalidStudentDataException("wrong date format should be in yyyy-MM-dd");
        }

        try (Connection connection = DBConnUtil.getConnection(URL)) {
            String updateQuery = "UPDATE students SET first_name=?, last_name=?, date_of_birth=?, email=?, phone_number=? WHERE student_id=?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                java.sql.Date sqlSpecificDate = new java.sql.Date(dateOfBirth.getTime());
                updateStatement.setString(1, firstName);
                updateStatement.setString(2, lastName);
                updateStatement.setDate(3, sqlSpecificDate);
                updateStatement.setString(4, email);
                updateStatement.setLong(5, phoneNumber);
                updateStatement.setLong(6, student.getStudentId());
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected == 0) {
                    // No student found for the given ID, throw exception
                    throw new StudentNotFoundException("Student not found for the given ID.");
                }
                System.out.println("Student information updated successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isInvalidStudentData(String firstName, String lastName, Date dateOfBirth, String email,
            long phoneNumber) {
        return firstName == null || lastName == null || dateOfBirth == null || email == null || email.trim().isEmpty()
                || !isValidDateFormat(dateOfBirth) || phoneNumber <= 0;
    }

    private boolean isValidDateFormat(Date dateOfBirth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            sdf.format(dateOfBirth);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Make a payment
    public void makePayment(Student student, double amount, Date paymentDate1) throws FileNotFoundException, ClassNotFoundException, IOException {
        try (Connection connection = DBConnUtil.getConnection(URL)) {
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


    // Display student information
    public void displayStudentInfo(Student student) throws StudentNotFoundException, FileNotFoundException, ClassNotFoundException, IOException {
        try (Connection connection = DBConnUtil.getConnection(URL)) {
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
                        // No student found for the given ID, throw exception
                        throw new StudentNotFoundException("Student not found for the given ID.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get enrolled courses
    public List<Course> getEnrolledCourses(Student student) throws StudentNotFoundException, FileNotFoundException, ClassNotFoundException, IOException {
        List<Course> enrolledCourses = new ArrayList<>();
        try (Connection connection =DBConnUtil.getConnection(URL)) {
            // Check if the student exists
            if (!isStudentExists(student)) {
                throw new StudentNotFoundException("Student not found for the given ID.");
            }

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
        return enrolledCourses;
    }

    // Get payment history
    public List<Payment> getPaymentHistory(Student student) throws StudentNotFoundException, FileNotFoundException, ClassNotFoundException, IOException {
        List<Payment> paymentHistory = new ArrayList<>();
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            // Check if the student exists
            if (!isStudentExists(student)) {
                throw new StudentNotFoundException("Student not found for the given ID.");
            }

            String selectQuery = "SELECT * FROM payments WHERE student_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, student.getStudentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                    	long paymentId=resultSet.getLong("payment_id");
                        double amount = resultSet.getDouble("amount");
                        Date paymentDate = resultSet.getDate("payment_date");
                        Payment payment = new Payment(paymentId,amount, paymentDate);
                        paymentHistory.add(payment);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentHistory;
    }

    // Helper method to check if a student exists
    private boolean isStudentExists(Student student) throws SQLException,FileNotFoundException,  ClassNotFoundException, IOException {
        String checkStudentQuery = "SELECT * FROM students WHERE student_id = ?";
        try (Connection connection =DBConnUtil.getConnection(URL);
             PreparedStatement checkStudentStatement = connection.prepareStatement(checkStudentQuery)) {
            checkStudentStatement.setLong(1, student.getStudentId());
            try (ResultSet resultSet = checkStudentStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
    public static boolean isValidDateFormat(String dateStr) throws PaymentValidationException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new PaymentValidationException("Empty or null Payment Date");
        }

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
        sdf1.setLenient(false);
        sdf2.setLenient(false);

        try {
            // Try parsing with the first format
            sdf1.parse(dateStr);
            return true;
        } catch (ParseException e1) {
        	throw new PaymentValidationException(e1.getMessage());
            
        }
    }


    public static void main(String[] args) throws ParseException, StudentNotFoundException, InvalidStudentDataException, FileNotFoundException, ClassNotFoundException, IOException {
        // Example usage
        exceptiondemo studentDao = new exceptiondemo();
        

        Student student = new Student(1L);
        Course course = new Course(1001L, "Math");

        // Enroll in a course
        try {
			studentDao.enrollInCourse(student, course);
		} catch (DuplicateEnrollmentException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
		}

        String specificDateString = "2003-09-01";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date specificDate = dateFormat.parse(specificDateString);

        // Update student info
        studentDao.updateStudentInfo(student, "Pr", "Bala", specificDate, "updated.email@example.com", 9897979696L);

        
        studentDao.displayStudentInfo(student);

        // Get enrolled courses
        Student student2 = new Student(1l);
        List<Course> enrolledCourses = studentDao.getEnrolledCourses(student2);

		if (enrolledCourses.isEmpty()) {
		    System.out.println("Student is not enrolled in any courses.");
		} else {
		    System.out.println("Enrolled Courses:");
		    for (Course enrolledCourse : enrolledCourses) {
		        System.out.println(enrolledCourse.getCourseName());
		    }
		}

        List<Payment> paymentHistory = studentDao.getPaymentHistory(student2);

		if (paymentHistory.isEmpty()) {
		    System.out.println("No payment history available for the student.");
		} else {
		    System.out.println("Payment History:");
		    for (Payment payment : paymentHistory) {
		        System.out.println("Amount: " + payment.getAmount() + ", Date: " + payment.getPaymentDate());
		    }
		}
		//update date format exception
		// Test 2: Update student info with invalid data (e.g., null values)
		Student validStudent=new Student(1L);
        try {
            studentDao.updateStudentInfo(validStudent, null, null, null, null, 0);
        } catch (StudentNotFoundException | InvalidStudentDataException e) {
            System.out.println(e.getMessage());
        }
        //Invalid date format
        try {
            // Test 4: Update student info with invalid date format (dd-mm-yyyy)
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
            Date invalidDate1 = dateFormat1.parse("09-08-2023");

            studentDao.updateStudentInfo(validStudent, "Updated", "Student", invalidDate1, "updated.student@example.com", 987654321l);
        } catch (InvalidStudentDataException | ParseException e) {
            System.out.println( e.getMessage());
        }
        //make payment
        
       
        	
			studentDao.makePayment(validStudent, 200, new Date());
        
        
    }
}
