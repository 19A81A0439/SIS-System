package com.hexaware.dao;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hexaware.entity.Course;
import com.hexaware.entity.Teacher;
import com.hexaware.exception.CourseNotFoundException;
import com.hexaware.exception.InvalidCourseDataException;
import com.hexaware.exception.StudentNotFoundException;
import com.hexaware.exception.TeacherNotFoundException;
import com.hexaware.util.DBConnUtil;
import com.hexaware.util.DBPropertyUtil;

public class CourseDaoImpl {
	private static final String fileName="src/com/hexaware/util/db.properties"; 
    private static final String URL = DBPropertyUtil.getConnectionString(fileName);
    
    
    //GetCourse Id By Course Name
    public long getCourseIdByCourseName(Course course) throws CourseNotFoundException, FileNotFoundException, ClassNotFoundException, IOException {
        int courseId = -1; // Default value if course ID is not found

        try (Connection connection =DBConnUtil.getConnection(URL)) {
            String selectQuery = "SELECT course_id FROM courses WHERE course_name = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setString(1, course.getCourseName());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        courseId = resultSet.getInt("course_id");
                    } else {
                        throw new CourseNotFoundException("Course not found for the given course name " );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseId;
    }
    

    public void assignTeacher(Course course, Teacher teacher)
            throws FileNotFoundException, ClassNotFoundException, IOException, TeacherNotFoundException, CourseNotFoundException {
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            // Check if the teacher exists
            if (!teacherExists(connection, teacher.getTeacherId())) {
                throw new TeacherNotFoundException("Teacher not found for the given teacher ID.");
            }

            // Check if the course exists
            if (!courseExists(connection, course.getCourseId())) {
                throw new CourseNotFoundException("Course not found for the given course ID.");
            }

            String checkAssignmentQuery = "SELECT * FROM courses WHERE course_id = ? AND teacher_id = ?";
            try (PreparedStatement checkAssignmentStatement = connection.prepareStatement(checkAssignmentQuery)) {
                checkAssignmentStatement.setLong(1, course.getCourseId());
                checkAssignmentStatement.setLong(2, teacher.getTeacherId());
                if (checkAssignmentStatement.executeQuery().next()) {
                    System.out.println("Teacher is already assigned to this course.");
                } else {
                    // Assign the teacher to the course
                    String assignTeacherQuery = "INSERT INTO courses (course_name,credits,teacher_id) VALUES ( ?, ?, ?)";
                    try (PreparedStatement assignTeacherStatement = connection.prepareStatement(assignTeacherQuery)) {
                        //assignTeacherStatement.setLong(1, course.getCourseId());
                        assignTeacherStatement.setString(1,course.getCourseName());
                        assignTeacherStatement.setInt(2,course.getCourseCredits()); 
                        assignTeacherStatement.setLong(3, teacher.getTeacherId());
                        assignTeacherStatement.executeUpdate();
                        System.out.println("Teacher assigned to the course successfully!");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean teacherExists(Connection connection, long teacherId) throws SQLException {
        String checkTeacherQuery = "SELECT * FROM teacher WHERE teacher_id = ?";
        try (PreparedStatement checkTeacherStatement = connection.prepareStatement(checkTeacherQuery)) {
            checkTeacherStatement.setLong(1, teacherId);
            return checkTeacherStatement.executeQuery().next();
        }
    }

    private boolean courseExists(Connection connection, long courseId) throws SQLException {
        String checkCourseQuery = "SELECT * FROM courses WHERE course_id = ?";
        try (PreparedStatement checkCourseStatement = connection.prepareStatement(checkCourseQuery)) {
            checkCourseStatement.setLong(1, courseId);
            return checkCourseStatement.executeQuery().next();
        }
    }

   /* private boolean isTeacherAssigned(Connection connection, long courseId, long teacherId) throws SQLException {
        String checkAssignmentQuery = "SELECT * FROM courses WHERE course_id = ? AND teacher_id = ?";
        try (PreparedStatement checkAssignmentStatement = connection.prepareStatement(checkAssignmentQuery)) {
            checkAssignmentStatement.setLong(1, courseId);
            checkAssignmentStatement.setLong(2, teacherId);
            return checkAssignmentStatement.executeQuery().next();
        }
    }*/
    public void updateCourseInfo(Course course, String courseName, int credits, long teacherId)
            throws FileNotFoundException, ClassNotFoundException, IOException, CourseNotFoundException, InvalidCourseDataException {
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            // Check if the course with the given course ID exists
            if (!courseExists(connection, course.getCourseId())) {
                throw new CourseNotFoundException("Course not found for the given course ID.");
            }

            // Validate course data
            if (isInvalidCourseData(courseName, credits, teacherId)) {
                throw new InvalidCourseDataException("Invalid data provided for updating course.");
            }

            // Update the course information
            String updateCourseQuery = "UPDATE courses SET course_name=?, credits=?, teacher_id=? WHERE course_id=?";
            try (PreparedStatement updateCourseStatement = connection.prepareStatement(updateCourseQuery)) {
                updateCourseStatement.setString(1, courseName);
                updateCourseStatement.setInt(2, credits);
                updateCourseStatement.setLong(3, teacherId);
                updateCourseStatement.setLong(4, course.getCourseId());
                int rowsAffected = updateCourseStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Course information updated successfully!");
                } else {
                    throw new CourseNotFoundException("Course not found for the given course ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    

    private boolean isInvalidCourseData(String courseName, int credits, long teacherId) {
        // Validate that courseName is not empty and credits is a positive integer
        return courseName == null || courseName.trim().isEmpty() || credits <= 0;
    }
    public void displayCourseInfo(Course course) throws FileNotFoundException, ClassNotFoundException, IOException, CourseNotFoundException {
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
                        throw new CourseNotFoundException("Course not found for the given course ID.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<String> getEnrolledStudentNames(Course course) throws FileNotFoundException, ClassNotFoundException, IOException, CourseNotFoundException, StudentNotFoundException {
        List<String> enrolledStudentNames = new ArrayList<>();
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            String selectQuery = "SELECT students.first_name, students.last_name " +
                    "FROM enrollments " +
                    "JOIN students ON enrollments.student_id = students.student_id " +
                    "WHERE enrollments.course_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, course.getCourseId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (!resultSet.isBeforeFirst()) {
                        throw new CourseNotFoundException("Course not found for the given course ID.");
                    }
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
        if (enrolledStudentNames.isEmpty()) {
            throw new StudentNotFoundException("No students found for the given course ID.");
        }
        return enrolledStudentNames;
    }
    public Teacher getAssignedTeachersNames(Course course) throws FileNotFoundException, ClassNotFoundException, IOException,  CourseNotFoundException {
        Teacher teacher = null;
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            String selectQuery = "SELECT teacher.teacher_id,teacher.first_name,teacher.last_name FROM teacher " +
                    "JOIN courses ON courses.teacher_id = teacher.teacher_id " +
                    "WHERE courses.course_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, course.getCourseId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (!resultSet.isBeforeFirst()) {
                        throw new CourseNotFoundException("Course not found for the given course ID.");
                    }
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
        if (teacher == null) {
            System.out.println("No teacher assigned for the given course ID.");
        }
        return teacher;
    }


    public static void main(String[] args) throws ClassNotFoundException, CourseNotFoundException, IOException {
        CourseDaoImpl courseClassException = new CourseDaoImpl();
        // Assuming you have valid Course and Teacher objects
        /*
        Course course = new Course(1008l, "Biology",10);
        Teacher teacher = new Teacher(108l);

        try {
            // Test 1: Assign teacher to the course
            courseClassException.assignTeacher(course, teacher);

            // Test 2: Try to assign the same teacher to the same course (should print "Teacher is already assigned...")
            courseClassException.assignTeacher(course, teacher);

            // Test 3: Try to assign a non-existing teacher (should throw TeacherNotFoundException)
          courseClassException.assignTeacher(course, new Teacher(999));

            // Test 4: Try to assign to a non-existing course (should throw CourseNotFoundException)
            courseClassException.assignTeacher(new Course(999, "NonExistentCourse",7), teacher);
        } catch (ClassNotFoundException | IOException | TeacherNotFoundException | CourseNotFoundException e) {
            System.out.println(e.getMessage());
        }
        
     // Assuming you have a valid Course object with a valid course ID
        Course validCourse = new Course(1001);

        try {
            // Test 1: Update course info with valid data
            courseClassException.updateCourseInfo(validCourse, "Math", 10, 101);
            System.out.println("Test 1 passed: Course info updated successfully!");

            // Test 2: Update course info with invalid data (empty course name)
            courseClassException.updateCourseInfo(validCourse, "", 3, 102);
            System.out.println("Test 2 failed: Exception not thrown for empty course name.");

        } catch (ClassNotFoundException | IOException | CourseNotFoundException | InvalidCourseDataException e) {
            System.out.println("Test 2 passed: " + e.getMessage());
        }

        try {
            // Test 3: Update course info with invalid data (negative credits)
            courseClassException.updateCourseInfo(validCourse, "Chemistry", -1, 103);
            System.out.println("Test 3 failed: Exception not thrown for negative credits.");

        } catch (ClassNotFoundException | IOException | CourseNotFoundException | InvalidCourseDataException e) {
            System.out.println("Test 3 passed: " + e.getMessage());
        }
        
        // Display Course Exception
        try {
            // Test 1: Display course info with valid data
            courseClassException.displayCourseInfo(validCourse);
            System.out.println("Test 1 passed: Course info displayed successfully!");

            // Test 2: Display course info with invalid data (non-existent course ID)
            Course nonExistentCourse = new Course(999, "InvalidCourse", 0);
            courseClassException.displayCourseInfo(nonExistentCourse);
            System.out.println("Test 2 failed: Exception not thrown for non-existent course ID.");

        } catch (ClassNotFoundException | IOException | CourseNotFoundException e) {
            System.out.println("Test 2 passed: " + e.getMessage());
        }
        
        //Enrolled student list
        
        try {
        	Course course1=new Course(1001);
            // Test 1: Get enrolled student names with valid data
            List<String> enrolledStudents = courseClassException.getEnrolledStudentNames(course1);
            if (!enrolledStudents.isEmpty()) {
                System.out.println("Test 1 passed: Enrolled students - " + String.join(", ", enrolledStudents));
            } else {
                System.out.println("Test 1 failed: No enrolled students found.");
            }

            // Test 2: Get enrolled student names with invalid data (non-existent course ID)
            Course nonExistentCourse = new Course(999, "InvalidCourse", 0);
            courseClassException.getEnrolledStudentNames(nonExistentCourse);
            System.out.println("Test 2 failed: Exception not thrown for non-existent course ID.");

        } catch (ClassNotFoundException | IOException | CourseNotFoundException | StudentNotFoundException e) {
            System.out.println("Test 2 passed: " + e.getMessage());
        }
        // teacher method
        
        try {
            // Test 1: Get assigned teacher with valid data
            Teacher assignedTeacher = courseClassException.getAssignedTeachersNames(validCourse);
            if (assignedTeacher != null) {
                System.out.println("Test 1 passed: Assigned teacher - " + assignedTeacher.getFirstName() + " " + assignedTeacher.getLastName());
            } else {
                System.out.println("Test 1 failed: No assigned teacher found.");
            }

            // Test 2: Get assigned teacher with invalid data (non-existent course ID)
            Course nonExistentCourse = new Course(999, "InvalidCourse", 0);
            try {
                courseClassException.getAssignedTeachersNames(nonExistentCourse);
                System.out.println("Test 2 failed: Exception not thrown for non-existent course ID.");
            } catch (CourseNotFoundException e) {
                System.out.println("Test 2 passed: " + e.getMessage());
            } 

        } catch (ClassNotFoundException | IOException |  CourseNotFoundException e) {
            e.printStackTrace();
        }*/
        Course course60 = new Course("Math");
        long courseId=courseClassException.getCourseIdByCourseName(course60);
        System.out.println(courseId);
    }
}
