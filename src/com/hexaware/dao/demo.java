package com.hexaware.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.hexaware.entity.Student;
import com.hexaware.util.DBConnUtil;
import com.hexaware.util.DBPropertyUtil;

public class demo {
    public static void main(String args[]) throws FileNotFoundException, SQLException, IOException, ClassNotFoundException {
        Date date = new Date();
        Student student = new Student("p", "Bala", date, "psgjsjs@gmail.com", 9798665234L);
        ss s = new ss();
        String email = "psgjsjs@gmail.com";
        s.DisplayStudentInfo(email);
    }
}

class ss {
    static String fileName = "src/com/hexaware/util/db.properties";
    String connectionString = DBPropertyUtil.getConnectionString(fileName);

    public void DisplayStudentInfo(String email) throws SQLException, FileNotFoundException, IOException, ClassNotFoundException {
        try (Connection connection = DBConnUtil.getConnection(connectionString)) {
            String sql = "SELECT first_name, last_name, date_of_birth, phone_number FROM Students WHERE email = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        Date dateOfBirth = resultSet.getDate("date_of_birth");
                        long phoneNumber = resultSet.getLong("phone_number");

                        // Create a Student object with the retrieved information
                        Student retrievedStudent = new Student(firstName, lastName, dateOfBirth, email, phoneNumber);

                        // Print the details of the retrieved student
                        System.out.println("Retrieved Student Information:");
                        System.out.println("First Name: " + retrievedStudent.getFirstName());
                        System.out.println("Last Name: " + retrievedStudent.getLastName());
                        System.out.println("Date of Birth: " + retrievedStudent.getDateOfBirth());
                        System.out.println("Email: " + retrievedStudent.getEmail());
                        System.out.println("Phone Number: " + retrievedStudent.getPhoneNumber());
                    }
                }
            }
        }
    }
}
