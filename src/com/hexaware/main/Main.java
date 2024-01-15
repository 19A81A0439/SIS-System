package com.hexaware.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.hexaware.dao.CourseDaoImpl;
import com.hexaware.dao.SISDaoImpl;
import com.hexaware.dao.TeacherClassException;
import com.hexaware.entity.Course;
import com.hexaware.entity.Student;
import com.hexaware.entity.Teacher;
import com.hexaware.exception.CourseNotFoundException;
import com.hexaware.exception.DuplicateEnrollmentException;
import com.hexaware.exception.PaymentValidationException;
import com.hexaware.exception.StudentNotFoundException;
import com.hexaware.exception.TeacherNotFoundException;

public class Main {

	public static void main(String[] args) throws ParseException, FileNotFoundException, ClassNotFoundException, DuplicateEnrollmentException, IOException, TeacherNotFoundException, CourseNotFoundException, SQLException  {
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		SISDaoImpl sdi=new SISDaoImpl();
		//Task 8
		/*
		//john doe details
		
		System.out.println("Enter your firstname:");
		String firstName=sc.next();
		System.out.println("Enter your lastname:");
		String lastName=sc.next();
		System.out.println("Enter your d-o-b:");
		String dob=sc.next();
		System.out.println("Enter your email:");
		String email=sc.next();
		System.out.println("ENter your phone number:");
		long phoneNumber=sc.nextLong();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	Date dateOfBirth=sdf.parse(dob);
    	
    	//Insert student into Students Table in database
    	
    	Student student=new Student(99,firstName,lastName,dateOfBirth,email,phoneNumber);
    	try {
    		sdi.insertStudent(student);
    	}
    	catch(DuplicateEnrollmentException|FileNotFoundException e) {
    		System.out.println(e.getMessage());
    	}
    	
    	//ENrolling in a course
		Student student2=new Student(99);
    	System.out.println("Enter the number of courses you want to enroll:");
    	int n=sc.nextInt();
    	String courseName;
    	for(int i=0;i<n;i++) {
    		
        	try {
        		System.out.println("Enter the name of the course:");
        		courseName=sc.next();
        		Course course=new Course(courseName);
        		
            	
            	CourseClassException cce=new CourseClassException();
        		long courseId=cce.getCourseIdByCourseName(course);
        		Course finalCourse=new Course(courseId,courseName);
        		sdi.EnrollStudentInCourse(student2, finalCourse);
        	}
        	catch(StudentNotFoundException|DuplicateEnrollmentException|CourseNotFoundException e) {
        		
        		System.out.println(e.getMessage());
        		
        	} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
    	}
    	
    	
    	
    	
    	//---------Teacher insertion---------
    	//----Teacher details-------
    	System.out.println("Enter teacher firstname:");
		String teacherFirstName=sc.next();
		System.out.println("Enter teacher lastname:");
		String teacherLastName=sc.next();
		
		System.out.println("Enter teacher email:");
		String teacherEmail=sc.next();
		
		//----Inserting teacher info
		
		Teacher teacher=new Teacher(teacherFirstName,teacherLastName,teacherEmail);
		try {
			sdi.insertTeacher(teacher);
		}catch(DuplicateEnrollmentException|ClassNotFoundException|IOException e) {
			System.out.println(e.getMessage());
		}
		
		//Assigning course to teacher
		System.out.println("Enter the course name:");
		String courseName=sc.next();
		Course course=new Course(courseName);
		
    	
    	CourseClassException cce=new CourseClassException();
		long courseId=cce.getCourseIdByCourseName(course);
		Course finalCourse=new Course(courseId,courseName,10);
		TeacherClassException tce=new TeacherClassException();
		long teachId=tce.getTeacherIdByEmail(teacher);
		Teacher finalTeacher=new Teacher(teachId);
		
		try {
			cce.assignTeacher(finalCourse, finalTeacher);
		}
		catch(FileNotFoundException|TeacherNotFoundException|CourseNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		
		
		//Make payment
		try {
			System.out.println("Enter student Id:");
			long studentId=sc.nextLong();
			System.out.println("Enter the amount:");
			double amount=sc.nextDouble();
			Student paymentStudent = new Student(studentId);
			sdi.recordPayment(paymentStudent, amount, new Date());
			
		
		}
		catch(StudentNotFoundException|PaymentValidationException e) {
			System.out.println(e.getMessage());
		}
    	
    	*/
		
		//Enrollment Report
		try {
			System.out.println("Enter Course Name:");
			String courseName1=sc.next();
			Course course5=new Course(courseName1);
			
	    	
	    	CourseDaoImpl cce=new CourseDaoImpl();
			long courseId=cce.getCourseIdByCourseName(course5);
			Course enrollmentReportForCourse=new Course(courseId);
			List<String> studentNames=sdi.CalculateCourseStatistics(enrollmentReportForCourse);
			for(String studentName:studentNames) {
				System.out.println(studentName);
			}
		}
		catch(CourseNotFoundException e) {
			System.out.println(e.getMessage());
		}
    	
    	
		
		
    	
    	
    	
    	
    	
    	sc.close();
	}

}
