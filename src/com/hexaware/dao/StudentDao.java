package com.hexaware.dao;

import java.util.Date;
import java.util.List;

import com.hexaware.entity.Course;
import com.hexaware.entity.Payment;
import com.hexaware.entity.Student;

public interface StudentDao {
	 public void enrollInCourse(Student student, Course course);
	 public void updateStudentInfo(Student student, String firstName, String lastName, Date dateOfBirth,
	            String email, long phoneNumber);
	 public void makePayment(Student student, double amount, Date paymentDate1);
	 public void displayStudentInfo(Student student) ;
	 public List<Course> getEnrolledCourses(Student student);
	 public List<Payment> getPaymentHistory(Student student);
}
