package com.hexaware.dao;

import com.hexaware.entity.Course;
import com.hexaware.entity.Enrollment;
import com.hexaware.entity.Student;

public interface EnrollmentDao {
	public Student getStudent(Enrollment enrollment);
	public Course getCourse(Enrollment enrollment);
}
