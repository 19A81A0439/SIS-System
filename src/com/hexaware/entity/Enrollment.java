package com.hexaware.entity;

import java.util.Date;

public class Enrollment {
	private long enrollmentId;
    private Student studentId;
    private Course courseId;
    private Date enrollmentDate;
    private Student student;
    private Course course;

    public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Enrollment(long enrollmentId, Student studentId, Course courseId, Date enrollmentDate, Student student,
			Course course) {
		super();
		this.enrollmentId = enrollmentId;
		this.studentId = studentId;
		this.courseId = courseId;
		this.enrollmentDate = enrollmentDate;
		this.student = student;
		this.course = course;
	}

	// Constructor
    public Enrollment(long enrollmentId,Student studentId, Course courseId, Date enrollmentDate) {
    	this.enrollmentId=enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
    }

	public Enrollment(long l) {
		// TODO Auto-generated constructor stub
		this.enrollmentId=l;
	}

	public Enrollment(long enrollmentId2, Student student, Course course) {
		// TODO Auto-generated constructor stub
		this.enrollmentId=enrollmentId2;
		
	}

	public long getEnrollmentId() {
		return enrollmentId;
	}

	public void setEnrollmentId(long enrollmentId) {
		this.enrollmentId = enrollmentId;
	}

	public Student getStudentId() {
		return studentId;
	}

	public void setStudentId(Student studentId) {
		this.studentId = studentId;
	}

	public Course getCourseId() {
		return courseId;
	}

	public void setCourseId(Course courseId) {
		this.courseId = courseId;
	}

	public Date getEnrollmentDate() {
		return enrollmentDate;
	}

	public void setEnrollmentDate(Date enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}

	
    
}