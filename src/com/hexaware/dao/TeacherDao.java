package com.hexaware.dao;

import java.sql.SQLException;
import java.util.List;

import com.hexaware.entity.Course;
import com.hexaware.entity.Teacher;

public interface TeacherDao {

	

	public void updateTeacherInfo(Teacher teacher, String firstName, String lastName, String email) throws SQLException;

	public void displayTeacherInfo(Teacher teacher);
	public List<Course> getAssignedCourses(Teacher teacher);

}
