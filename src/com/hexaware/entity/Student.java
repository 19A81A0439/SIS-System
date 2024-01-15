package com.hexaware.entity;

import java.util.Date;
import java.util.List;



public class Student {
    private long studentId;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String email;
    private long phoneNumber;
    private List<Enrollment> enrollments;
    
    
    
   

    // Constructor
    public Student(long studentId,String firstName, String lastName, Date dateOfBirth, String email, long phoneNumber) {
        this.studentId=studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        
        
    }
    
    public List<Enrollment> getEnrollments() {
		return enrollments;
	}

	public void setEnrollments(List<Enrollment> enrollments) {
		this.enrollments = enrollments;
	}

	public Student(long studentId,String firstName, String lastName, Date dateOfBirth, String email, long phoneNumber,List<Enrollment> enrollments) {
        this.studentId=studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.enrollments=enrollments;
        
    }



    public Student(List<Enrollment> enrollments) {
        
        this.enrollments=enrollments;
        
    }

    





	public Student(long studentId, String firstName, String lastName, String email, long phoneNumber) {
		super();
		this.studentId = studentId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}











	public Student(long l) {
		// TODO Auto-generated constructor stub
		this.studentId=l;
	}











	public Student(String string, String string2, Date date, String string3, long l) {
		// TODO Auto-generated constructor stub
		this.firstName=string;
		this.lastName=string2;
		this.dateOfBirth=date;
		this.email=string3;
		this.phoneNumber=l;
	}











	@Override
	public String toString() {
		return "Student [studentId=" + studentId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", dateOfBirth=" + dateOfBirth + ", email=" + email + ", phoneNumber=" + phoneNumber + "]";
	}











	public long getStudentId() {
		return studentId;
	}





	public void setStudentId(long studentId) {
		this.studentId = studentId;
	}





	public String getFirstName() {
		return firstName;
	}





	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}





	public String getLastName() {
		return lastName;
	}





	public void setLastName(String lastName) {
		this.lastName = lastName;
	}





	public Date getDateOfBirth() {
		return dateOfBirth;
	}





	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}





	public String getEmail() {
		return email;
	}





	public void setEmail(String email) {
		this.email = email;
	}





	public long getPhoneNumber() {
		return phoneNumber;
	}





	public void setPhoneNumber(long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
    
    
    

   
  
}