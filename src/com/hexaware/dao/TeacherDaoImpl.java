package com.hexaware.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hexaware.entity.Course;
import com.hexaware.entity.Teacher;

public class TeacherDaoImpl implements TeacherDao {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/sisdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Subbu@999";

    @Override
    public void updateTeacherInfo(Teacher teacher,String firstName, String lastName, String email) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateQuery = "UPDATE teacher SET first_name=?,last_name=?, email=?  WHERE teacher_id=?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, firstName);
                updateStatement.setString(2, lastName);
                updateStatement.setString(3, email);
                
                updateStatement.setLong(4, teacher.getTeacherId());
                updateStatement.executeUpdate();
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Teacher information updated successfully!");
                } else {
                    System.out.println("Teacher not found for the given teacher ID. No updates were made.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        }
    }
    @Override
    public void displayTeacherInfo(Teacher teacher) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM teacher WHERE teacher_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, teacher.getTeacherId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Teacher Information:");
                        System.out.println("Teacher ID: " + resultSet.getLong("teacher_id"));
                        System.out.println("First Name: " + resultSet.getString("first_name"));
                        System.out.println("Last Name: " + resultSet.getString("last_name"));
                        System.out.println("Email: " + resultSet.getString("email"));
                        
                    } else {
                        System.out.println("Teacher not found for the given teacher ID.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<Course> getAssignedCourses(Teacher teacher) {
        List<Course> assignedCourses = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT course_id, course_name FROM courses WHERE teacher_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, teacher.getTeacherId());

                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                        long courseId = resultSet.getLong("course_id");
                        String courseName = resultSet.getString("course_name");
                        Course course = new Course(courseId, courseName);
                        assignedCourses.add(course);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignedCourses;
    }


    public static void main(String[] args) throws SQLException {
        // Example usage
        TeacherDao teacherDao = new TeacherDaoImpl();

        // Assuming you have a Teacher object with a valid teacherId
        Teacher teacher = new Teacher(101L);

        // Update teacher info
        teacherDao.updateTeacherInfo(teacher, "Dr","Strange", "DrStrange.email@example.com");
     // Display teacher info
        teacherDao.displayTeacherInfo(teacher);
        List<Course> assignedCourses=teacherDao.getAssignedCourses(teacher);
        if (!assignedCourses.isEmpty()) {
            System.out.println("Assigned Courses:");
            for (Course course : assignedCourses) {
                System.out.println("Course ID: " + course.getCourseId() + ", Course Name: " + course.getCourseName());
            }
        } else {
            System.out.println("No courses assigned to the teacher.");
        }
    }
}
