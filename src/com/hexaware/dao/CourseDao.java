package com.hexaware.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.hexaware.entity.Course;
import com.hexaware.entity.Enrollment;
import com.hexaware.entity.Teacher;

public interface CourseDao {

	public void assignTeacher(Course course, Teacher teacher) throws FileNotFoundException, ClassNotFoundException, IOException;


	public void updateCourseInfo(Course course,String courseName, int credits, long teacherId) throws FileNotFoundException, ClassNotFoundException, IOException;



	public void displayCourseInfo(Course course) throws FileNotFoundException, ClassNotFoundException, IOException;



	public List<String> getEnrolledStudentNames(Course course) throws FileNotFoundException, ClassNotFoundException, IOException;




	public Teacher getAssignedTeachersNames(Course course) throws FileNotFoundException, ClassNotFoundException, IOException;

}
