package com.hexaware.entity;

import java.util.List;

public class Teacher {
    private long teacherId;
    private String firstName;
    private String lastName;
    private String email;
    private List<Course> assignedCourses;
    
 


    public Teacher(List<Course> assignedCourses) {
		super();
		this.assignedCourses = assignedCourses;
	}



	public Teacher(long teacherId, String firstName, String lastName, String email, List<Course> assignedCourses) {
		super();
		this.teacherId = teacherId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.assignedCourses = assignedCourses;
	}



	



	public List<Course> getAssignedCourses() {
		return assignedCourses;
	}



	public void setAssignedCourses(List<Course> assignedCourses) {
		this.assignedCourses = assignedCourses;
	}



	public Teacher(long l) {
		// TODO Auto-generated constructor stub
		this.teacherId=l;
	}



	public Teacher(long teacherId2, String firstName2, String lastName2) {
		// TODO Auto-generated constructor stub
		this.teacherId=teacherId2;
		this.firstName=firstName2;
		this.lastName=lastName2;
	}



	public Teacher(String teacherFirstName, String teacherLastName, String teacherEmail) {
		// TODO Auto-generated constructor stub
		this.firstName=teacherFirstName;
		this.lastName=teacherLastName;
		this.email=teacherEmail;
	}



	public Teacher(String string) {
		// TODO Auto-generated constructor stub
		this.email=string;
	}



	public long getTeacherId() {
		return teacherId;
	}



	public void setTeacherId(long teacherId) {
		this.teacherId = teacherId;
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



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	

    
}