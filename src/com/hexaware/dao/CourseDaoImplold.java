package com.hexaware.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hexaware.entity.Course;
import com.hexaware.entity.Enrollment;
import com.hexaware.entity.Student;
import com.hexaware.entity.Teacher;
import com.hexaware.util.DBConnUtil;
import com.hexaware.util.DBPropertyUtil;

public class CourseDaoImplold implements CourseDao {
    
    private static String fileName="src/com/hexaware/util/db.properties";
    private static final String URL = DBPropertyUtil.getConnectionString(fileName);
    @Override
    public void assignTeacher(Course course, Teacher teacher) throws FileNotFoundException, ClassNotFoundException, IOException {
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            // Check if the teacher is already assigned to the course
            String checkAssignmentQuery = "SELECT * FROM courses WHERE course_id = ? AND teacher_id = ?";
            try (PreparedStatement checkAssignmentStatement = connection.prepareStatement(checkAssignmentQuery)) {
                checkAssignmentStatement.setLong(1, course.getCourseId());
                checkAssignmentStatement.setLong(2, teacher.getTeacherId());
                if (checkAssignmentStatement.executeQuery().next()) {
                    System.out.println("Teacher is already assigned to this course.");
                } else {
                    // Assign the teacher to the course
                    String assignTeacherQuery = "INSERT INTO courses (course_id,course_name,credits,teacher_id) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement assignTeacherStatement = connection.prepareStatement(assignTeacherQuery)) {
                        assignTeacherStatement.setLong(1, course.getCourseId());
                        assignTeacherStatement.setString(2,course.getCourseName());
                        assignTeacherStatement.setInt(3,course.getCourseCredits()); 
                        assignTeacherStatement.setLong(4, teacher.getTeacherId());
                        assignTeacherStatement.executeUpdate();
                        System.out.println("Teacher assigned to the course successfully!");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void updateCourseInfo(Course course, String courseName,int credits, long teacherId) throws FileNotFoundException, ClassNotFoundException, IOException {
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            // Check if the course with the given course code exists
            String checkCourseQuery = "SELECT * FROM courses WHERE course_id = ?";
            try (PreparedStatement checkCourseStatement = connection.prepareStatement(checkCourseQuery)) {
                checkCourseStatement.setLong(1, course.getCourseId());
                if (checkCourseStatement.executeQuery().next()) {
                    // Update the course information
                    String updateCourseQuery = "UPDATE courses SET course_name=?, credits=?,teacher_id=? WHERE course_id=?";
                    try (PreparedStatement updateCourseStatement = connection.prepareStatement(updateCourseQuery)) {
                        updateCourseStatement.setString(1, courseName);
                        updateCourseStatement.setInt(2, credits);
                        updateCourseStatement.setLong(3,teacherId);
                        updateCourseStatement.setLong(4, course.getCourseId());
                        updateCourseStatement.executeUpdate();
                        System.out.println("Course information updated successfully!");
                    }
                } else {
                    System.out.println("Course with the given course code not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void displayCourseInfo(Course course) throws FileNotFoundException, ClassNotFoundException, IOException {
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            String selectQuery = "SELECT * FROM courses WHERE course_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, course.getCourseId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Course Information:");
                        System.out.println("Course ID: " + resultSet.getLong("course_id"));
                        System.out.println("Course Name: " + resultSet.getString("course_name"));
                        System.out.println("Course Credits: " + resultSet.getInt("credits"));
                        System.out.println("Teacher Id: " + resultSet.getString("teacher_id"));
                    } else {
                        System.out.println("Course not found for the given course ID.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<String> getEnrolledStudentNames(Course course) throws FileNotFoundException, ClassNotFoundException, IOException {
        List<String> enrolledStudentNames = new ArrayList<>();
        try (Connection connection =DBConnUtil.getConnection(URL)) {
            String selectQuery = "SELECT students.first_name, students.last_name " +
                                 "FROM enrollments " +
                                 "JOIN students ON enrollments.student_id = students.student_id " +
                                 "WHERE enrollments.course_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, course.getCourseId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        String fullName = firstName + " " + lastName;
                        enrolledStudentNames.add(fullName);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrolledStudentNames;
    }
    @Override
    public Teacher getAssignedTeachersNames(Course course) throws FileNotFoundException, ClassNotFoundException, IOException {
    	Teacher teacher = null;
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            String selectQuery = "SELECT teacher.teacher_id,teacher.first_name,teacher.last_name FROM teacher " +
                                 "JOIN courses ON courses.teacher_id = teacher.teacher_id " +
                                 "WHERE courses.course_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, course.getCourseId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                    	long teacherId = resultSet.getLong("teacher_id");
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        teacher = new Teacher(teacherId, firstName, lastName);
                        
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teacher;
    }



    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
        // Example usage
        CourseDao courseDao = new CourseDaoImplold();

        // Assuming you have another Course object with a valid ID
        Course anotherCourse = new Course(1013L, "Computer Science",10);

        Teacher teacher=new Teacher(101L);
		// Assign a teacher to the course
        courseDao.assignTeacher(anotherCourse, teacher);
        //update course info
        Course updatedCourseInfo=new Course(1003l);
        //courseDao.updateCourseInfo(updatedCourseInfo, "Art", 5, 101l);
        courseDao.displayCourseInfo(updatedCourseInfo);
     // Get enrolled student names for the course
        List<String> enrolledStudentNames = courseDao.getEnrolledStudentNames(updatedCourseInfo);
        System.out.println("Enrolled Students for the course:");
        for (String studentName : enrolledStudentNames) {
            System.out.println(studentName);
        }
        
     // Get assigned teachers for the course
        Teacher assignedTeacher = courseDao.getAssignedTeachersNames(updatedCourseInfo);
        if (assignedTeacher!=null) {
            System.out.println("Assigned Teacher:");
            
                System.out.println("Teacher ID: " + assignedTeacher.getTeacherId());
                System.out.println("First Name: " + assignedTeacher.getFirstName());
                System.out.println("Last Name: " + assignedTeacher.getLastName());
            
        } else {
            System.out.println("No teachers assigned for the course.");
        }
    }
}
