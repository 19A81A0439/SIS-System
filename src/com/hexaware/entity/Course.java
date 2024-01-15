package com.hexaware.entity;

import java.util.List;

public class Course {
    private long courseId;
    private int courseCredits;
    private String courseName;
    private long teacherId;
    private List<Enrollment> enrollments;
   


    // Constructor
    public Course(long courseId, String courseName,int courseCredits, long teacherId) {
        this.courseId = courseId;
        this.courseCredits = courseCredits;
        this.courseName = courseName;
        this.teacherId = teacherId;
        
      
    }
    public List<Enrollment> getEnrollments() {
		return enrollments;
	}
	public void setEnrollments(List<Enrollment> enrollments) {
		this.enrollments = enrollments;
	}
	public Course(long courseId, String courseName,int courseCredits, long teacherId,List<Enrollment> enrollments) {
        this.courseId = courseId;
        this.courseCredits = courseCredits;
        this.courseName = courseName;
        this.teacherId = teacherId;
        this.enrollments=enrollments;
        
      
    }
    public Course(List<Enrollment> enrollments) {
        
        this.enrollments=enrollments;
        
      
    }
    



	public long getCourseId() {
		return courseId;
	}



	public void setCourseId(long courseId) {
		this.courseId = courseId;
	}



	public int getCourseCredits() {
		return courseCredits;
	}



	public void setCourseCredits(int courseCredits) {
		this.courseCredits = courseCredits;
	}



	public String getCourseName() {
		return courseName;
	}



	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}



	public long getTeacherId() {
		return teacherId;
	}



	public void setTeacherId(long teacherId) {
		this.teacherId = teacherId;
	}



	public Course(long courseId2, String courseName2) {
		// TODO Auto-generated constructor stub
		this.courseId=courseId2;
		this.courseName=courseName2;
	}



	public Course(long l, String string, int i) {
		// TODO Auto-generated constructor stub
		this.courseId=l;
		this.courseName=string;
		this.courseCredits=i;
	}



	public Course(long l) {
		// TODO Auto-generated constructor stub
		this.courseId=l;
	}
	public Course(String courseName) {
		// TODO Auto-generated constructor stub
		this.courseName=courseName;
	}
	public Course() {
		// TODO Auto-generated constructor stub
	}



    
}