package com.hexaware.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.hexaware.entity.Course;
import com.hexaware.entity.Payment;
import com.hexaware.entity.Student;
import com.hexaware.entity.Teacher;
import com.hexaware.exception.CourseNotFoundException;
import com.hexaware.exception.DuplicateEnrollmentException;
import com.hexaware.exception.PaymentValidationException;
import com.hexaware.exception.StudentNotFoundException;
import com.hexaware.exception.TeacherNotFoundException;
import com.hexaware.util.DBConnUtil;
import com.hexaware.util.DBPropertyUtil;

public class SISDaoImpl implements SISDao {
	
	    private static String fileName="src/com/hexaware/util/db.properties";
	    private static final String URL = DBPropertyUtil.getConnectionString(fileName);
	    
	    
	    
	 // Insert student into students table
	    public void insertStudent(Student student) throws DuplicateEnrollmentException, FileNotFoundException, ClassNotFoundException, IOException {
	        try (Connection connection = DBConnUtil.getConnection(URL)) {
	            // Check if the student already exists
	            if (isStudentExistByPhoneNumber(student)) {
	                throw new DuplicateEnrollmentException("Student with ID " + student.getStudentId() + " already exists.");
	            }
	            
	            java.sql.Date dateOfBirth=new java.sql.Date(student.getDateOfBirth().getTime());
	            // Insert the student
	            String insertQuery = "INSERT INTO students (student_id,first_name, last_name, date_of_birth, email, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
	            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
	            	insertStatement.setLong(1, student.getStudentId());
	                insertStatement.setString(2, student.getFirstName());
	                insertStatement.setString(3, student.getLastName());
	                insertStatement.setDate(4, dateOfBirth);
	                insertStatement.setString(5, student.getEmail());
	                insertStatement.setLong(6, student.getPhoneNumber());
	                insertStatement.executeUpdate();
	                System.out.println("Student inserted successfully!");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    
	    
	 // Insert Teacher into teachers table
	    public void insertTeacher(Teacher teacher) throws DuplicateEnrollmentException, FileNotFoundException, ClassNotFoundException, IOException {
	        try (Connection connection = DBConnUtil.getConnection(URL)) {
	            // Check if the student already exists
	            if (isTeacherExistsByEmail(teacher)) {
	                throw new DuplicateEnrollmentException("Teacher already exists");
	            }
	            
	            
	            // Insert the teacher
	            String insertQuery = "INSERT INTO teacher (first_name, last_name, email) VALUES ( ?, ?, ?)";
	            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
	            	
	                insertStatement.setString(1, teacher.getFirstName());
	                insertStatement.setString(2, teacher.getLastName());
	                
	                insertStatement.setString(3, teacher.getEmail());
	                
	                insertStatement.executeUpdate();
	                System.out.println("Teacher inserted successfully!");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	
	public void EnrollStudentInCourse(Student student, Course course) throws StudentNotFoundException, CourseNotFoundException, DuplicateEnrollmentException, SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        // Check if the student exists
        if (!isStudentExists(student)) {
            throw new StudentNotFoundException("Student not found for the given ID.");
        }

        // Check if the course exists
        if (!isCourseExists(course)) {
            throw new CourseNotFoundException("Course not found for the given ID.");
        }

        // Perform enrollment logic
        try {
            exceptiondemo exceptionDemo = new exceptiondemo();
            exceptionDemo.enrollInCourse(student, course);
            System.out.println("Student enrolled successfully!");
        } catch (DuplicateEnrollmentException e) {
            throw new DuplicateEnrollmentException("Duplicate enrollment: " + e.getMessage());
        }
    }

    // Other methods...

	// Helper method to check if a student exists
    private boolean isStudentExists(Student student) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        String checkStudentQuery = "SELECT * FROM students WHERE student_id = ?";
        try (Connection connection =DBConnUtil.getConnection(URL);
             PreparedStatement checkStudentStatement = connection.prepareStatement(checkStudentQuery)) {
            checkStudentStatement.setLong(1, student.getStudentId());
            try (ResultSet resultSet = checkStudentStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
    
    //Teacher exists by email
    
    private boolean isTeacherExistsByEmail(Teacher teacher) throws FileNotFoundException, ClassNotFoundException, IOException {
    	boolean exists = false;
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            String selectQuery = "SELECT * FROM teacher WHERE email = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setString(1, teacher.getEmail());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    exists = resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }
    // Check if a student exists based on phone number
    public boolean isStudentExistByPhoneNumber(Student student) throws FileNotFoundException, ClassNotFoundException, IOException {
        boolean exists = false;
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            String selectQuery = "SELECT * FROM students WHERE phone_number = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, student.getPhoneNumber());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    exists = resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    private boolean isCourseExists(Course course) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
    	String checkCourseQuery = "SELECT * FROM courses WHERE course_id = ?";
        try (Connection connection =DBConnUtil.getConnection(URL);
             PreparedStatement checkStudentStatement = connection.prepareStatement(checkCourseQuery)) {
            checkStudentStatement.setLong(1, course.getCourseId());
            try (ResultSet resultSet = checkStudentStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
    public void AssignTeacherToCourse(Teacher teacher, Course course)
            throws TeacherNotFoundException, CourseNotFoundException, FileNotFoundException, DuplicateEnrollmentException {
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            // Check if the teacher exists
            if (!isTeacherExists(teacher)) {
                throw new TeacherNotFoundException("Teacher not found for the given teacher ID.");
            }

            // Check if the course exists
            if (!isCourseExists(course)) {
                throw new CourseNotFoundException("Course not found for the given course ID.");
            }
         // Check if the teacher is already assigned to the course
            if (isTeacherAssigned(course,teacher) ){
                throw new DuplicateEnrollmentException("Teacher is already assigned to this course.");
            }

            // Use the provided method to assign the teacher to the course
            CourseDaoImpl courseClassException = new CourseDaoImpl();
            courseClassException.assignTeacher(course, teacher);

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    // Placeholder implementation - replace with actual database interactions

    private boolean isTeacherExists(Teacher teacher) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
    	String checkTeacherQuery = "SELECT * FROM teacher WHERE teacher_id = ?";
        try (Connection connection1 = DBConnUtil.getConnection(URL);
             PreparedStatement checkStudentStatement = connection1.prepareStatement(checkTeacherQuery)) {
            checkStudentStatement.setLong(1, teacher.getTeacherId());
            try (ResultSet resultSet = checkStudentStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
    private boolean isTeacherAssigned(Course course,Teacher teacher) throws SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        // Check if the teacher is already assigned to the course
        String checkAssignmentQuery = "SELECT * FROM courses WHERE course_id = ? AND teacher_id = ?";
        try (Connection connection1 = DBConnUtil.getConnection(URL);
        		PreparedStatement checkAssignmentStatement = connection1.prepareStatement(checkAssignmentQuery)) {
            checkAssignmentStatement.setLong(1, course.getCourseId());
            checkAssignmentStatement.setLong(2, teacher.getTeacherId());
            try (ResultSet resultSet = checkAssignmentStatement.executeQuery()) {
                return resultSet.next(); // If resultSet has any rows, teacher is already assigned
            }
        }
    }
    public void recordPayment(Student student, double amount, Date paymentDate)
            throws StudentNotFoundException, PaymentValidationException, FileNotFoundException, ClassNotFoundException, IOException {
        try {
            // Check if the student exists
            if (!isStudentExists(student)) {
                throw new StudentNotFoundException("Student not found for the given ID.");
            }

            // Validate payment amount and date
            validatePaymentData(amount, paymentDate);
            /*
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String dateStr=sdf.format(paymentDate);
            if(isValidDateFormat(dateStr)) {;
            	throw new PaymentValidationException("Wrong Date Format . Please enter date format in yyyy-MM-dd");
            }
*/
            // Make the payment using the existing makePayment method from ExceptionDemo class
            exceptiondemo exceptionDemo = new exceptiondemo();
            exceptionDemo.makePayment(student, amount, paymentDate);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Other methods...
    public static boolean isValidDateFormat(String dateStr ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    private void validatePaymentData(double amount, Date paymentDate) throws PaymentValidationException {
        if (amount <= 0) {
            throw new PaymentValidationException("Invalid payment amount. Amount should be greater than 0.");
        }

        if (paymentDate == null) {
            throw new PaymentValidationException("Payment date cannot be null.");
        }
        /*
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            String formattedDate = sdf.format(paymentDate);
            if (!formattedDate.equals(paymentDate.toString())) {
                throw new PaymentValidationException("Invalid Payment Date Format");
            }
        } catch (Exception e) {
            throw new PaymentValidationException("Invalid Payment Date Format");
        }
        
        */
    }
    //ENrollment Report
    public List<Course> GenerateEnrollmentReport(Student student) throws StudentNotFoundException, SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        
    	List<Course> enrolledCourses=new ArrayList<>();
    	// Check if the student exists
    	
        if (!isStudentExists(student)) {
            throw new StudentNotFoundException("Student not found for the given ID.");
        }

        
        // Perform enrollment logic
        try {
            exceptiondemo exceptionDemo = new exceptiondemo();
           enrolledCourses= exceptionDemo.getEnrolledCourses(student);
           return enrolledCourses;
            
        }catch(Exception e) {
        	System.out.println(e.getMessage());
        }
        return enrolledCourses;
    }
  //payment Report
    public List<Payment> GeneratePaymentReport(Student student) throws StudentNotFoundException, SQLException, FileNotFoundException, ClassNotFoundException, IOException {
        
    	List<Payment> paymentHistory=new ArrayList<>();
    	// Check if the student exists
    	
        if (!isStudentExists(student)) {
            throw new StudentNotFoundException("Student not found for the given ID.");
        }

        
        // Perform enrollment logic
        try {
            exceptiondemo exceptionDemo = new exceptiondemo();
           paymentHistory= exceptionDemo.getPaymentHistory(student);
           return paymentHistory;
            
        }catch(Exception e) {
        	System.out.println(e.getMessage());
        }
        return paymentHistory;
    }
    //Course Statistics
    public List<String> CalculateCourseStatistics(Course course) throws SQLException, CourseNotFoundException, FileNotFoundException, ClassNotFoundException, IOException{
    	List<String> enrolledStudentNames = new ArrayList<>();
    	if (!isCourseExists(course)) {
            throw new CourseNotFoundException("Course not found for the given ID.");
        }
    	try {
    		CourseDaoImpl cce=new CourseDaoImpl();
            enrolledStudentNames= cce.getEnrolledStudentNames(course);
            return enrolledStudentNames;
    	}catch(Exception e ) {
    		System.out.println(e.getMessage());
    	}
    	return enrolledStudentNames;
    	
    }
    
    public static void main(String args[]) throws StudentNotFoundException, CourseNotFoundException, DuplicateEnrollmentException, SQLException, ParseException, FileNotFoundException, ClassNotFoundException, IOException {
    	SISDaoImpl s=new SISDaoImpl();
    	//Student st=new Student(1l);
    	//Course cr=new Course(1007);
    	/*
    	try {
    	s.EnrollStudentInCourse(st, cr);
    }
    	catch(StudentNotFoundException|CourseNotFoundException|DuplicateEnrollmentException|SQLException e) {
    		System.out.println(e.getMessage());
    	}
    	
    	Course cr1=new Course(1009,"Art",5);
    	Teacher teacher=new Teacher(101l);
    	try {
    		s.AssignTeacherToCourse(teacher, cr1);
    	}catch(TeacherNotFoundException|CourseNotFoundException|FileNotFoundException|DuplicateEnrollmentException e) {
    		System.out.println(e.getMessage());
    	}
    	*/
    	Student s3=new Student(1l);
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date currentDate = new Date();
    	String formattedDate =sdf.format(currentDate);
    	Date curDate=sdf.parse(formattedDate);
    	try {
			s.recordPayment(s3, 500.00, curDate);
		} catch (StudentNotFoundException|PaymentValidationException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} 
    	/*
    	Student s4=new Student(2l);
    	try {
        	List<Course> enrolledCourses=s.GenerateEnrollmentReport(s4);
        	if(!enrolledCourses.isEmpty()) {
        		for(Course courses:enrolledCourses) {
        			System.out.println(courses.getCourseId()+" "+courses.getCourseName());
        		}
        	}
        	else {
        		System.out.println("Student does not enrolled in any courses");
        	}
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	Student s5=new Student(1l);
    	try {
        	List<Payment> paymentHistory=s.GeneratePaymentReport(s5);
        	if(!paymentHistory.isEmpty()) {
        		for(Payment payments:paymentHistory) {
        			System.out.println(payments.getPaymentId()+" "+payments.getAmount()+" "+payments.getPaymentDate());
        		}
        	}
        	else {
        		System.out.println("Student has no payment History");
        	}
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	Course course=new Course(1007);
    	try {
        	List<String> enrolledStudentNames=s.CalculateCourseStatistics(course);
        	int numberOfEnrollments=enrolledStudentNames.size();
        	System.out.println("Number Of Enrollments: " +numberOfEnrollments);
        	
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	Scanner sc = new Scanner (System.in);
    	System.out.println("give date in yy-MM-dd format");
    	String dob=sc.next();
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	Date dob1=sdf.parse(dob);
    	Student s4=new Student(100l,"ji","gh",dob1,"uihvu@gmail.com",98789889688l);
    	try {
    		s.insertStudent(s4);
    	}catch(DuplicateEnrollmentException|FileNotFoundException e) {
    		System.out.println(e.getMessage());
    	}
    	sc.close();
    	*/
    	
    }
    
}

